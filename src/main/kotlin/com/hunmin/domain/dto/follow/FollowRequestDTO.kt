package com.hunmin.domain.dto.follow

import com.hunmin.domain.entity.FollowStatus
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class FollowRequestDTO (
    @NotNull( message = " 팔로우 아이디는 필수입니다.")
    var followId: Long? = null,
    @NotNull( message = " 팔로워 아이디는 필수입니다.")
    var followerId: Long? = null,
    @NotNull( message = " 팔로이 아이디는 필수입니다.")
    var followeeId: Long? = null,
    @NotNull( message = " 차단설정은 필수입니다.")
    var isBlock: Boolean? = null,
    @NotNull( message = " 알림설정은 필수입니다.")
    var notification: Boolean? = null,
    var createdAt: LocalDateTime? = null,
    @NotNull( message = " 팔로우 상태는 필수입니다.")
    var status: FollowStatus? = null,
    // 팔로워
    @NotNull( message = " 팔로워 닉네임은 필수입니다.")
    var followerName: String? = null,
    @NotNull( message = " 팔로워 이메일 필수입니다.")
    var followerEmail: String? = null,
    @NotNull( message = " 팔로우 이름는 필수입니다.")
    var followerImage: String? = null,
    // 팔로이
    @NotNull( message = " 팔로이 닉네임은 필수입니다.")
    var followeeEmail: String? = null,
    @NotNull( message = " 팔로이 이메일 필수입니다.")
    var followeeImage: String? = null,
    @NotNull( message = " 팔로이 이름는 필수입니다.")
    var followeeName: String? = null
){
    constructor(followRequestDTO: FollowRequestDTO) : this(
        followRequestDTO.followId,
        followRequestDTO.followerId,
        followRequestDTO.followeeId,
        followRequestDTO.isBlock,
        followRequestDTO.notification,
        followRequestDTO.createdAt,
        followRequestDTO.status,

        followRequestDTO.followerName,
        followRequestDTO.followerEmail,
        followRequestDTO.followerImage,

        followRequestDTO.followeeEmail,
        followRequestDTO.followeeImage,
        followRequestDTO.followeeImage

    )
}
