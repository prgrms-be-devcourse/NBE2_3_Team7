package com.hunmin.domain.entity

import jakarta.persistence.*


@Entity
data class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val noticeId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,
) : BaseTimeEntity() {

    fun changeMember(member: Member?) {
        this.member = member
    }

    fun changeTitle(title: String) {
        this.title = title
    }

    fun changeContent(content: String) {
        this.content = content
    }

    //빌더패턴처럼 사용
    companion object {
        fun create(
            member: Member?,
            title: String,
            content: String
        ): Notice {
            return Notice(
                member = member,
                title = title,
                content = content
            )
        }
    }
}