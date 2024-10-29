package com.hunmin.domain.dto.follow

import com.hunmin.domain.entity.FollowStatus
import java.time.LocalDateTime

data class FollowRequestDTO (
    var followId: Long? = null,
    var followerId: Long? = null,
    var followeeId: Long? = null,
    var isBlock: Boolean? = null,
    var notification: Boolean? = null,
    var createdAt: LocalDateTime? = null,
    var status: FollowStatus? = null,
    // 팔로워
    var followerName: String? = null,
    var followerEmail: String? = null,
    var followerImage: String? = null,
    // 팔로이
    var followeeEmail: String? = null,
    var followeeImage: String? = null,
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
