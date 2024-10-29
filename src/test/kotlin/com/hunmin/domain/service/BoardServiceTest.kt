package com.hunmin.domain.service

import com.hunmin.domain.dto.board.BoardRequestDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.MemberRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.*

class BoardServiceTest {

    @Mock
    private lateinit var boardRepository: BoardRepository

    @Mock
    private lateinit var memberRepository: MemberRepository

    @InjectMocks
    private lateinit var boardService: BoardService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    //게시글 등록 테스트
    @Test
    fun createBoard() {
        val member = Member(memberId = 1L, nickname = "tester", email = "test@test.com", country = "Korea", level = MemberLevel.BEGINNER, password = "123")
        val boardRequestDTO = BoardRequestDTO(
            boardId = 1L,
            memberId = member.memberId,
            title = "테스트 제목",
            content = "테스트 내용",
            location = "위치 이름",
            latitude = 0.0,
            longitude = 0.0,
            imageUrls = listOf("image1.png", "image2.png").toMutableList()
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(boardRepository.save(any(Board::class.java))).thenAnswer { it.arguments[0] as Board }

        val responseDTO = boardService.createBoard(boardRequestDTO)

        assertNotNull(responseDTO.boardId)
        assertEquals(boardRequestDTO.title, responseDTO.title)
        verify(boardRepository, times(1)).save(any(Board::class.java))
    }

    //게시글 조회 테스트
    @Test
    fun readBoard() {
        val boardId = 1L
        val board = Board(
            boardId = boardId,
            title = "테스트 제목",
            nickname = "tester",
            content = "테스트 내용",
            location = "위치 이름",
            latitude = 0.0,
            longitude = 0.0
        )

        `when`(boardRepository.findByIdWithComments(boardId)).thenReturn(Optional.of(board))

        val responseDTO = boardService.readBoard(boardId)

        assertEquals(board.title, responseDTO.title)
        verify(boardRepository, times(1)).findByIdWithComments(boardId)
    }

    //게시글 수정 테스트
    @Test
    fun updateBoard() {
        val boardId = 1L
        val board = Board(
            boardId = boardId,
            title = "테스트 제목",
            nickname = "tester",
            content = "테스트 내용",
            location = "위치 이름",
            latitude = 0.0,
            longitude = 0.0
        )
        val boardRequestDTO = BoardRequestDTO(
            boardId = boardId,
            memberId = 1L,
            title = "테스트 제목 수정",
            content = "테스트 내용 수정",
            location = "위치 이름 수정",
            latitude = 1.0,
            longitude = 1.0,
            imageUrls = listOf("image1.png").toMutableList()
        )

        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))

        val updatedResponseDTO = boardService.updateBoard(boardId, boardRequestDTO)

        assertEquals(boardRequestDTO.title, updatedResponseDTO.title)
        assertEquals(boardRequestDTO.content, updatedResponseDTO.content)
        verify(boardRepository, times(1)).save(board)
    }

    //게시글 삭제 테스트
    @Test
    fun deleteBoard() {
        val boardId = 1L
        val board = Board(
            boardId = boardId,
            title = "테스트 제목",
            nickname = "tester",
            content = "테스트 내용",
            location = "위치 이름",
            latitude = 0.0,
            longitude = 0.0
        )

        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))

        boardService.deleteBoard(boardId)

        verify(boardRepository, times(1)).delete(board)
    }

    //게시글 목록 조회 테스트
    @Test
    fun readBoardList() {
        val boards = listOf(
            Board(boardId = 1L, title = "제목1", nickname = "tester1", content = "내용1", location = "위치1", latitude = 0.0, longitude = 0.0),
            Board(boardId = 2L, title = "제목2", nickname = "tester2", content = "내용2", location = "위치2", latitude = 1.0, longitude = 1.0)
        )
        val pageRequestDTO = PageRequestDTO(page = 1, size = 10)
        val pageable = pageRequestDTO.getPageable(Sort.by(Sort.Direction.DESC, "createdAt"))

        `when`(boardRepository.findAll(pageable)).thenReturn(PageImpl(boards, pageable, boards.size.toLong()))

        val responsePage = boardService.readBoardList(pageRequestDTO)

        assertEquals(2, responsePage.totalElements)
        assertEquals(boards[0].title, responsePage.content[0].title)
        verify(boardRepository, times(1)).findAll(pageable)
    }

    //회원 별 작성글 목록 조회 테스트
    @Test
    fun readBoardListByMember() {
        val memberId = 1L
        val boards = listOf(
            Board(boardId = 1L, title = "제목1", nickname = "tester1", content = "내용1", location = "위치1", latitude = 0.0, longitude = 0.0),
            Board(boardId = 2L, title = "제목2", nickname = "tester2", content = "내용2", location = "위치2", latitude = 1.0, longitude = 1.0)
        )
        val pageRequestDTO = PageRequestDTO(page = 1, size = 10)
        val pageable = pageRequestDTO.getPageable(Sort.by(Sort.Direction.DESC, "createdAt"))

        `when`(boardRepository.findByMemberId(memberId, pageable)).thenReturn(PageImpl(boards))

        val responsePage = boardService.readBoardListByMember(memberId, pageRequestDTO)

        assertEquals(2, responsePage.totalElements)
        assertEquals(boards[0].title, responsePage.content[0].title)
        verify(boardRepository, times(1)).findByMemberId(memberId, pageable)
    }
}
