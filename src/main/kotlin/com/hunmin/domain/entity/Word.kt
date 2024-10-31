package com.hunmin.domain.entity

import jakarta.persistence.*

@Entity
data class Word (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val wordId: Long = 0,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var lang: String,

    @Column(nullable = false)
    var translation: String,

    @Column(nullable = false)
    var definition: String

) : BaseTimeEntity() {
    fun changeTitle(title: String) {
        this.title = title
    }

    fun changeLang(lang: String) {
        this.lang = lang
    }

    fun changeTranslation(translation: String) {
        this.translation = translation
    }

    fun changeDefinition(definition: String) {
        this.definition = definition
    }
}