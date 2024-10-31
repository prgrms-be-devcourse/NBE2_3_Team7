package com.hunmin.domain.repository.search

import com.hunmin.domain.dto.follow.FollowRequestDTO
import com.hunmin.domain.entity.QFollow
import com.hunmin.domain.entity.QMember
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPQLQueryFactory
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class FollowSearchImpl(em: EntityManager) : FollowSearch {
    val queryFactory: JPQLQueryFactory

    init {
        this.queryFactory = JPAQueryFactory(em)
    }

    override fun getFollowPage(memberId: Long, pageable: Pageable): Page<FollowRequestDTO> {
        // 서로 다른 별칭을 가진 QFollow 인스턴스 생성
        val followeeFollow = QFollow("followeeFollow")
        val followerFollow = QFollow("followerFollow")

        val results: List<FollowRequestDTO> = queryFactory
            .select(
                Projections.bean(
                    FollowRequestDTO::class.java,
                    followeeFollow.followId,
                    followeeFollow.follower.memberId.`as`("followerId"),
                    followeeFollow.followee.memberId.`as`("followeeId"),
                    followeeFollow.notification,
                    followeeFollow.isBlock,
                    followeeFollow.status,
                    followeeFollow.follower.nickname.`as`("followerName"),
                    followeeFollow.follower.email.`as`("followerEmail"),
                    followeeFollow.follower.image.`as`("followerImage"),
                    followeeFollow.followee.nickname.`as`("followeeName"),
                    followeeFollow.followee.email.`as`("followeeEmail"),
                    followeeFollow.followee.image.`as`("followeeImage"),
                    followeeFollow.createdAt
                )
            ).distinct()
            .from(QMember.member)
            .leftJoin(QMember.member.followees, followeeFollow)
            .leftJoin(QMember.member.followers, followerFollow)
            .where(QMember.member.memberId.eq(memberId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        log.info("results{}", results.toTypedArray())

        // 전체 갯수 계산 시 특정 요건에 맞게 쿼리 수정 필요할 수 있음
        val total: Long = queryFactory
            .select(followeeFollow.count())
            .from(QMember.member)
            .leftJoin(QMember.member.followees, followeeFollow)
            .leftJoin(QMember.member.followers, followerFollow)
            .where(QMember.member.memberId.eq(memberId))
            .fetchOne()?: throw RuntimeException("follow total is null")

        return PageImpl(results, pageable, total)
    }
}
