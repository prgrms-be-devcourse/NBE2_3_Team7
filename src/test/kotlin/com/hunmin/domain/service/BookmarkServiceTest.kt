package com.hunmin.domain.service

import com.hunmin.domain.dto.board.BoardResponseDTO
import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.Bookmark
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.BookmarkRepository
import com.hunmin.domain.repository.MemberRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookmarkServiceTest {

    @Mock
    private lateinit var boardRepository: BoardRepository

    @Mock
    private lateinit var memberRepository: MemberRepository

    @Mock
    private lateinit var bookmarkRepository: BookmarkRepository

    @InjectMocks
    private lateinit var bookmarkService: BookmarkService

    private val memberId = 1L
    private val boardId = 1L
    private lateinit var member: Member
    private lateinit var board: Board

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        member = Member(memberId, "tester", "test@test.com", "123", "country", MemberLevel.BEGINNER)
        board = Board(boardId, member, "테스트 제목", "tester", "테스트 내용")
    }

    //북마크 등록 테스트
    @Test
    fun createBookmark() {
        `when`(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(bookmarkRepository.findByMemberAndBoard(member, board)).thenReturn(Optional.empty())

        bookmarkService.createBookmark(boardId, memberId)

        verify(bookmarkRepository, times(1)).save(any(Bookmark::class.java))
    }

    // 북마크 삭제 테스트
    @Test
    fun deleteBookmark() {
        `when`(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(bookmarkRepository.findByMemberAndBoard(member, board)).thenReturn(Optional.of(Bookmark(1, member, board)))

        bookmarkService.deleteBookmark(boardId, memberId)

        verify(bookmarkRepository, times(1)).delete(any(Bookmark::class.java))
    }

    //회원별 북마크 게시글 목록 조회 테스트
    @Test
    fun readBookmarkByMember() {
        val board1 = Board(1, member, "제목1", "tester", "내용1")
        val board2 = Board(2, member, "테스트 제목", "tester", "테스트 내용")
        val boards = listOf(board1, board2)

        `when`(bookmarkRepository.findByMemberId(memberId)).thenReturn(boards)

        val result = bookmarkService.readBookmarkByMember(memberId)

        assertEquals(2, result.size)
        assertTrue(result.all { it is BoardResponseDTO })
    }

    //북마크 여부 확인 테스트
    @Test
    fun isBookmarked() {
        `when`(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(bookmarkRepository.existsByMemberAndBoard(member, board)).thenReturn(true)

        val result = bookmarkService.isBookmarked(boardId, memberId)

        assertTrue(result)
    }
}
