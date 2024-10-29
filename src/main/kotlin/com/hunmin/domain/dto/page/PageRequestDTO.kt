package com.hunmin.domain.dto.page

import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class PageRequestDTO(
    @field:Min(1)
    val page: Int = 1,

    @field:Min(10)
    val size: Int = 10
) {
    fun getPageable(sort: Sort): Pageable {
        val pageNum = if (page < 1) 0 else page - 1
        val sizeNum = if (size < 10) 10 else size

        return PageRequest.of(pageNum, sizeNum, sort)
    }
}