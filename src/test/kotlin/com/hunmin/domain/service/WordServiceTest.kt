package com.hunmin.domain.service

import com.hunmin.domain.dto.word.WordRequestDTO
import com.hunmin.domain.dto.word.WordResponseDTO
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.entity.Word
import com.hunmin.domain.exception.WordException
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.repository.WordRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.*

class WordServiceTest {

    @Mock
    private lateinit var wordRepository: WordRepository

    @Mock
    private lateinit var memberRepository: MemberRepository

    @InjectMocks
    private lateinit var wordService: WordService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    // 단어 등록 테스트
    @Test
    fun createWord() {
        val member = Member(
            memberId = 1L,
            nickname = "tester",
            email = "test@test.com",
            country = "Korea",
            level = MemberLevel.BEGINNER,
            password = "123",
            memberRole = MemberRole.ADMIN
        )

        val wordRequestDTO = WordRequestDTO(
            wordId = 1L,
            memberId = 1L,
            title = "테스트 단어",
            translation = "Test Word",
            definition = "테스트 단어 정의",
            lang = "ko"
        )

        val savedWord = Word(
            wordId = 1L,
            title = "테스트 단어",
            translation = "Test Word",
            definition = "테스트 단어 정의",
            lang = "ko"
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(wordRepository.save(any(Word::class.java))).thenReturn(savedWord)

        val responseDTO = wordService.createWord(wordRequestDTO)

        assertEquals("테스트 단어", responseDTO.title)
        verify(wordRepository, times(1)).save(any(Word::class.java))
    }

    // 단어 수정 테스트
    @Test
    fun updateWord() {
        val member = Member(
            memberId = 1L,
            nickname = "tester",
            email = "test@test.com",
            country = "Korea",
            level = MemberLevel.BEGINNER,
            password = "123",
            memberRole = MemberRole.ADMIN
        )

        val wordRequestDTO = WordRequestDTO(
            wordId = 1L,
            memberId = 1L,
            title = "수정된 단어",
            translation = "Updated Word",
            definition = "수정된 단어 정의",
            lang = "ko",
            originalTitle = "테스트 단어",
            originalLang = "ko"
        )

        val existingWord = Word(
            wordId = 1L,
            title = "테스트 단어",
            translation = "Test Word",
            definition = "테스트 단어 정의",
            lang = "ko"
        )

        val updatedWord = Word(
            wordId = 1L,
            title = "수정된 단어",
            translation = "Updated Word",
            definition = "수정된 단어 정의",
            lang = "ko"
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(wordRepository.findByTitleAndLang(wordRequestDTO.originalTitle!!, wordRequestDTO.originalLang!!))
            .thenReturn(Optional.of(existingWord))
        `when`(wordRepository.save(any(Word::class.java))).thenReturn(updatedWord)

        val responseDTO = wordService.updateWord(wordRequestDTO)

        assertEquals("수정된 단어", responseDTO.title)
        assertEquals("Updated Word", responseDTO.translation)
        assertEquals("수정된 단어 정의", responseDTO.definition)
        verify(wordRepository, times(1)).save(any(Word::class.java))
    }

    // 단어 삭제 테스트
    @Test
    fun deleteWord() {
        val title = "테스트 단어"
        val lang = "ko"
        val member = Member(
            memberId = 1L,
            nickname = "tester",
            email = "test@test.com",
            country = "Korea",
            level = MemberLevel.BEGINNER,
            password = "123",
            memberRole = MemberRole.ADMIN
        )

        val word = Word(
            wordId = 1L,
            title = title,
            translation = "Test Word",
            definition = "테스트 단어 정의",
            lang = lang
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(wordRepository.findByTitleAndLang(title, lang)).thenReturn(Optional.of(word))
        doNothing().`when`(wordRepository).delete(word)

        wordService.deleteWord(title, lang, 1L)

        verify(wordRepository, times(1)).delete(word)
    }

    // 단어 조회 테스트
    @Test
    fun getWord() {
        val title = "테스트 단어"
        val lang = "ko"
        val member = Member(
            memberId = 1L,
            nickname = "tester",
            email = "test@test.com",
            country = "Korea",
            level = MemberLevel.BEGINNER,
            password = "123",
            memberRole = MemberRole.USER
        )

        val word = Word(
            wordId = 1L,
            title = title,
            translation = "Test Word",
            definition = "테스트 단어 정의",
            lang = lang
        )

        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(wordRepository.findByTitleAndLang(title, lang)).thenReturn(Optional.of(word))

        val responseDTO = wordService.getWord(title, lang, 1L)

        assertEquals(word.title, responseDTO.title)
        assertEquals(word.translation, responseDTO.translation)
        assertEquals(word.definition, responseDTO.definition)
    }

    // 전체 단어 조회 테스트
    @Test
    fun getAllWords() {
        val lang = "ko"
        val pageable: Pageable = PageRequest.of(0, 10)
        val member = Member(
            memberId = 1L,
            nickname = "tester",
            email = "test@test.com",
            country = "Korea",
            level = MemberLevel.BEGINNER,
            password = "123",
            memberRole = MemberRole.USER
        )

        val words = listOf(
            Word(wordId = 1L, title = "단어1", translation = "Word1", definition = "정의1", lang = "ko"),
            Word(wordId = 2L, title = "단어2", translation = "Word2", definition = "정의2", lang = "ko")
        )
        val wordsPage = PageImpl(words)
        `when`(memberRepository.findById(1L)).thenReturn(Optional.of(member))
        `when`(wordRepository.findByLang(lang, pageable)).thenReturn(wordsPage)

        val responsePage = wordService.getAllWords(lang, pageable, 1L)

        assertEquals(2, responsePage.size)
        assertEquals("단어1", responsePage.content[0].title)
        assertEquals("단어2", responsePage.content[1].title)
    }
}