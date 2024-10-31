package com.hunmin.domain.service

import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.repository.MemberRepository
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    private fun createTestUser() {
        val countries = listOf("South Korea", "Japan", "China", "USA", "UK", "Sweden", "Germany")

        for (i in 1..10) {
            val member = Member(
                email = "user$i@email.com",
                password = "password$i",
                nickname = "nickName$i",
                country = countries[i % countries.size],
                memberRole = MemberRole.USER,
                level = MemberLevel.entries.toTypedArray().random(),
                image = "profile$i.jpg"
            )
            memberRepository.save(member)
        }
    }

    @Test
    fun registerProcess() {
    }

    @Test
    fun updateMember() {
    }

    @Test
    @DisplayName("이메일로 사용자 정보 검색")
    fun readUserInfo() {
        createTestUser()

        val result = memberService.readUserInfo("user3@email.com")

        assertEquals("user3@email.com", result.email)
        assertEquals("nickName3", result.nickname)
    }

    @Test
    fun verifyUserForPasswordReset() {
    }

    @Test
    fun updatePassword() {
    }
}