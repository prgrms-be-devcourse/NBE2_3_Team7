package com.hunmin.domain.service

import com.hunmin.domain.dto.board.BoardRequestDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.Follow
import com.hunmin.domain.entity.FollowStatus
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.entity.QBoard.board
import com.hunmin.domain.exception.follow.FollowTaskException
import com.hunmin.domain.repository.FollowRepository
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.repository.NotificationRepository
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
    private lateinit var notificationService: NotificationService

    @Autowired
    private lateinit var notificationRepository: NotificationRepository

    @Autowired
    private lateinit var boardService: BoardService

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
        assertEquals(newFollow.followerEmail, "test2@test.com")
        assertEquals(newFollow.followeeId, 8)
    }

    @Test
    fun 팔로우수락() {
        //given
        followService.register("test2@test.com", 8)
        //when
        val acceptFollow = followService.registerAccept("test8@test.com", 2)
        //then
        assertNotNull(acceptFollow)
        assertEquals(acceptFollow.followerEmail, "test2@test.com")
        assertEquals(acceptFollow.followeeId, 8)
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
        //given
        val newFollow = followService.register("test2@test.com", 8)
        val acceptFollow = followService.registerAccept("test8@test.com", 2)
        //when
        var pageRequestDTO = PageRequestDTO(page = 1, size = 10)
        val newFollowList = followService.readPage(pageRequestDTO, "test2@test.com")
        //then
        assertNotNull(newFollowList)
        assertThat(newFollowList.totalPages).isEqualTo(1)
        assertThat(newFollowList.totalElements).isEqualTo(2)
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
        assertEquals(foundFollow.notification,true)

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
        assertEquals(foundFollow.isBlock,false)
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