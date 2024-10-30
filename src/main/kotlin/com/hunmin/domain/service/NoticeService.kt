package com.hunmin.domain.service

import com.hunmin.domain.dto.notice.NoticePageRequestDTO
import com.hunmin.domain.dto.notice.NoticeRequestDTO
import com.hunmin.domain.dto.notice.NoticeResponseDTO
import com.hunmin.domain.dto.notice.NoticeUpdateDTO
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.entity.Notice
import com.hunmin.domain.exception.MemberException
import com.hunmin.domain.exception.NoticeException
import com.hunmin.domain.exception.NoticeTaskException
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.repository.NoticeRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Function
import java.util.function.Supplier

@Service
@Transactional
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val memberRepository: MemberRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    //공지사항 리스트 조회
    fun getAllNotices(pageRequestDTO: NoticePageRequestDTO): Page<NoticeResponseDTO> {
        return runCatching {
            val sort = Sort.by("noticeId").descending()
            val pageable: Pageable = pageRequestDTO.getPageable(sort)
            val noticePage: Page<Notice> = noticeRepository.findAllNotices(pageable)
            noticeRepository.findAllNotices(pageable).map { NoticeResponseDTO(it) }
        }.getOrElse {
            log.error("getAllNotices error: ${it.message}")
            throw NoticeException.NOTICE_NOT_FOUND.get()
        }
    }

    //공지사항 조회
    fun getNoticeById(noticeId: Long): NoticeResponseDTO {
        val notice = noticeRepository.findById(noticeId)
            .orElseThrow { NoticeException.NOTICE_NOT_FOUND.get() }
        return NoticeResponseDTO(notice)
    }

    //공지사항 등록
    fun createNotice(noticeRequestDTO: NoticeRequestDTO, username: String): NoticeResponseDTO {
        val member = getMember(username)

        //관리자 아닐경우 예외 발생
        if (member.memberRole != MemberRole.ADMIN) {
            throw NoticeException.MEMBER_NOT_VALID.get()
        }
        return runCatching {
            val notice: Notice = noticeRequestDTO.toEntity(member)
            val savedNotice: Notice = noticeRepository.save<Notice>(notice)
            log.info("Notice created successfully. Notice ID: {}", savedNotice.noticeId)
            NoticeResponseDTO(savedNotice)
        }.getOrElse { e ->
            log.error("createNotice error: {}", e.message)
            throw NoticeException.NOTICE_NOT_CREATED.get()
        }
    }

    //공지사항 수정
    fun updateNotice(noticeUpdateDTO: NoticeUpdateDTO, username: String, noticeId: Long): NoticeResponseDTO {
        val member = getMember(username)
        //관리자 아닐경우 예외 발생
        if (member.memberRole != MemberRole.ADMIN) {
            throw NoticeException.MEMBER_NOT_VALID.get()
        }

        val notice = noticeRepository.findById(noticeId)
            .orElseThrow { NoticeException.NOTICE_NOT_FOUND.get() }

        return runCatching {
            notice.apply {
                changeTitle(noticeUpdateDTO.title)
                changeContent(noticeUpdateDTO.content)
                changeMember(member) //수정한 관리자에 대한 정보 반영
            }
            NoticeResponseDTO(notice)
        } .getOrElse {e ->
            log.error("updateNotice error: {}", e.message)
            throw NoticeException.NOTICE_NOT_UPDATED.get()
        }
    }


    //공지사항 삭제
    fun deleteNotice(noticeId: Long, username: String): Boolean {
        val member = getMember(username)
        //관리자 아닐경우 예외 발생
        if (member.memberRole != MemberRole.ADMIN) {
            throw NoticeException.MEMBER_NOT_VALID.get()
        }
       noticeRepository.findById(noticeId)
            .orElseThrow { NoticeException.NOTICE_NOT_FOUND.get() }
        return runCatching {
            noticeRepository.deleteById(noticeId)
            true
        }.getOrElse { e->
            log.error("deleteNotice error: {}", e.message)
            throw NoticeException.NOTICE_NOT_DELETED.get()
        }
    }

    private fun getMember(username: String): Member {
        val member: Member = memberRepository.findByEmail(username)
            ?: throw MemberException.NOT_FOUND.get()
        return member
    }
}