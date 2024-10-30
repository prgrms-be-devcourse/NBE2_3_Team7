package com.hunmin.domain.dto.follow

import com.hunmin.domain.entity.Follow
import com.hunmin.domain.entity.FollowStatus
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.QFollow.follow
import com.querydsl.core.types.Projections.constructor
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class FollowRequestDTO (
    @NotNull( message = " 팔로우 아이디는 필수입니다.")
    var followId: Long,
    @NotNull( message = " 팔로워 아이디는 필수입니다.")
    var followerId: Long,
    @NotNull( message = " 팔로이 아이디는 필수입니다.")
    var followeeId: Long,
    @NotNull( message = " 차단설정은 필수입니다.")
    var isBlock: Boolean,
    @NotNull( message = " 알림설정은 필수입니다.")
    var notification: Boolean,
    var createdAt: LocalDateTime? = null,
    @NotNull( message = " 팔로우 상태는 필수입니다.")
    var status: FollowStatus,
    // 팔로워
    @NotNull( message = " 팔로워 닉네임은 필수입니다.")
    var followerName: String,
    @NotNull( message = " 팔로워 이메일 필수입니다.")
    var followerEmail: String,
    @NotNull( message = " 팔로우 이름는 필수입니다.")
    var followerImage: String? = null,
    // 팔로이
    @NotNull( message = " 팔로이 닉네임은 필수입니다.")
    var followeeEmail: String,
    @NotNull( message = " 팔로이 이메일 필수입니다.")
    var followeeImage: String? = null,
    @NotNull( message = " 팔로이 이름는 필수입니다.")
    var followeeName: String
){
    constructor(follow: Follow) : this(
        follow.followId,
        follow.follower!!.memberId,
        follow.followee!!.memberId,
        follow.isBlock,
        follow.notification,
        follow.createdAt,
        follow.status,

        // 팔로워 정보
        follow.follower!!.nickname,
        follow.follower!!.email,
        follow.follower?.image,

        // 팔로이 정보
        follow.followee!!.email ?: "Unknown",
        follow.followee?.image,
        follow.followee!!.nickname
    )

}
