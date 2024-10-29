package com.hunmin.domain.repository

import com.hunmin.domain.entity.Follow
import com.hunmin.domain.repository.search.FollowSearch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface FollowRepository : JpaRepository<Follow, Long>, FollowSearch {
    // 팔로우 유무 확인
    @Query("SELECT f FROM Follow f WHERE f.follower.memberId = :myId AND f.followee.memberId = :memberId")
    fun findByMemberId(
        @Param("myId") myId: Long,
        @Param("memberId") memberId: Long
    ): Optional<Follow>

    fun existsByFollowerMemberIdAndFolloweeMemberId(followerId: Long, followeeId: Long): Boolean
}
