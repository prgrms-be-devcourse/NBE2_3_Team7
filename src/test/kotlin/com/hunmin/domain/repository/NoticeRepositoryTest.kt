package com.hunmin.domain.repository

import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.entity.Notice
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class NoticeRepositoryTest {

    @Autowired
    private lateinit var noticeRepository: NoticeRepository
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @BeforeEach
    fun setup() {
        // 테스트용 Member 객체 생성
        val member1 = Member.create(
            nickname = "member1",
            email = "member1@example.com",
            password = "password",
            country = "Country1",
            level = MemberLevel.BEGINNER
        )

        val member2 = Member.create(
            nickname = "member2",
            email = "member2@example.com",
            password = "password",
            country = "Country2",
            level = MemberLevel.BEGINNER
        )
        memberRepository.save(member1)
        memberRepository.save(member2)

        // 테스트용 Notice 객체 생성
        for (i in 1..10) {
            val notice = Notice.create(
                title = "Notice $i",
                content = "Content $i",
                member = member1
            )
            noticeRepository.save(notice)
        }
        // 데이터베이스에 저장
        val notice1 = Notice.create(title = "멤버 2의 공지사항", content = "Content 1", member = member2)
        val notice2 = Notice.create(title = "멤버 2의 공지사항2", content = "Content 2", member = member2)
        val notice3 = Notice.create(title = "멤버 2의 공지사항3", content = "Content 3", member = member2)
        noticeRepository.save(notice1)
        noticeRepository.save(notice2)
        noticeRepository.save(notice3)
    }

    @Test
    fun testFindAllNotices() {
        // Given:


        // When: Pageable 객체를 사용하여 findAllNotices 호출
        val pageable: Pageable = PageRequest.of(0, 10)
        val result = noticeRepository.findAllNotices(pageable)

        // Then: 결과 검증
        assertThat(result).isNotNull
        assertThat(result.totalElements).isEqualTo(13)
        assertThat(result.content).extracting("title").containsExactly(
            "Notice 1", "Notice 2", "Notice 3", "Notice 4", "Notice 5",
            "Notice 6", "Notice 7", "Notice 8", "Notice 9", "Notice 10"
        ) // 첫 페이지의 공지사항 제목
    }
}