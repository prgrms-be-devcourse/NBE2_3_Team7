package com.hunmin.domain.service

import com.hunmin.domain.dto.word.WordRequestDTO
import com.hunmin.domain.dto.word.WordResponseDTO
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.entity.Word
import com.hunmin.domain.exception.AdminException
import com.hunmin.domain.exception.WordException
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.repository.WordRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WordService(
    private val wordRepository: WordRepository,
    private val memberRepository: MemberRepository
) {
    // 회원 확인
    private fun checkMember(memberId: Long): Member {
        return memberRepository.findById(memberId).orElseThrow {
            throw AdminException.MEMBER_NOT_FOUND.get()
        }
    }

    // 관리자 확인
    private fun checkAdmin(memberId: Long): Member {
        val member: Member = checkMember(memberId) // 여기서 멤버 확인
        if (member.memberRole != MemberRole.ADMIN) {
            throw WordException.WORD_FORBIDDEN.toException()
        }
        return member
    }

    // 단어 등록
    fun testCreate(wordRequestDTO: WordRequestDTO): WordResponseDTO {
        val member = checkAdmin(wordRequestDTO.memberId ?: throw AdminException.MEMBER_NOT_FOUND.get())

        return try {
            val word: Word = wordRequestDTO.toEntity(member)
            val savedWord = wordRepository.save(word)
            WordResponseDTO(savedWord)
        } catch (e: Exception) {
            throw WordException.WORD_NOT_CREATED.toException()
        }
    }

    // 단어 수정
    fun testUpdate(wordRequestDTO: WordRequestDTO): WordResponseDTO {
        checkAdmin(wordRequestDTO.memberId ?: throw AdminException.MEMBER_NOT_FOUND.get())

        val word = wordRepository.findByTitleAndLang(wordRequestDTO.originalTitle, wordRequestDTO.originalLang)
            .orElseThrow { WordException.WORD_NOT_FOUND.toException() }

        return try {
            word.changeTitle(wordRequestDTO.title)
            word.changeLang(wordRequestDTO.lang)
            word.changeTranslation(wordRequestDTO.translation)
            word.changeDefinition(wordRequestDTO.definition)

            val updatedWord = wordRepository.save(word)

            WordResponseDTO(updatedWord)
        } catch (e: Exception) {
            throw WordException.WORD_NOT_UPDATED.toException()
        }
    }

    // 단어 삭제
    fun testDelete(title: String, lang: String, memberId: Long) {
        checkAdmin(memberId)

        val word = wordRepository.findByTitleAndLang(title, lang)
            .orElseThrow { WordException.WORD_NOT_FOUND.toException() }

        try {
            wordRepository.delete(word)
        } catch (e: Exception) {
            throw WordException.WORD_NOT_DELETED.toException()
        }
    }

    // 단어 조회
    fun getWord(title: String, lang: String, memberId: Long): WordResponseDTO {
        checkMember(memberId)

        val word = wordRepository.findByTitleAndLang(title, lang)
            .orElseThrow { WordException.WORD_NOT_FOUND.toException() }

        return WordResponseDTO(word)
    }

    // 단어 전체 조회
    fun getAllWords(lang: String, pageable: Pageable, memberId: Long): Page<WordResponseDTO> {
        checkMember(memberId)

        val wordsPage = wordRepository.findByLang(lang, pageable)
        return wordsPage.map { WordResponseDTO(it) }
    }
}