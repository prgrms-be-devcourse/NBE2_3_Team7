package com.hunmin.domain.repository

import com.hunmin.domain.entity.LikeComment
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Commit
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
class LikeCommentRepositoryTest {
    @Autowired
    lateinit var likeCommentRepository: LikeCommentRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var commentRepository: CommentRepository

    //댓글 좋아요 등록 테스트
    @Test
    @Commit
    fun testCreateLikeComment() {
        val likeComment = LikeComment.builder()
            .member(memberRepository.findById(1).get())
            .comment(commentRepository.findById(3).get())
            .build()

        val savedLikeComment = likeCommentRepository.save(likeComment)

        assertNotNull(savedLikeComment)
    }

    //댓글 좋아요 삭제 테스트
    @Test
    @Transactional
    @Commit
    fun testDeleteLikeComment() {
        val likeCommentId = 3L

        likeCommentRepository.deleteById(likeCommentId)

        assertTrue(likeCommentRepository.findById(likeCommentId).isEmpty)
    }

    //사용자와 댓글로 좋아요 조회 테스트
    @Test
    fun testReadLikeCommentByMemberAndComment() {
        val member = memberRepository.findById(1).get()
        val comment = commentRepository.findById(3).get()

        val likeComment = LikeComment.builder()
            .member(member)
            .comment(comment)
            .build()

        likeCommentRepository.save(likeComment)

        val foundLikeComment = likeCommentRepository.findByMemberAndComment(member, comment)

        assertTrue(foundLikeComment.isPresent)
        assertEquals(member.memberId, foundLikeComment.get().member.memberId)
        assertEquals(comment.commentId, foundLikeComment.get().comment.commentId)
    }

    //좋아요 여부 확인 테스트
    @Test
    fun testExistsLikeCommentByMemberAndComment() {
        val member = memberRepository.findById(1).get()
        val comment = commentRepository.findById(3).get()

        val likeComment = LikeComment.builder()
            .member(member)
            .comment(comment)
            .build()
        likeCommentRepository.save(likeComment)

        val exists = likeCommentRepository.existsByMemberAndComment(member, comment)

        assertTrue(exists)
    }

    //좋아요 누른 사용자 목록 조회 테스트
    @Test
    fun testReadBookMarkUserListByLikeCommentId() {
        val member1 = memberRepository.findById(1).get()
        val member2 = memberRepository.findById(2).get()
        val comment = commentRepository.findById(3).get()

        val likeComment1 = LikeComment.builder().member(member1).comment(comment).build()
        val likeComment2 = LikeComment.builder().member(member2).comment(comment).build()

        likeCommentRepository.save(likeComment1)
        likeCommentRepository.save(likeComment2)

        val likedMembers = likeCommentRepository.findMembersByLikedCommentId(comment.commentId)

        assertEquals(2, likedMembers.size)
        assertTrue(likedMembers.contains(member1))
        assertTrue(likedMembers.contains(member2))
    }
}
