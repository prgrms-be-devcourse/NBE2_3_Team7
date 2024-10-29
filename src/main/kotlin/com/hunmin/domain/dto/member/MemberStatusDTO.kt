package com.hunmin.domain.dto.member

import com.hunmin.domain.entity.Member

data class MemberStatusDTO(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val image: String?,
    val boardCount: Int,
    val commentCount: Int
) {
    constructor(member: Member, boardCount: Int, commentCount: Int) : this(
        memberId = member.memberId,
        email = member.email,
        nickname = member.nickname,
        image = member.image,
        boardCount = boardCount,
        commentCount = commentCount
    )
}