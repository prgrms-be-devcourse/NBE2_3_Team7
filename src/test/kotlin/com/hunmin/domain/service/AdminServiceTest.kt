package com.hunmin.domain.service

import com.hunmin.domain.dto.member.MemberStatusDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.*
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.CommentRepository
import com.hunmin.domain.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class AdminServiceTest {
    @Autowired
    private lateinit var adminService: AdminService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var commentRepository: CommentRepository

    private val savedMembers = mutableListOf<Member>()
    private val savedBoards = mutableListOf<Board>()
    private val savedComments = mutableListOf<Comment>()

    @BeforeEach
    fun setUp() {
        // 회원 더미 데이터 생성 (관리자 5명, 일반 사용자 5명)
        (1..10).forEach { i ->
            val isAdmin = i <= 5
            val member = Member(
                memberRole = if (isAdmin) MemberRole.ADMIN else MemberRole.USER,
                nickname = if (isAdmin) "관리자이름$i" else "유저이름$i",
                country = if (isAdmin) "관리자국적$i" else "유저국적$i",
                email = if (isAdmin) "관리자이멜$i" else "유저이멜$i",
                image = if (isAdmin) "관리자아바타$i" else "유저아바타$i",
                level = MemberLevel.ADVANCED,
                password = if (isAdmin) "관리자비밀번호$i" else "유저비밀번호$i"
            )

            val savedMember = memberRepository.save(member)
            savedMembers.add(savedMember)

            // 각 회원별 게시글 생성
            val boardCount = 5
            repeat(boardCount) { boardIndex ->
                val board = Board(
                    title = "${savedMember.nickname}의 게시글 ${boardIndex + 1}",
                    content = "게시글 내용 ${boardIndex + 1}",
                    nickname = "게시글 작성자 닉네임 ${boardIndex + 1}",
                    member = savedMember
                )
                val savedBoard = boardRepository.save(board)
                savedBoards.add(savedBoard)

                // 각 게시글별 댓글 생성 (게시글당 2개)
                repeat(2) { commentIndex ->
                    val comment = Comment(
                        content = "${savedMember.nickname}의 댓글 ${commentIndex + 1}",
                        member = savedMember,
                        board = savedBoard
                    )
                    val savedComment = commentRepository.save(comment)
                    savedComments.add(savedComment)
                }
            }
        }
    }

    @Test
    @DisplayName("전체 회원 목록 조회 - 페이징")
    fun getAllMemberStatus() {
        // given
        val pageRequest = PageRequestDTO(page = 1)

        // when
        val result = adminService.getAllMembers(pageRequest)

        // then
        assertThat(result.content).hasSize(10)
        assertThat(result.content.first()).isInstanceOf(MemberStatusDTO::class.java)

        // 사용자의 게시글, 댓글 수 확인
        result.content.forEach { memberStatus ->
            assertThat(memberStatus.boardCount).isEqualTo(5)  // 각 사용자당 5개 게시글
            assertThat(memberStatus.commentCount).isEqualTo(10)  // 각 게시글당 2개 댓글 = 총 10개
        }
    }

    @Test
    @DisplayName("특정 회원의 게시글 목록 조회")
    fun getBoardByMemberId() {
        // given
        val member = savedMembers.first()
        val pageRequest = PageRequestDTO(page = 1)

        // when
        val result = adminService.getBoardsByMemberId(member.memberId, pageRequest)

        // then
        val boards = result.content
        val expectedBoardCount = 5

        assertThat(boards.size).isEqualTo(expectedBoardCount)

        for (board in boards) {
            assertThat(board.nickname).isEqualTo(member.nickname) //페이징된 게시글이 같은 작성자인지
        }
    }

    @Test
    @DisplayName("특정 회원의 댓글 목록 조회")
    fun getCommentByMemberId() {
        // given
        val member = savedMembers.first()
        val pageRequest = PageRequestDTO(page = 1)

        // when
        val result = adminService.getCommentsByMemberId(member.memberId, pageRequest)

        // then
        val comments = result.content
        val expectedCommentCount = 10

        assertThat(comments.size).isEqualTo(expectedCommentCount)

        for (comment in comments) {
            assertThat(comment.nickname).isEqualTo(member.nickname) //페이징된 댓글이 같은 작성자인지
        }
    }

    @Test
    @DisplayName("닉네임으로 회원 검색")
    fun getMemberByNickname() {
        // given
        val member = savedMembers[2]
        val nickname = member.nickname

        // when
        val result = adminService.getMemberByNickname(nickname)

        // then
        assertThat(result.nickname).isEqualTo(nickname)
        assertThat(result.boardCount).isEqualTo(5)
        assertThat(result.commentCount).isEqualTo(10)
    }

    @Test
    @DisplayName("회원별 활동 상황 확인")
    fun getMemberStatus() {
        // given
        val member = savedMembers[3]

        // when
        val memberStatus = adminService.getMemberByMemberId(member.memberId)

        // then
        assertThat(memberStatus).apply {
            extracting { it.boardCount }.isEqualTo(5)
            extracting { it.commentCount }.isEqualTo(10)
        }
    }
}