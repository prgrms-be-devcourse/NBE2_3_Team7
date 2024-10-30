package com.hunmin.domain.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hunmin.domain.dto.chat.ChatRoomDTO
import com.hunmin.domain.dto.chat.ChatRoomRequestDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.entity.ChatRoom
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.exception.chat.ChatRoomException
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.repository.ChatRoomRepository
import com.hunmin.domain.repository.MemberRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.redis.core.HashOperations
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException

@Service
@Transactional
class ChatRoomService(
    private val roomStorage: HashOperations<String, String, Any>,
    private val objectMapper: ObjectMapper,
    private val chatRoomRepository: ChatRoomRepository,
    private val memberRepository: MemberRepository,
    private val notificationService: NotificationService,
    private val sseEmitters: SseEmitters
) {
    // 단일 채팅방 조회
    fun findRoomById(id: Long): ChatRoomDTO {
        val chatRoom: ChatRoom = chatRoomRepository.findById(id).orElse(null)
            ?: throw ChatRoomException.NOT_FOUND.get()
        return ChatRoomDTO(chatRoom)
    }

    //관련 채팅방 조회
    fun findRoomByEmail(email: String): MutableList<ChatRoomRequestDTO> {
        try {

            val me: Member = memberRepository.findByEmail(email)

            val partnerNameAndChatRoom: MutableList<Any> = roomStorage.values(me.nickname)

            val chatRoomIds: MutableSet<Long> = HashSet()
            val chatRoomRequestDTOList: MutableList<ChatRoomRequestDTO> = ArrayList<ChatRoomRequestDTO>()

            for (chatRoomRequestDTO in partnerNameAndChatRoom) {
                val chatRoomRequest: ChatRoomRequestDTO =
                    objectMapper.convertValue(chatRoomRequestDTO, ChatRoomRequestDTO::class.java)
                chatRoomIds.add(chatRoomRequest.chatRoomId)
                chatRoomRequestDTOList.add(chatRoomRequest)
            }

            val allMembers: List<Member> = memberRepository.findAll()
            for (member in allMembers) {
                val rawChatRoom = roomStorage.get(member.nickname, me.nickname)
                val chatRoomRequestDTO =
                    objectMapper.convertValue(rawChatRoom, ChatRoomRequestDTO::class.java)
                chatRoomRequestDTO.let {
                    if (!chatRoomIds.contains(it.chatRoomId)) {
                        chatRoomIds.add(it.chatRoomId)
                        chatRoomRequestDTOList.add(it)
                    }
                }
            }
            return chatRoomRequestDTOList
        } catch (e: Exception) {
            log.error("채팅방 불러오기에 실패하였습니다")
            throw ChatRoomException.FAILED_READ_ROOMS.get()
        }
    }


    // 채팅방 생성
    fun createChatRoomByNickName(partnerName: String, myEmail: String): ChatRoomRequestDTO {
        try {
            var byNickname = memberRepository.findByNickname(partnerName)

            val me = memberRepository.findByEmail(myEmail)
            val myNickname = me.nickname

            if (roomStorage.get(myNickname, partnerName) != null || roomStorage.get(
                    partnerName,
                    myNickname
                ) != null
            ) {
                throw ChatRoomException.CHATROOM_ALREADY_EXIST.get()
            }
            val chatRoom: ChatRoom = ChatRoom(member = me).apply {}
            val SavedchatRoom: ChatRoom = chatRoomRepository.save(chatRoom)
            val chatRoomRequestDTO: ChatRoomRequestDTO = ChatRoomRequestDTO(
                chatRoomId = SavedchatRoom.chatRoomId, memberId = me.memberId, nickName = me.nickname,
                partnerName = partnerName,
                createdAt = SavedchatRoom.createdAt
            )
            roomStorage.put(me.nickname, partnerName, chatRoomRequestDTO)

            val partner: Member =
                memberRepository.findByNickname(partnerName)

            val partnerId = partner.memberId

            val notificationSendDTO = NotificationSendDTO(
                message = "[" + me.nickname + "]님이 새로운 채팅방을 개설",
                notificationType = NotificationType.CHAT,
                url = "/chat-room/" + SavedchatRoom.chatRoomId
            ).apply {
                memberId = partnerId
            }

            notificationService.send(notificationSendDTO)

            val emitterId = partnerId.toString() + "_"
            val emitter = sseEmitters.findSingleEmitter(emitterId)

            if (emitter != null) {
                try {
                    emitter.send(ChatRoomDTO(SavedchatRoom))
                } catch (e: IOException) {
                    log.error("Error sending chat room notification to client via SSE: {}", e)
                    sseEmitters.delete(emitterId)
                }
            }
            return ChatRoomRequestDTO(
                chatRoomId = SavedchatRoom.chatRoomId,
                memberId = me.memberId, nickName = me.nickname,
                partnerName = partnerName,
                createdAt = SavedchatRoom.createdAt
            )
        } catch (e: Exception) {
            throw NoSuchElementException("사용자가 존재하지 않습니다")
        }
    }

    // 채팅방 삭제
    fun deleteChatRoom(chatRoomId: Long, partnerName: String, meEmail: String): Boolean {
        try {
            val me = memberRepository.findByEmail(meEmail)
            val chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(ChatRoomException.NOT_FOUND::get)

            if (roomStorage.get(me.nickname, partnerName) != null) {
                roomStorage.delete(me.nickname, partnerName)
            } else if (roomStorage.get(partnerName, me.nickname) != null) {
                roomStorage.delete(partnerName, me.nickname)
            } else return false
            chatRoomRepository.delete(chatRoom)
            return true
        } catch (e: Exception) {
            throw NoSuchElementException("사용자가 존재하지 않습니다")
        }

    }
}
