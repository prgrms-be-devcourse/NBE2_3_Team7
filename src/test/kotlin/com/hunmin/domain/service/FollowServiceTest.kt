package com.hunmin.domain.service

import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.exception.follow.FollowTaskException
import com.hunmin.domain.repository.FollowRepository
import com.hunmin.domain.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.*

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
@Transactional
class FollowServiceTest {

    @Autowired
    private lateinit var followRepository: FollowRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var followService: FollowService

    @Test
    fun 팔로우요청() {
        //when
        val newFollow = followService.register("test2@test.com", 8)
        //then
        assertNotNull(newFollow)
    }

    @Test
    fun 팔로우수락() {
        //given
        followService.register("test2@test.com", 8)
        //when
        val acceptFollow = followService.registerAccept("test8@test.com", 2)
        //then
        assertNotNull(acceptFollow)
    }

    @Test
    fun 팔로우삭제() {
        //when
        val result = followService.remove("test2@test.com", 3)
        val foundFollow = followRepository.findById(2)
        //then
        assertTrue(foundFollow.isEmpty)
        assertEquals(result, true)
    }

    @Test
    fun `팔로우삭제실패 없는 팔로우`() {
        //when
        val foundFollow = followRepository.findById(2)
        //then
        assertThrows<FollowTaskException> {
            followService.remove("test2@test.com", 8)
        }
        assertFalse(foundFollow.isEmpty)
    }
    @Test
    fun 팔로우리스트조회() {
        //when
        var pageRequestDTO = PageRequestDTO(page = 1, size = 10)
        val newFollow = followService.readPage(pageRequestDTO, "test1@test.com")
        //then
        assertNotNull(newFollow)
        assertThat(newFollow.totalPages).isEqualTo(0)
        assertThat(newFollow.totalElements).isEqualTo(0)
    }

    @Test
    fun 팔로우알림변경() {
        //given
        followService.register("test2@test.com", 8)
        val acceptFollow = followService.registerAccept("test8@test.com", 2)
        //when
        val newFollow = followService.turnNotification("test2@test.com", 8)
        val foundFollow = followRepository.findById(acceptFollow.followId).get()
        //then
        assertTrue(newFollow)
        assertEquals(foundFollow.notification,false)

    }

    @Test
    fun 팔로우차단상태변경() {
        //given
        followService.register("test2@test.com", 8)
        val acceptFollow = followService.registerAccept("test8@test.com", 2)
        //when
        val newFollow = followService.blockFollower("test2@test.com", 8)
        val foundFollow = followRepository.findById(acceptFollow.followId).get()
        //then
        assertTrue(newFollow)
        assertEquals(foundFollow.isBlock,true)
    }

    @Test
    fun 팔로우상태확인() {
        //when
        val newFollow = followService.isFollowing(2,3)
        //then
        assertNotNull(newFollow)
        assertTrue(newFollow)
    }
}