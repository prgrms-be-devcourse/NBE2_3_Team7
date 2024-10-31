package com.hunmin.domain.service

import com.hunmin.domain.dto.notice.NoticePageRequestDTO
import com.hunmin.domain.dto.notice.NoticeRequestDTO
import com.hunmin.domain.dto.notice.NoticeUpdateDTO
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.entity.Notice
import com.hunmin.domain.exception.NoticeException
import com.hunmin.domain.exception.NoticeTaskException
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.repository.NoticeRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class NoticeServiceTest {

    @Autowired
    private lateinit var noticeService: NoticeService
    @Autowired
    private lateinit var noticeRepository: NoticeRepository
    @Autowired
    private lateinit var memberRepository: MemberRepository

    private val savedMembers = mutableListOf<Member>()
    private val savedNotices = mutableListOf<Notice>()

    @BeforeEach
    @DisplayName("더미데이터 설정")
    fun setUp() {
        (1..10).forEach { i ->
            val isAdmin = i <= 5
            val member = Member(
                memberRole = if (isAdmin) MemberRole.ADMIN else MemberRole.USER,
                nickname = if (isAdmin) "관리자이름$i" else "유저이름$i",
                country = if (isAdmin) "관리자국적$i" else "유저국적$i",
                email = if (isAdmin) "관리자이멜$i" else "유저이멜$i",
                image = if (isAdmin) "관리자아바타$i" else "유저아바타$i",
                level = MemberLevel.ADVANCED,
                password = if (isAdmin) "관리자비밀번호$i" else "유저비밀번호$i"
            )

            val savedMember = memberRepository.save(member)
            savedMembers.add(savedMember)

            if (isAdmin) {
                val notice = Notice(
                    title = "제목$i",
                    content = "내용$i",
                    member = savedMember
                )
                savedNotices.add(noticeRepository.save(notice))
            }
        }
    }

    @Test
    @DisplayName("공지 페이지 조회")
    fun getAllNotices(){
        //given
        val pageRequestDTO = NoticePageRequestDTO()
        // when
        val notices = noticeService.getAllNotices(pageRequestDTO)
        // then
        assertThat(notices).apply {
            isNotEmpty()
            hasSizeLessThanOrEqualTo(savedNotices.size)

        }
    }

    @Test
    @DisplayName("공지 조회")
    fun getNotice() {
        // given
        val notice = savedNotices.first()
        val noticeId = notice.noticeId

        // when
        val response = noticeService.getNoticeById(noticeId)

        // then
        assertThat(response).apply {
            isNotNull()
            extracting { it.noticeId }.isEqualTo(noticeId)
            extracting { it.content }.isEqualTo(notice.content)
        }
    }

    @Test
    @DisplayName("관리자가 공지사항 등록")
    fun createNoticeByUserAdmin() {
        // given
        val adminEmail = savedMembers[1].email
        val noticeRequest = NoticeRequestDTO(title = "공지사항", content = "공지내용")

        // when
        val response = noticeService.createNotice(noticeRequest, adminEmail)

        // then
        assertThat(response).isNotNull()
        assertThat(response.title).isEqualTo(noticeRequest.title)

        // then
        val savedNotice = noticeService.getNoticeById(response.noticeId)
        assertThat(savedNotice).apply {
            extracting { it.noticeId }.isEqualTo(response.noticeId)
            extracting { it.title }.isEqualTo(noticeRequest.title)
        }
    }

    @Test
    @DisplayName("유저가 공지사항 등록시 예외 발생")
    fun createNoticeByUser() {
        // given
        val userEmail = savedMembers[6].email
        val noticeRequest = NoticeRequestDTO(title = "공지제목", content = "공지내용")

        // when & then
        assertThatThrownBy {
            noticeService.createNotice(noticeRequest, userEmail)
        }.apply {
            isInstanceOf(NoticeTaskException::class.java)
            hasMessage(NoticeException.MEMBER_NOT_VALID.get().message)
        }
    }

    @Test
    @DisplayName("공지사항 수정")
    fun modifyNotice() {
        // given
        val adminEmail = savedMembers[1].email
        val noticeId = savedNotices[3].noticeId
        val updateRequest = NoticeUpdateDTO(
            title = "제목수정",
            content = "내용수정")

        // when
        val response = noticeService.updateNotice(updateRequest, adminEmail, noticeId)

        // then
        assertThat(response).apply {
            isNotNull()
            extracting { it.noticeId }.isEqualTo(noticeId)
            extracting { it.title }.isEqualTo(updateRequest.title)
            extracting { it.content }.isEqualTo(updateRequest.content)
        }

        // DB에서 꺼내서 확인
        val updatedNotice = noticeService.getNoticeById(response.noticeId)
        assertThat(updatedNotice).apply {
            extracting { it.title }.isEqualTo(updateRequest.title)
            extracting { it.content }.isEqualTo(updateRequest.content)
        }
    }

    @Test
    @DisplayName("유저가 공지사항 수정시 예외 발생")
    fun modifyNoticeByUser() {
        // given
        val userEmail = savedMembers[9].email
        val noticeId = savedNotices[4].noticeId
        val updateRequest = NoticeUpdateDTO(
            title = "제목수정",
            content = "내용수정")

        // when & then
        assertThatThrownBy {
            noticeService.updateNotice(updateRequest, userEmail, noticeId)
        }.apply {
            isInstanceOf(NoticeTaskException::class.java)
            hasMessage(NoticeException.MEMBER_NOT_VALID.get().message)
        }
    }

    @Test
    @DisplayName("관리자만 삭제 가능, 유저 삭제시 예외발생")
    fun deleteNoticeByUser() {
        // given
        val adminEmail = savedMembers[2].email
        val userEmail = savedMembers[8].email
        val noticeId = savedNotices[3].noticeId

        // when
        noticeService.deleteNotice(noticeId, adminEmail)

        // then
        assertThatThrownBy {
            noticeService.deleteNotice(noticeId, userEmail)
        }.apply {
            isInstanceOf(NoticeTaskException::class.java)
            hasMessage(NoticeException.MEMBER_NOT_VALID.get().message)
        }

        assertThat(noticeRepository.findById(noticeId)).isEmpty()
    }
}