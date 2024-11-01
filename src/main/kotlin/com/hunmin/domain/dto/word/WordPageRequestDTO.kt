package com.hunmin.domain.dto.word

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class WordPageRequestDTO(
    @field:Min(1) var page: Int = 1,
    @field:Min(1) @field:Max(100) var size: Int = 25
){
    fun getPageable(sort: Sort): Pageable {
        return PageRequest.of(page - 1, size, sort)
    }
}