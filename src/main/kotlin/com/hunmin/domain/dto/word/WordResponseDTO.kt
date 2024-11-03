package com.hunmin.domain.dto.word

import com.hunmin.domain.entity.Word
import java.time.LocalDateTime

data class WordResponseDTO(
    val wordId: Long,
    var title: String,
    var lang: String,
    var translation: String,
    var definition: String,
    val createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null,

    var displayTitle: String = "",
    var displayTranslation: String = "",
    var displayTime: Long = 0
) {
    constructor(word: Word) : this(
        wordId = word.wordId,
        title = word.title,
        lang = word.lang,
        translation = word.translation,
        definition = word.definition,
        createdAt = word.createdAt,
        updatedAt = word.updatedAt
    )
}