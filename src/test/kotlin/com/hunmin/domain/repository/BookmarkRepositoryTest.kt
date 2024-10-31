package com.hunmin.domain.repository

import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.Bookmark
import com.hunmin.domain.entity.Member
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotNull

@SpringBootTest
@Transactional
class BookmarkRepositoryTest {
    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var bookmarkRepository: BookmarkRepository

    //북마크 등록 테스트
    @Test
    @Commit
    fun testCreateBookmark() {
        val bookmark = Bookmark.builder()
            .member(memberRepository.findById(1).get())
            .board(boardRepository.findById(3).get())
            .build()

        val savedBookmark = bookmarkRepository.save(bookmark)

        assertNotNull(savedBookmark)
    }

    //북마크 삭제 테스트
    @Test
    @Transactional
    @Commit
    fun testDeleteBookmark() {
        val bookmarkId = 1L

        bookmarkRepository.deleteById(bookmarkId)

        assertTrue(bookmarkRepository.findById(bookmarkId).isEmpty)
    }

    //사용자와 게시글로 북마크 조회 테스트
    @Test
    fun testReadBookmarkByMemberAndBoard() {
        val member: Member = memberRepository.findById(1).get()
        val board: Board = boardRepository.findById(2).get()

        val bookmark = Bookmark.builder()
            .member(member)
            .board(board)
            .build()

        bookmarkRepository.save(bookmark)

        val foundBookmark = bookmarkRepository.findByMemberAndBoard(member, board)

        assertTrue(foundBookmark.isPresent)
        assertEquals(member.memberId, foundBookmark.get().member.memberId)
        assertEquals(board.boardId, foundBookmark.get().board.boardId)
    }

    //회원 별 북마크 게시글 목록 조회 테스트
    @Test
    fun testReadBookmarkListByMemberId() {
        val member: Member = memberRepository.findById(1).get()
        val board: Board = boardRepository.findById(2).get()

        val bookmark = Bookmark.builder()
            .member(member)
            .board(board)
            .build()
        bookmarkRepository.save(bookmark)

        val bookmarkedBoards: List<Board> = bookmarkRepository.findByMemberId(member.memberId)

        assertNotNull(bookmarkedBoards)
    }

    //북마크 여부 확인 테스트
    @Test
    fun testExistsBookmarkByMemberAndBoard() {
        val member: Member = memberRepository.findById(1).get()
        val board: Board = boardRepository.findById(2).get()

        val bookmark = Bookmark.builder()
            .member(member)
            .board(board)
            .build()
        bookmarkRepository.save(bookmark)

        val exists: Boolean = bookmarkRepository.existsByMemberAndBoard(member, board)

        assertTrue(exists)
    }
}