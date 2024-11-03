package com.hunmin.domain.repository

import com.hunmin.domain.entity.Follow
import com.hunmin.domain.repository.search.FollowSearch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface FollowRepository : JpaRepository<Follow, Long>, FollowSearch {
    @Query("SELECT f FROM Follow f WHERE f.follower.memberId = :myId AND f.followee.memberId = :memberId")
    fun findByMemberId(
        @Param("myId") myId: Long,
        @Param("memberId") memberId: Long
    ): Optional<Follow>

    @Query("SELECT f FROM Follow f WHERE f.followee.memberId = :myId")
    fun findByOneMemberId(
        @Param("myId") myId: Long
    ): Optional<Follow>

    // 팔로우 유무 확인
    fun existsByFollowerMemberIdAndFolloweeMemberId(followerId: Long, followeeId: Long): Boolean
}
