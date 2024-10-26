package com.hunmin.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize

@Entity
data class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val memberId: Long = 0,

    @Column(nullable = false, unique = true)
    var nickname: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var country: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var level: MemberLevel,

    @Enumerated(EnumType.STRING)
    val memberRole: MemberRole = MemberRole.USER,

    @Column(columnDefinition = "TEXT")
    var image: String? = null,

    @BatchSize(size = 100)
    @OneToMany(
        mappedBy = "follower",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var followers: MutableSet<Follow>? = mutableSetOf(),

    @BatchSize(size = 100)
    @OneToMany(
        mappedBy = "followee",
        fetch = FetchType.LAZY, cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var followees: MutableSet<Follow>? = mutableSetOf(),

//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
//    val bookmarks: MutableList<Bookmark> = mutableListOf()
) : BaseTimeEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Member) return false
        return memberId == other.memberId
    }

    override fun hashCode(): Int {
        return memberId.hashCode()
    }

    companion object {
        fun create(
            nickname: String,
            email: String,
            password: String,
            country: String,
            level: MemberLevel,
            memberRole: MemberRole = MemberRole.USER,
            image: String? = null,
        ): Member {
            return Member(
                nickname = nickname,
                email = email,
                password = password,
                country = country,
                level = level,
                memberRole = memberRole,
                image = image,
            ).apply {
                this.followers = followers
                this.followees = followees
            }
        }
    }
}
