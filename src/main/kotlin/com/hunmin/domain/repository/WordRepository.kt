package com.hunmin.domain.repository

import com.hunmin.domain.entity.Word
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface WordRepository : JpaRepository<Word, Long> {
    // title 과 lang 조합으로 조회 (조회, 수정, 삭제)
    fun findByTitleAndLang(title: String, lang: String): Optional<Word>

    // 선택한 lang에 해당되는 단어 리스트 반환 (단어 학습)
    fun findByLang(lang: String): List<Word>

    // 선택한 lang에 해당되는 단어 전체 조회 (전체 조회)
    fun findByLang(lang: String, pageable: Pageable): Page<Word>
}