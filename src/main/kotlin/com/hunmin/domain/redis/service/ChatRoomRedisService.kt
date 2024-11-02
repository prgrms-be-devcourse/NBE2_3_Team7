package com.hunmin.domain.redis.service

import com.hunmin.domain.dto.chat.ChatRoomRequestDTO
import com.hunmin.domain.dto.member.MemberDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.entity.ChatRoom
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.redis.entity.ChatRoomRedis
import com.hunmin.domain.redis.repository.ChatRoomRedisRepository
import com.hunmin.domain.repository.ChatRoomRepository
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.service.NotificationService
import io.jsonwebtoken.io.DeserializationException
import io.lettuce.core.RedisCommandTimeoutException
import org.hibernate.TransactionException
import org.hibernate.query.sqm.tree.SqmNode.log
import org.modelmapper.ModelMapper
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException

@Service
@Transactional
class ChatRoomRedisService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val memberRepository: MemberRepository,
    private val notificationService: NotificationService,
    private val sseEmitters: SseEmitters,
    private val modelMapper: ModelMapper,
    private val chatRoomRedisRepository: ChatRoomRedisRepository,
    private val chatRoomRepository: ChatRoomRepository
) {
    // 채팅방 생성
    fun createChatRoomByNickName(partnerName: String, myEmail: String): ChatRoomRequestDTO {
        try {
            val me: Member = memberRepository.findByEmail(myEmail)

            var memberDTO = MemberDTO(
                memberId = me.memberId
                ,email=me.email,
                nickname=me.nickname,
                level = me.level,
                password = me.password,
                country = me.country,
                image = me.image
            )

            // redis로 id값 자동 증가시키기
            val increasedId= redisTemplate.opsForValue().increment("chatRoomRedisId")!!

            // StackOverFlow 해결 =memberDTO사용
            val chatRoom: ChatRoomRedis = ChatRoomRedis(id= increasedId , member = memberDTO)

            // redis에 저장
            val save = chatRoomRedisRepository.save(chatRoom)

            // 캐싱전략 설계 10개 10개 이상이면 -> DB저장
            val allChatRooms = chatRoomRedisRepository.findAll()
            if ((increasedId % 10).toInt() == 0){
                for (chatRooms in allChatRooms){
                    // 새로운 ChatRoom 생성
                    val newMember = modelMapper.map(chatRooms.member,Member::class.java)
                    val newChatRoom = ChatRoom(
                        chatRoomId = chatRooms.id,
                        member = newMember,
                        chatMessage = chatRooms.chatMessage,
                        userCount = chatRooms.userCount
                    )
                    // DB저장
                    val chatRoomRepository1 = chatRoomRepository.save(newChatRoom)
                }
                // redis 저장소 비우기
                chatRoomRedisRepository.deleteAll()
            }
            //알림
            val partner = memberRepository.findByNickname(partnerName)
            val partnerId = partner.memberId

            val notificationSendDTO = NotificationSendDTO(
                message = "[" + me.nickname + "]님이 ${partner}님을 채팅방에 초대하였습니다.",
                notificationType = NotificationType.CHAT,
                url = "/chat-room/" + chatRoom.id

            ).apply {
                memberId = partnerId
            }

            notificationService.send(notificationSendDTO)

            val emitterId = partnerId.toString() + "_"
            val emitter = sseEmitters.findSingleEmitter(emitterId)

            if (emitter != null) {
                try {
                    emitter.send(chatRoom)
                } catch (e: IOException) {
                    log.error("Error sending chat room notification to client via SSE: {}", e)
                    sseEmitters.delete(emitterId)
                }
            }
            return ChatRoomRequestDTO(
                chatRoomId = chatRoom.id,
                memberId = me.memberId, nickName = me.nickname,
                partnerName = partnerName,
                createdAt = chatRoom.createdAt
            )
        } catch (e: RedisConnectionFailureException) {
            throw RedisConnectionFailureException("레디스 연결문제 ${e.message}")
        }catch (e: RedisCommandTimeoutException) {
            throw RedisCommandTimeoutException("레디스 서버 과부하 ${e.message}")
        }catch (e: DeserializationException) {
            throw DeserializationException("역직렬화 문제 ${e.message}")
        }catch (e: OutOfMemoryError) {
            throw OutOfMemoryError("기본 명령실패 ${e.message}")
        }catch (e: TransactionException) {
            throw TransactionException("트랜젝션 사용으로 인한 문제 ${e.message}")
        }catch (e: Exception) {
            throw Exception("채팅룸 만들기 실패 ${e.message}")
        }
    }
}