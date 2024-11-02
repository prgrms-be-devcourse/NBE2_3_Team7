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

        val results = queryFactory
            .select(
                Projections.constructor(
                    FollowRequestDTO::class.java,
                    followeeFollow.followId,
                    followeeFollow.follower.memberId.`as`("followerId"),
                    followeeFollow.followee.memberId.`as`("followeeId"),
                    followeeFollow.isBlock,
                    followeeFollow.notification,
                    followeeFollow.createdAt,
                    followeeFollow.status,
                    followeeFollow.follower.nickname.`as`("followerName"),
                    followeeFollow.follower.email.`as`("followerEmail"),
                    followeeFollow.follower.image.`as`("followerImage"),
                    followeeFollow.followee.nickname.`as`("followeeName"),
                    followeeFollow.followee.email.`as`("followeeEmail"),
                    followeeFollow.followee.image.`as`("followeeImage"),
                )
            ).distinct()
            .from(QMember.member)
            .leftJoin(QMember.member.followees, followeeFollow)
            .leftJoin(QMember.member.followers, followerFollow)
            .where(QMember.member.memberId.eq(memberId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        log.info("results페이징${results}")

        val total: Long = queryFactory
            .select(followeeFollow.count())
            .from(QMember.member)
            .leftJoin(QMember.member.followees, followeeFollow)
            .leftJoin(QMember.member.followers, followerFollow)
            .where(QMember.member.memberId.eq(memberId))
            .fetchOne()?:0

        return PageImpl(results, pageable, total)
    }

    override fun getFollowList(memberId: Long): List<FollowRequestDTO> {
        // 서로 다른 별칭을 가진 QFollow 인스턴스 생성
        val followeeFollow = QFollow("followeeFollow")
        val followerFollow = QFollow("followerFollow")

        val results = queryFactory
            .select(
                Projections.constructor(
                    FollowRequestDTO::class.java,
                    followeeFollow.followId,
                    followeeFollow.follower.memberId.`as`("followerId"),
                    followeeFollow.followee.memberId.`as`("followeeId"),
                    followeeFollow.isBlock,
                    followeeFollow.notification,
                    followeeFollow.createdAt,
                    followeeFollow.status,
                    followeeFollow.follower.nickname.`as`("followerName"),
                    followeeFollow.follower.email.`as`("followerEmail"),
                    followeeFollow.follower.image.`as`("followerImage"),
                    followeeFollow.followee.nickname.`as`("followeeName"),
                    followeeFollow.followee.email.`as`("followeeEmail"),
                    followeeFollow.followee.image.`as`("followeeImage"),
                )
            ).distinct()
            .from(QMember.member)
            .leftJoin(QMember.member.followees, followeeFollow)
            .leftJoin(QMember.member.followers, followerFollow)
            .where(QMember.member.memberId.eq(memberId))
            .fetch() ?: emptyList()

        log.info("results 팔로우 리스트${results}")

        return results
    }
}
