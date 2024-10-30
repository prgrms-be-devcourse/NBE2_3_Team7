package com.hunmin.domain.dto.word

import com.hunmin.domain.entity.Word
import java.time.LocalDateTime

data class WordResponseDTO(
    val wordId: Long,
    val title: String,
    val lang: String,
    val translation: String,
    val definition: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,

    var displayTitle: String,
    var displayTranslation: String
) {
    constructor(word: Word) : this(
        wordId = word.wordId,
        title = word.title,
        lang = word.lang,
        translation = word.translation,
        definition = word.definition,
        createdAt = word.createdAt,
        updatedAt = word.updatedAt,
        displayTitle = word.title,
        displayTranslation = word.translation
    )

}