package com.hunmin.domain.service

import com.hunmin.domain.dto.comment.CommentRequestDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.Comment
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.CommentRepository
import com.hunmin.domain.repository.MemberRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.*

class CommentServiceTest {

    @Mock
    private lateinit var memberRepository: MemberRepository

    @Mock
    private lateinit var boardRepository: BoardRepository

    @Mock
    private lateinit var commentRepository: CommentRepository

    @InjectMocks
    private lateinit var commentService: CommentService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    //댓글 등록 테스트
    @Test
    fun createComment() {
        val member = Member(memberId = 1L, nickname = "tester", email = "test@test.com", country = "Korea", level = MemberLevel.BEGINNER, password = "123")
        val board = Board(1L, member, "테스트 제목", "tester", "테스트 내용", "위치 이름", 0.0, 0.0)
        val commentRequestDTO = CommentRequestDTO(commentId = 1L, memberId = 1L, boardId = 1L, content = "테스트 댓글 내용")

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(boardRepository.findById(1L)).thenReturn(Optional.of(board))
        `when`(commentRepository.save(any(Comment::class.java))).thenAnswer { it.arguments[0] as Comment }

        val responseDTO = commentService.createComment(commentRequestDTO)

        assertEquals(commentRequestDTO.content, responseDTO.content)
        verify(commentRepository, times(1)).save(any(Comment::class.java))
    }

    //대댓글 등록 테스트
    @Test
    fun createCommentChild() {
        val member = Member(memberId = 1L, nickname = "tester", email = "test@test.com", country = "Korea", level = MemberLevel.BEGINNER, password = "123")
        val board = Board(1L, member, "테스트 제목", "tester", "테스트 내용", "위치 이름", 0.0, 0.0)
        val parentComment = Comment(member = member, board = board, content = "부모 댓글 내용")
        val commentRequestDTO = CommentRequestDTO(commentId = 1L, memberId = 1L, boardId = 1L, content = "자식 댓글 내용")
        val childComment = Comment(member = member, board = board, parent = parentComment, content = "자식 댓글 내용")

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(boardRepository.findById(1L)).thenReturn(Optional.of(board))
        `when`(commentRepository.findById(1L)).thenReturn(Optional.of(parentComment))
        `when`(commentRepository.save(any(Comment::class.java))).thenReturn(childComment)

        val response = commentService.createCommentChild(1L, 1L, commentRequestDTO)

        assertEquals("자식 댓글 내용", response.content)
        assertEquals(parentComment, childComment.parent)
        verify(commentRepository, times(1)).save(any(Comment::class.java))
    }

    //댓글 수정 테스트
    @Test
    fun updateComment() {
        val member = Member(memberId = 1L, nickname = "tester", email = "test@test.com", country = "Korea", level = MemberLevel.BEGINNER, password = "123")
        val board = Board(1L, member, "테스트 제목", "tester", "테스트 내용", "위치 이름", 0.0, 0.0)
        val commentId = 1L
        val commentRequestDTO = CommentRequestDTO(commentId = commentId, memberId = 1L, boardId = 2L, content = "댓글 내용 수정")
        val existingComment = Comment(member = member, board = board, content = "Old content")

        `when`(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment))
        `when`(commentRepository.save(any(Comment::class.java))).thenReturn(existingComment)

        val response = commentService.updateComment(commentId, commentRequestDTO)

        assertEquals("댓글 내용 수정", response.content)
        verify(commentRepository, times(1)).save(existingComment)
    }

    //댓글 삭제 테스트
    @Test
    fun deleteComment() {
        val member = Member(memberId = 1L, nickname = "tester", email = "test@test.com", country = "Korea", level = MemberLevel.BEGINNER, password = "123")
        val board = Board(1L, member, "테스트 제목", "tester", "테스트 내용", "위치 이름", 0.0, 0.0)
        val commentId = 1L
        val comment = Comment(member = member, board = board, content = "댓글 내용 삭제")

        `when`(commentRepository.findById(commentId)).thenReturn(Optional.of(comment))
        doNothing().`when`(commentRepository).delete(comment)

        val response = commentService.deleteComment(commentId)

        assertEquals(comment.content, response.content)
        verify(commentRepository, times(1)).delete(comment)
    }

    //게시글 별 댓글 목록 조회 테스트
    @Test
    fun readCommentList() {
        val member = Member(memberId = 1L, nickname = "tester", email = "test@test.com", country = "Korea", level = MemberLevel.BEGINNER, password = "123")
        val board = Board(1L, member, "테스트 제목", "tester", "테스트 내용", "위치 이름", 0.0, 0.0)
        val comments = listOf(
            Comment(member = member, board = board, content = "댓글1"),
            Comment(member = member, board = board, content = "댓글2")
        )
        val pageRequestDTO = PageRequestDTO(page = 1, size = 10)
        val pageable: Pageable = pageRequestDTO.getPageable(Sort.by("commentId").ascending())

        `when`(commentRepository.findByBoardId(1L, pageable)).thenReturn(PageImpl(comments, pageable, comments.size.toLong()))

        val responsePage = commentService.readCommentList(1L, pageRequestDTO)

        assertEquals(2, responsePage.totalElements)
        assertEquals(comments[0].content, responsePage.content[0].content)
        verify(commentRepository, times(1)).findByBoardId(1L, pageable)
    }
}