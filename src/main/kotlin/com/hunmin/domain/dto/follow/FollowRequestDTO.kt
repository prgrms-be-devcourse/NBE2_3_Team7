package com.hunmin.domain.dto.follow

import com.hunmin.domain.entity.Follow
import com.hunmin.domain.entity.FollowStatus
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class FollowRequestDTO (
    @NotNull( message = " 팔로우 아이디는 필수입니다.")
    val followId: Long,
    @NotNull( message = " 팔로워 아이디는 필수입니다.")
    val followerId: Long,
    @NotNull( message = " 팔로이 아이디는 필수입니다.")
    val followeeId: Long,
    @NotNull( message = " 차단설정은 필수입니다.")
    val isBlock: Boolean,
    @NotNull( message = " 알림설정은 필수입니다.")
    val notification: Boolean,
    val createdAt: LocalDateTime? = null,
    @NotNull( message = " 팔로우 상태는 필수입니다.")
    val status: FollowStatus,
    // 팔로워
    @NotNull( message = " 팔로워 닉네임은 필수입니다.")
    val followerName: String,
    @NotNull( message = " 팔로워 이메일 필수입니다.")
    val followerEmail: String,
    val followerImage: String? = null,
    // 팔로이
    @NotNull( message = " 팔로이 이름는 필수입니다.")
    val followeeName: String,
    @NotNull( message = " 팔로이 이메일 필수입니다.")
    val followeeEmail: String,
    val followeeImage: String? = null

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
        follow.followee!!.nickname,
        follow.followee!!.email,
        follow.followee?.image
    )

}
