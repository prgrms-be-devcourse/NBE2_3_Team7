package com.hunmin.domain.repository

import com.hunmin.domain.entity.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    // 회원 정보 조회
    fun findByEmail(email: String): Member

    // 중복 체크
    fun existsByEmail(email: String): Boolean

    // 이름으로 조회
    fun findByNickname(nickname: String): Member

    @Query("SELECT cr.member FROM ChatRoom cr WHERE cr.chatRoomId = :chatRoomId")
    fun findByChatRoomId(@Param("chatRoomId") chatRoomId: Long): Optional<Member>

    // 비밀번호 찾기/변경 메서드
    fun findUserByEmailAndNickname(email: String, nickname: String): Member?
    fun existsByEmailAndNickname(email: String, nickname: String): Boolean

    //모든 멤버 리스트로 조회
    override fun findAll(pageable: Pageable): Page<Member>
}
