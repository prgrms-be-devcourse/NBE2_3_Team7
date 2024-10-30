package com.hunmin.domain.service

import com.hunmin.domain.dto.board.BoardResponseDTO
import com.hunmin.domain.dto.comment.CommentResponseDTO
import com.hunmin.domain.dto.member.MemberStatusDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.Comment
import com.hunmin.domain.entity.Member
import com.hunmin.domain.exception.AdminException
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.CommentRepository
import com.hunmin.domain.repository.MemberRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminService (
    private val boardRepository: BoardRepository ,
    private val commentRepository: CommentRepository ,
    private val memberRepository: MemberRepository
){
    private val log = LoggerFactory.getLogger(this::class.java)
    //회원 검색

    fun getMemberByMemberId(memberId: Long): MemberStatusDTO {
        return runCatching {
            val member: Member =
                memberRepository.findById(memberId).orElseThrow<RuntimeException>(AdminException.MEMBER_NOT_FOUND::get)
            val boardCount: Int = boardRepository.countByMemberId(member.memberId)
            val commentCount: Int = commentRepository.countByMemberId(member.memberId)
            MemberStatusDTO(member, boardCount, commentCount)
        }.getOrElse { e ->
            log.error("getMemberByMemberId : {}", e.message)
            throw AdminException.MEMBER_NOT_FOUND.get()
        }
    }

    //회원 닉네임으로 검색
    fun getMemberByNickname(username: String): MemberStatusDTO {
        return runCatching {
            val member: Member =
                memberRepository.findByNickname(username)?: throw AdminException.MEMBER_NOT_FOUND.get()
            val boardCount: Int = boardRepository.countByMemberId(member.memberId)
            val commentCount: Int = commentRepository.countByMemberId(member.memberId)
            MemberStatusDTO(member, boardCount, commentCount)
        }.getOrElse { e ->
            log.error("getMemberByNickname : {}", e.message)
            throw AdminException.MEMBER_NOT_FOUND.get()
        }
    }

    //회원 목록 조회
    fun getAllMembers(pageRequestDTO: PageRequestDTO): Page<MemberStatusDTO> {
        val sort = Sort.by(Sort.Direction.DESC, "createdAt")
        val pageable: Pageable = PageRequest.of(pageRequestDTO.page - 1, 10, sort)
        return runCatching {
            memberRepository.findAll(pageable)
                .map { member ->
                    val boardCount = boardRepository.countByMemberId(member.memberId)
                    val commentCount = commentRepository.countByMemberId(member.memberId)
                    MemberStatusDTO(member, boardCount, commentCount)
                }
        }.getOrElse { e->
            log.error("getAllMembers : {}", e.message)
            throw AdminException.MEMBERS_NOT_FOUND.get()
        }
    }

    //회원별 작성글 목록
    fun getBoardsByMemberId(memberId: Long, pageRequestDTO: PageRequestDTO): Page<BoardResponseDTO> {
        val sort = Sort.by(Sort.Direction.DESC, "createdAt")
        val pageable: Pageable = PageRequest.of(pageRequestDTO.page - 1, 10, sort)

        return runCatching {
            val boardPage = boardRepository.findByMemberId(memberId, pageable)
            val boardResponseDTOs = boardPage.content.map { BoardResponseDTO(it) }
            PageImpl(boardResponseDTOs, pageable, boardPage.totalElements)
        }.getOrElse { e ->
            log.error("getBoardsByMemberId : {}", e.message)
            throw AdminException.BOARDS_NOT_FOUND.get()
        }
    }

    //회원별 댓글 목록
    fun getCommentsByMemberId(memberId: Long, pageRequestDTO: PageRequestDTO): Page<CommentResponseDTO> {
        val sort = Sort.by(Sort.Direction.DESC, "createdAt")
        val pageable: Pageable = PageRequest.of(pageRequestDTO.page - 1, 10, sort)

        // Page<Comment>로 반환받기
        return runCatching {
            val commentPage = commentRepository.findByMemberId(memberId, pageable)
            val commentResponseDTOs = commentPage.content.map { CommentResponseDTO(it) }
            PageImpl(commentResponseDTOs, pageable, commentPage.totalElements)
        }.getOrElse { e ->
            log.error("getCommentsByMemberId : {}", e.message)
            throw AdminException.COMMENTS_NOT_FOUND.get()
        }
    }
}