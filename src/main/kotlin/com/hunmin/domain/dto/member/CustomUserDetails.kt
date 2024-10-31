package com.hunmin.domain.dto.member

import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

// UserDetails를 구현하여 인증된 사용자의 정보 처리를 위한 클래스
class CustomUserDetails(
    private val member: Member
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(
        SimpleGrantedAuthority("ROLE_${member.memberRole.name}")
    )

    override fun getPassword(): String = member.password

    override fun getUsername(): String = member.email

    fun getMemberId(): Long = member.memberId

    fun getNickname(): String = member.nickname

    fun getImage(): String? = member.image

    fun getLevel(): MemberLevel = member.level

    fun getCountry(): String = member.country

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}