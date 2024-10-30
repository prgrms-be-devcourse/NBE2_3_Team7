package com.hunmin.domain.service

import com.hunmin.domain.dto.follow.FollowRequestDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.Follow
import com.hunmin.domain.entity.FollowStatus
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.exception.follow.FollowException
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.repository.FollowRepository
import com.hunmin.domain.repository.MemberRepository
import jdk.internal.joptsimple.internal.Messages.message
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.*

@Service
@Transactional
class FollowService(
    private val memberRepository: MemberRepository,
    private val followRepository: FollowRepository,
    private val notificationService: NotificationService,
    private val sseEmitters: SseEmitters
) {
    // 팔로워 등록
    fun register(myEmail: String, memberId: Long): FollowRequestDTO {
        try {
            var followee: Member = memberRepository.findById(memberId).orElseThrow()
            val owner: Member = memberRepository.findByEmail(myEmail)

            if (followee.memberId == owner.memberId) {
                throw FollowException.IMPOSSIBLE_FOLLOW.get()
            }

            // 중복체크
            val foundMember = followRepository.findByMemberId(owner.memberId, memberId)

            if (foundMember.isPresent) {
                throw FollowException.DUPLICATED_FOLLOW.get()
            }

            val follow: Follow = Follow().apply {
                this.follower = owner
                this.followee = followee
            }

            // 알림
            val receiverId = followee.memberId

            val notificationSendDTO: NotificationSendDTO = NotificationSendDTO(
                message = owner.nickname + "님이 팔로우 요청을 보냈습니다.",
                notificationType = NotificationType.FOLLOW,
                url = "/follow"
            ).apply {
                this.memberId = receiverId
            }
            notificationService.send(notificationSendDTO)

            val emitterId = receiverId.toString() + "_"
            val emitter = sseEmitters.findSingleEmitter(emitterId)


            if (emitter != null) {
                try {
                    emitter.send(notificationSendDTO)
                } catch (e: IOException) {
                    log.error("Error sending comment to client via SSE:")
                    sseEmitters.delete(emitterId)
                }
            }
            return FollowRequestDTO(followRepository.save(follow))
        } catch (e: RuntimeException) {
            log.error("팔로우 등록 실패")
            throw FollowException.FAILED_REGISTER_FOLLOW.get()
        }
    }

    @Transactional // 팔로이 수락
    fun registerAccept(myEmail: String, memberId: Long): FollowRequestDTO {
        try {
            val followee: Member = memberRepository.findById(memberId).get()
            val owner: Member = memberRepository.findByEmail(myEmail)

            // 중복체크
            val foundMember: Optional<Follow> = followRepository.findByMemberId(owner.memberId, memberId)
            if (foundMember.isPresent()) {
                throw FollowException.DUPLICATED_FOLLOW.get()
            }

            val follow: Follow = Follow().apply {
                this.follower = owner
                this.followee = followee
            }
            followRepository.save(follow)
            val follower = followRepository.findByMemberId(memberId, owner.memberId).get()
            follower.status = FollowStatus.ACCEPTED
            followRepository.save(follower)

            return FollowRequestDTO(followRepository.save(follow))
        } catch (e: RuntimeException) {
            log.error("팔로우 수락 실패")
            throw FollowException.FAILED_ACCEPT_FOLLOW.get()
        }
    }

    // 팔로이 삭제
    fun remove(myEmail: String, memberId: Long): Boolean {
        try {
            val owner = memberRepository.findByEmail(myEmail)
            val foundMember = followRepository.findByMemberId(owner.memberId, memberId).get()

            followRepository.deleteById(foundMember.followId)
            return true
        } catch (e: RuntimeException) {
            log.error("팔로이 삭제 실패")
            throw FollowException.NOT_FOUND.get()
        }
    }

    // 팔로이 리스트 조회
    fun readPage(pageRequestDTO: PageRequestDTO, email: String): Page<FollowRequestDTO> {
        try {
            val member = memberRepository.findByEmail(email)
            val sort = Sort.by("followId").descending()
            val pageable = pageRequestDTO.getPageable(sort)
            return followRepository.getFollowPage(member.memberId, pageable)
        } catch (e: RuntimeException) {
            log.error("페이징 실패")
            throw FollowException.NOT_FOUND.get()
        }
    }

    // 알림 변경
    @Transactional
    fun turnNotification(myEmail: String, memberId: Long): Boolean {
        try {
            val owner = memberRepository.findByEmail(myEmail)
            val foundMember: Follow = followRepository.findByMemberId(memberId, owner.memberId).get()


            foundMember.notification = !foundMember.notification
            return true
        } catch (e: RuntimeException) {
            log.error("알림 변경 실패")
            throw FollowException.NOT_FOUND.get()
        }
    }

    // 차단 상태 변경
    @Transactional
    fun blockFollower(myEmail: String, memberId: Long): Boolean {
        try {
            val owner = memberRepository.findByEmail(myEmail)
            val foundMember = followRepository.findByMemberId(memberId, owner.memberId).get()

            foundMember.notification = foundMember.isBlock
            foundMember.isBlock = !foundMember.isBlock
            return true
        } catch (e: RuntimeException) {
            log.error("상대방 차단에 실패하였습니다. {}")
            throw FollowException.NOT_FOUND.get()
        }
    }

    fun isFollowing(memberId: Long, followeeId: Long): Boolean {
        return followRepository.existsByFollowerMemberIdAndFolloweeMemberId(memberId, followeeId)
    }
}
