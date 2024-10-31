package com.hunmin.domain.repository

import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.Member
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.annotation.Commit
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
class BoardRepositoryTest {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var boardRepository: BoardRepository

    //게시글 등록 테스트
    @Test
    @Commit
    fun testCreateBoard() {
        val board = Board.builder()
            .member(memberRepository.findById(1).get())
            .title("제목")
            .nickname(memberRepository.findById(1).get().nickname)
            .content("내용")
            .build()

        val savedBoard = boardRepository.save(board)

        assertNotNull(savedBoard)
    }

    // 게시글 조회 테스트
    @Test
    fun testBoardRead() {
        val boardId = 2L

        val board = boardRepository.findById(boardId).orElseThrow()

        assertNotNull(board)
    }

    // 게시글 수정 테스트
    @Test
    @Transactional
    @Commit
    fun testUpdateBoard() {
        val boardId = 3L
        val title = "수정 제목"
        val content = "수정 내용"

        val board = boardRepository.findById(boardId).orElseThrow()

        board.changeTitle(title)
        board.changeContent(content)

        val updatedBoard = boardRepository.findById(boardId).orElseThrow()

        assertEquals(title, updatedBoard.title)
        assertEquals(content, updatedBoard.content)
    }

    // 게시글 삭제 테스트
    @Test
    @Transactional
    @Commit
    fun testDeleteBoard() {
        val boardId = 1L

        boardRepository.deleteById(boardId)

        assertTrue(boardRepository.findById(boardId).isEmpty)
    }

    // 게시글 목록 조회 테스트
    @Test
    fun testReadBoardList() {
        val pageable: Pageable = PageRequest.of(0, 20)
        val boards: Page<Board> = boardRepository.findAll(pageable)

        assertNotNull(boards)
    }

    // 회원 별 작성글 목록 조회
    @Test
    fun testReadBoardListByMember() {
        val member: Member = memberRepository.findById(1).get()
        val pageable: Pageable = PageRequest.of(0, 20)
        val boards: Page<Board> = boardRepository.findByMemberId(member.memberId, pageable)

        assertNotNull(boards)
    }
}
