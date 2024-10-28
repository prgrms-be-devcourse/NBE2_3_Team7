package com.hunmin.domain.entity

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

@Entity
data class Board (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val boardId: Long = 0,

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    var member: Member? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var nickname: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    var location: String? = null,

    var latitude: Double? = null,

    var longitude: Double? = null,

    @ElementCollection
    @CollectionTable(name = "board_image_urls", joinColumns = [JoinColumn(name = "board_id")])
    @Column(name = "image_urls", columnDefinition = "TEXT", nullable = false)
    var imageUrls: MutableList<String> = mutableListOf(),

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SUBSELECT)
    var comments: MutableList<Comment> = mutableListOf()

) : BaseTimeEntity() {
    fun changeTitle(title: String) {
        this.title = title
    }

    fun changeContent(content: String) {
        this.content = content
    }

    fun changeLocation(location: String?) {
        this.location = location
    }

    fun changeLatitude(latitude: Double?) {
        this.latitude = latitude
    }

    fun changeLongitude(longitude: Double?) {
        this.longitude = longitude
    }

    fun changeImgUrls(imageUrls: MutableList<String>) {
        this.imageUrls = imageUrls
    }

    override fun hashCode(): Int {
        return boardId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Board) return false
        return boardId == other.boardId
    }

    override fun toString(): String {
        return "Board(boardId=$boardId, title=$title, nickname=$nickname)"
    }
}
