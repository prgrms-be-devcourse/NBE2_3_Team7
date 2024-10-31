package com.hunmin.domain.dto.word

import com.hunmin.domain.entity.Word
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class WordRequestDTO (
    @field:NotNull(message = "단어 ID는 필수항목입니다.")
    val wordId: Long,

    @field:NotBlank(message = "명칭은 필수 입력값입니다.")
    var title: String,

    @field:NotBlank(message = "뜻은 필수 입력값입니다.")
    var translation: String,

    @field:NotBlank(message = "정의는 필수 입력값입니다.")
    var definition: String,

    @field:NotBlank(message = "언어는 필수 입력값입니다.")
    var lang: String
) {
    fun toEntity(): Word {
        return Word(
            title = title,
            translation = translation,
            definition = definition,
            lang = lang
        )
    }
}