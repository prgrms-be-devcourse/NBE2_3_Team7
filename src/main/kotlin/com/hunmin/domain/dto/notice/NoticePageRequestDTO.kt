package com.hunmin.domain.dto.notice

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class NoticePageRequestDTO {
    @Min(1)
    private val page = 1

    @Min(20)
    @Max(100)
    private val size = 20

    fun getPageable(sort: Sort): Pageable {
        return PageRequest.of(page - 1, size, sort)
    }
}