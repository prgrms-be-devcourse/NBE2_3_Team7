package com.hunmin.domain.repository

import com.hunmin.domain.entity.Follow
import com.hunmin.domain.entity.FollowStatus
import com.hunmin.domain.entity.MemberLevel
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class FollowRepositoryTest {
    @Autowired
    lateinit var memberRepository: MemberRepository
    @Autowired
    lateinit var followRepository: FollowRepository

    @Test
    fun 팔로우추가() {
        //given
        val follow = Follow().apply {
            follower = memberRepository.findById(1).get()
            followee = memberRepository.findById(2).get()
        }
        //when
        val savedFollow = followRepository.save(follow)
        //then
        assertNotNull(savedFollow)
    }

    @Test
    fun 팔로우조회() {
        //when
        val foundFollow = followRepository.findById(1).get()
        //then
        assertNotNull(foundFollow)
        assertEquals(1, foundFollow.follower!!.memberId)
        assertEquals(2, foundFollow.followee!!.memberId)
        assertEquals("testMember1", foundFollow.follower!!.nickname)
        assertEquals("testMember2", foundFollow.followee!!.nickname)
        assertEquals("Korea", foundFollow.follower!!.country)
        assertEquals("Korea", foundFollow.followee!!.country)
        assertEquals("test1@test.com", foundFollow.follower!!.email)
        assertEquals("test2@test.com", foundFollow.followee!!.email)
        assertEquals("12341", foundFollow.follower!!.password)
        assertEquals("12342", foundFollow.followee!!.password)
        assertEquals(MemberLevel.BEGINNER, foundFollow.follower!!.level)
        assertEquals(MemberLevel.BEGINNER, foundFollow.followee!!.level)
    }
    @Test
    fun 팔로우수정() {
        //given
        var foundFollow = followRepository.findById(1).get()
        //when
        foundFollow.apply {
            follower = memberRepository.findById(2).get()
            followee = memberRepository.findById(3).get()
            isBlock = true;
            notification = false;
            status = FollowStatus.ACCEPTED
        }
        //then
        assertEquals(2, foundFollow.follower!!.memberId)
        assertEquals(3, foundFollow.followee!!.memberId)
        assertEquals(true, foundFollow.isBlock)
        assertEquals(false, foundFollow.notification)
        assertEquals(FollowStatus.ACCEPTED, foundFollow.status)
    }
    @Test
    fun 팔로우삭제() {
        //when
        followRepository.deleteById(1)
        var foundFollow = followRepository.findById(1)
        //then
        assertTrue(foundFollow.isEmpty)
    }

}