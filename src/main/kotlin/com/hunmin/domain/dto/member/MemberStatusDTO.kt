package com.hunmin.domain.dto.member

import com.hunmin.domain.entity.Member

data class MemberStatusDTO(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val image: String,
    val boardCount: Int,
    val commentCount: Int
) {
    companion object {
        fun from(member: Member, boardCount: Int = 0, commentCount: Int = 0) = MemberStatusDTO(
            memberId = member.memberId,
            email = member.email,
            nickname = member.nickname,
            image = member.image ?: "", 
            boardCount = boardCount,
            commentCount = commentCount
        )
    }
}