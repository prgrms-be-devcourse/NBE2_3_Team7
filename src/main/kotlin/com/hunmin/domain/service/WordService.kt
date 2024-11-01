package com.hunmin.domain.service

import com.hunmin.domain.dto.word.WordRequestDTO
import com.hunmin.domain.dto.word.WordResponseDTO
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.exception.AdminException
import com.hunmin.domain.exception.MemberException
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
//    // 관리자 확인
//    private fun checkAdmin(memberId: Long): Member {
//        return memberRepository.findById(memberId).orElseThrow {
//            throw WordException.WORD_FORBIDDEN.toException()
//        }.also { member ->
//            if (member.memberRole != MemberRole.ADMIN) {
//                throw WordException.WORD_FORBIDDEN.toException()
//            }
//        }
//    }
//
//    // 단어 등록
//    fun createWord(wordRequestDTO: WordRequestDTO, memberId: Long): WordResponseDTO {
//        val member = checkAdmin(memberId) // 관리자 확인 및 Member 객체 조회
//
//        return try {
//            val word = wordRequestDTO.toEntity(member) // Member 객체 전달
//            val savedWord = wordRepository.save(word)
//
//            WordResponseDTO(savedWord)
//        } catch (e: Exception) {
//            throw WordException.WORD_NOT_CREATED.toException()
//        }
//    }
    // 관리자 확인
    private fun checkAdmin(memberId: Long) {
        val member = memberRepository.findById(memberId).orElseThrow()
        if (member.memberRole != MemberRole.ADMIN) {
            throw WordException.WORD_FORBIDDEN.toException()
        }
    }

//    // 단어 등록
//    fun createWord(wordRequestDTO: WordRequestDTO, memberId: Long): WordResponseDTO {
//        checkAdmin(memberId)
//
//        return try {
//            val word = wordRequestDTO.toEntity()
//            val savedWord = wordRepository.save(word)
//
//            WordResponseDTO(savedWord)
//        } catch (e: Exception) {
//            throw WordException.WORD_NOT_CREATED.toException()
//        }
//    }


//    // 멤버 확인
//    private fun getMember(username: String): Member {
//        return memberRepository.findByEmail(username) ?: throw MemberException.NOT_FOUND.get()
//    }
//
//    // 단어 등록
//    fun createWord(wordRequestDTO: WordRequestDTO, username: String): WordResponseDTO {
//        val member = getMember(username)
//
//        if (member.memberRole != MemberRole.ADMIN) {
//            throw WordException.WORD_FORBIDDEN.toException()
//        }
//
//        return try {
//            val word = wordRequestDTO.toEntity(member)
//            val savedWord = wordRepository.save(word)
//            WordResponseDTO(savedWord)
//        } catch (e: Exception) {
//            throw WordException.WORD_NOT_CREATED.toException()
//        }
//    }

//    // 단어 수정
//    fun updateWord(wordRequestDTO: WordRequestDTO, memberId: Long): WordResponseDTO {
//        checkAdmin(memberId)
//
//        val word = wordRepository.findByTitleAndLang(wordRequestDTO.title, wordRequestDTO.lang)
//            .orElseThrow { WordException.WORD_NOT_FOUND.toException() }
//
//        return try {
//            word.changeTitle(wordRequestDTO.title)
//            word.changeLang(wordRequestDTO.lang)
//            word.changeTranslation(wordRequestDTO.translation)
//            word.changeDefinition(wordRequestDTO.definition)
//
//            val updatedWord = wordRepository.save(word)
//
//            WordResponseDTO(updatedWord)
//        } catch (e: Exception) {
//            throw WordException.WORD_NOT_UPDATED.toException()
//        }
//    }
//
//    // 단어 삭제
//    fun deleteWord(title: String, lang: String, memberId: Long) {
//        checkAdmin(memberId)
//
//        val word = wordRepository.findByTitleAndLang(title, lang)
//            .orElseThrow { WordException.WORD_NOT_FOUND.toException() }
//
//        try {
//            wordRepository.delete(word)
//        } catch (e: Exception) {
//            throw WordException.WORD_NOT_DELETED.toException()
//        }
//    }
//
    // 단어 조회
    fun getWord(title: String, lang: String): WordResponseDTO {
        val word = wordRepository.findByTitleAndLang(title, lang)
            .orElseThrow { WordException.WORD_NOT_FOUND.toException() }

        return WordResponseDTO(word)
    }

    // 단어 전체 조회
    fun getAllWords(lang: String, pageable: Pageable): Page<WordResponseDTO> {
        val wordsPage = wordRepository.findByLang(lang, pageable)
        return wordsPage.map { WordResponseDTO(it) }
    }

    // -------------------------------------------------------------------------------------------------
    // 테스트 등록
    fun testCreate(wordRequestDTO: WordRequestDTO): WordResponseDTO {
        return try {
            val word = wordRequestDTO.testEntity() // testEntity로 임시 생성
            val savedWord = wordRepository.save(word)
            WordResponseDTO(savedWord)
        } catch (e: Exception) {
            throw WordException.WORD_NOT_CREATED.toException()
        }
    }

    // 테스트 수정
    fun testUpdate(wordRequestDTO: WordRequestDTO): WordResponseDTO {
        val word = wordRepository.findByTitleAndLang(wordRequestDTO.title, wordRequestDTO.lang)
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

    // 테스트 삭제
    fun testDelete(title: String, lang: String) {
        val word = wordRepository.findByTitleAndLang(title, lang)
            .orElseThrow { WordException.WORD_NOT_FOUND.toException() }

        try {
            wordRepository.delete(word)  // 단어 삭제
        } catch (e: Exception) {
            throw WordException.WORD_NOT_DELETED.toException()
        }
    }
}