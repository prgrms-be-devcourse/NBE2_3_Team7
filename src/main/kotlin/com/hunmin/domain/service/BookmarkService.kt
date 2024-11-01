package com.hunmin.domain.service

import com.hunmin.domain.dto.board.BoardResponseDTO
import com.hunmin.domain.entity.Bookmark
import com.hunmin.domain.exception.BoardException
import com.hunmin.domain.exception.BookmarkException
import com.hunmin.domain.exception.MemberException
import com.hunmin.domain.repository.BookmarkRepository
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val boardRepository: BoardRepository,
    private val memberRepository: MemberRepository
) {

    //북마크 등록
    fun createBookmark(boardId: Long, memberId: Long) {
        return try {
            val member = memberRepository.findById(memberId).orElseThrow { MemberException.NOT_FOUND.get() }
            val board = boardRepository.findById(boardId).orElseThrow { BoardException.NOT_FOUND.toException() }

            bookmarkRepository.findByMemberAndBoard(member, board).ifPresentOrElse(
                { throw BookmarkException.NOT_CREATED.toException() },
                { bookmarkRepository.save(Bookmark.builder().member(member).board(board).build()) }
            )
        } catch (e: Exception) {
            throw BookmarkException.NOT_CREATED.toException()
        }
    }

    //북마크 삭제
    fun deleteBookmark(boardId: Long, memberId: Long) {
        return try {
            val member = memberRepository.findById(memberId).orElseThrow { MemberException.NOT_FOUND.get() }
            val board = boardRepository.findById(boardId).orElseThrow { BoardException.NOT_FOUND.toException() }

            val bookmark = bookmarkRepository.findByMemberAndBoard(member, board).orElseThrow { BookmarkException.NOT_FOUND.toException() }

            bookmarkRepository.delete(bookmark)
        } catch (e: Exception) {
            throw BookmarkException.NOT_DELETED.toException()
        }
    }

    // 회원 별 북마크 게시글 목록 조회
    fun readBookmarkByMember(memberId: Long): List<BoardResponseDTO> {
        val boards = bookmarkRepository.findByMemberId(memberId)

        return boards.map { BoardResponseDTO(it) }
    }

    // 북마크 여부 확인
    fun isBookmarked(boardId: Long, memberId: Long): Boolean {
        val member = memberRepository.findById(memberId).orElseThrow { MemberException.NOT_FOUND.get() }
        val board = boardRepository.findById(boardId).orElseThrow { BoardException.NOT_FOUND.toException() }

        return bookmarkRepository.existsByMemberAndBoard(member, board)
    }
}
