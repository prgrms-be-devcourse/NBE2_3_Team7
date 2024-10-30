package com.hunmin.domain.repository.search

import com.hunmin.domain.dto.board.BoardResponseDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BoardSearch {
    fun searchBoard(pageable: Pageable, title: String): Page<BoardResponseDTO>
}
