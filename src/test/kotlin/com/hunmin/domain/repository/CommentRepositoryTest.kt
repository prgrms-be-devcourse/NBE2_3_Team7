package com.hunmin.domain.repository

import com.hunmin.domain.entity.Comment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.annotation.Commit
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class CommentRepositoryTest {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var commentRepository: CommentRepository

    //댓글 등록 테스트
    @Test
    @Commit
    fun testCreateComment() {
        val comment = Comment.builder()
            .board(boardRepository.findById(2).get())
            .member(memberRepository.findById(1).get())
            .content("댓글 내용")
            .build()

        val savedComment = commentRepository.save(comment)

        assertNotNull(savedComment);
    }

    //대댓글 등록 테스트
    @Test
    @Commit
    fun testCreateCommentChildren() {
        val comment = Comment.builder()
            .board(boardRepository.findById(2).get())
            .member(memberRepository.findById(1).get())
            .parent(commentRepository.findById(1).get())
            .content("대댓글 내용")
            .build()

        val savedComment = commentRepository.save(comment);

        assertNotNull(savedComment);
    }

    //댓글 수정 테스트
    @Test
    @Transactional
    @Commit
    fun testUpdateComment() {
        val commentId = 1L;
        val content = "댓글 수정 내용";

        val comment = commentRepository.findById(commentId).orElseThrow();

        comment.changeContent(content)

        val updatedComment = commentRepository.findById(commentId).orElseThrow();

        assertEquals(content, updatedComment.content);
    }

    //댓글 삭제 테스트
    @Test
    @Transactional
    @Commit
    fun testDeleteComment() {
        val commentId = 1L;

        commentRepository.deleteById(commentId);

        assertTrue(commentRepository.findById(commentId).isEmpty());
    }

    //게시글 별 댓글 목록 조회 테스트
    @Test
    fun testReadCommentList() {
        val pageable: Pageable = PageRequest.of(0, 20)
        val comments: Page<Comment> = commentRepository.findAll(pageable)

        assertNotNull(comments)
    }
}