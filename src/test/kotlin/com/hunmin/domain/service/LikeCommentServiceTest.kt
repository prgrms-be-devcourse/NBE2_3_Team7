package com.hunmin.domain.service

import com.hunmin.domain.entity.*
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.CommentRepository
import com.hunmin.domain.repository.LikeCommentRepository
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

class LikeCommentServiceTest {

    @Mock
    private lateinit var likeCommentRepository: LikeCommentRepository

    @Mock
    private lateinit var memberRepository: MemberRepository

    @Mock
    private lateinit var boardRepository: BoardRepository

    @Mock
    private lateinit var commentRepository: CommentRepository

    @InjectMocks
    private lateinit var likeCommentService: LikeCommentService

    private val memberId = 1L
    private val boardId = 1L
    private val commentId = 1L
    private lateinit var member: Member
    private lateinit var board: Board
    private lateinit var comment: Comment

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        member = Member(memberId, "tester", "test@test.com", "123", "country", MemberLevel.BEGINNER)
        board = Board(boardId, member, "테스트 제목", "tester", "테스트 내용")
        comment = Comment(commentId, board, member, "댓글 내용")
    }

    //좋아요 등록 테스트
    @Test
    fun createLikeComment() {
        `when`(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(commentRepository.findById(commentId)).thenReturn(Optional.of(comment))
        `when`(likeCommentRepository.findByMemberAndComment(member, comment)).thenReturn(Optional.empty())

        likeCommentService.createLikeComment(memberId, commentId)

        verify(likeCommentRepository, times(1)).save(any(LikeComment::class.java))
        assertEquals(1, comment.likeCount)
    }

    //좋아요 삭제 테스트
    @Test
    fun deleteLikeComment() {
        `when`(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(commentRepository.findById(commentId)).thenReturn(Optional.of(comment))
        `when`(likeCommentRepository.findByMemberAndComment(member, comment)).thenReturn(Optional.of(LikeComment(1, member, comment)))

        likeCommentService.deleteLikeComment(memberId, commentId)

        verify(likeCommentRepository, times(1)).delete(any(LikeComment::class.java))
        assertEquals(0, comment.likeCount)
    }

    //좋아요 여부 확인 테스트
    @Test
    fun isLikeComment() {
        `when`(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(commentRepository.findById(commentId)).thenReturn(Optional.of(comment))
        `when`(likeCommentRepository.existsByMemberAndComment(member, comment)).thenReturn(true)

        val result = likeCommentService.isLikeComment(memberId, commentId)

        assertTrue(result)
    }

    // 좋아요 누른 사용자 목록 조회 테스트
    @Test
    fun getLikeCommentMembers() {
        val likedMembers = listOf(
            Member(2, "tester2", "test2@test.com", "123", "country", MemberLevel.BEGINNER),
            Member(3, "tester3", "test3@test.com", "123", "country", MemberLevel.BEGINNER)
        )

        `when`(likeCommentRepository.findMembersByLikedCommentId(commentId)).thenReturn(likedMembers)

        val result = likeCommentService.getLikeCommentMembers(commentId)

        assertEquals(2, result.size)
    }
}
