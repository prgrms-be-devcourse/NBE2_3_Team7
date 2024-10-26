package com.hunmin.domain.entity

import jakarta.persistence.*

@Entity
data class Follow (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val followId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    var follower: Member? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id")
    var followee: Member? = null,

    @Column(nullable = false)
    var isBlock: Boolean? = null,

    @Column(nullable = false)
    var notification: Boolean = true,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: FollowStatus = FollowStatus.PENDING

) : BaseTimeEntity(){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Follow) return false
        return followId == other.followId
    }

    override fun toString(): String {
        return "followId = $followId, isBlock = $isBlock, notification = $notification, status = $status"
    }

    override fun hashCode(): Int {
        return followId.hashCode()
    }
}
