package com.hunmin.domain.dto.notice

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.hunmin.domain.entity.Notice
import java.time.LocalDateTime


data class NoticeResponseDTO(
    val noticeId: Long,
    val memberId: Long,
    val title: String,
    val content: String,
    val nickname: String,
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val createdAt: LocalDateTime,
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val updatedAt: LocalDateTime
) {
    constructor(notice: Notice) : this(
        noticeId = notice.noticeId ?: 0L,
        memberId = notice.member?.memberId ?: 0L,
        title = notice.title,
        content = notice.content,
        nickname = notice.member!!.nickname,
        createdAt = notice.createdAt ?: LocalDateTime.now(),
        updatedAt = notice.updatedAt ?: LocalDateTime.now()
    )
}