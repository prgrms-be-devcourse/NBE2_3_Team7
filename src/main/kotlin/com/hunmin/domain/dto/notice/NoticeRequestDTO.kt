package com.hunmin.domain.dto.notice

import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.Notice
import jakarta.validation.constraints.NotEmpty

data class NoticeRequestDTO(
    @field:NotEmpty(message = "제목을 입력하세요")
    val title: String = "",
    @field:NotEmpty(message = "내용을 입력하세요")
    val content: String = ""
) {
    fun toEntity(): Notice {
        return Notice(
            title = title,
            content = content
        )
    }

    fun toEntity(member: Member?): Notice {
        return Notice(
            member = member,
            title = title,
            content = content
        )
    }
}