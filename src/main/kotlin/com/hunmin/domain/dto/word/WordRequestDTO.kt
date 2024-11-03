package com.hunmin.domain.dto.word

import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.Word
import jakarta.validation.constraints.NotBlank

data class WordRequestDTO(
    var wordId: Long,
    var memberId: Long,

    @field:NotBlank(message = "명칭은 필수 입력값입니다.")
    var title: String,

    @field:NotBlank(message = "뜻은 필수 입력값입니다.")
    var translation: String,

    @field:NotBlank(message = "정의는 필수 입력값입니다.")
    var definition: String,

    @field:NotBlank(message = "언어는 필수 입력값입니다.")
    var lang: String,

    var originalTitle: String,
    var originalLang: String
) {
    fun toEntity(member: Member): Word {
        return Word(
            member = member, // 추가
            title = title,
            translation = translation,
            definition = definition,
            lang = lang
        )
    }
}