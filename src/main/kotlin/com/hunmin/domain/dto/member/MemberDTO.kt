package com.hunmin.domain.dto.member

import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel

data class MemberDTO(
    var memberId: Long? = null,
    var email: String? = null,
    var password: String? = null,
    var nickname: String? = null,
    var country: String? = null,
    var level: MemberLevel? = null,
    var image: String? = null
) {
    constructor(member: Member): this() {
        this.memberId = member.memberId
        this.email = member.email
        this.password = member.password
        this.nickname = member.nickname
        this.country = member.country
        this.level = member.level
        this.image = member.image
    }
}

