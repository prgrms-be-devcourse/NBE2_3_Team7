package com.hunmin.domain.dto.member

import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel

data class MemberDTO(
    var memberId: Long,
    var email: String,
    var password: String,
    var nickname: String,
    var country: String,
    var level: MemberLevel,
    var image: String? = null
) {
    companion object {
        fun from(member: Member) = MemberDTO(
            memberId = member.memberId,
            email = member.email,
            password = member.password,
            nickname = member.nickname,
            country = member.country,
            level = member.level,
            image = member.image
        )
    }
}

