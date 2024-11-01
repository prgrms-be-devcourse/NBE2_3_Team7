package com.hunmin.domain.service

import com.hunmin.domain.entity.ChatRoom
import com.hunmin.domain.repository.ChatRoomRepository
import com.hunmin.domain.repository.MemberRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
class ChatRoomServiceTest {

    @Autowired
    private lateinit var memberRepository: MemberRepository
    private lateinit var modelMapper: ModelMapper

    @BeforeEach
    fun setUp() {
        modelMapper = ModelMapper()
    }

    @Autowired
    private lateinit var chatRoomRepository: ChatRoomRepository

    @Autowired
    private lateinit var chatRoomService: ChatRoomService

    @Test
    fun 채팅방조회() {
        //when
        val findRoomById = chatRoomService.findRoomById(1)
        //then
        assertNotNull(findRoomById)
    }

    @Test
    fun `관련 채팅방 조회, 하지만 null일 때`() {
        //when
        val findRoomById = chatRoomService.findRoomByEmail("test5@test.com")
        //then
        assertTrue(findRoomById.isEmpty())
    }

    @Test
    fun `관련 채팅방 조회, 채팅방 존재할 때`() {
        //when
        val findRoomById = chatRoomService.findRoomByEmail("test1@test.com")
        //then
        assertNotNull(findRoomById)
    }

    @Test
    @Transactional
    fun 채팅방생성() {
        //when
        val createdChatRoom = chatRoomService.createChatRoomByNickName("testMember1", "test2@test.com")
        //then
        assertNotNull(createdChatRoom)
        assertEquals(createdChatRoom.chatRoomId, 101)
        assertEquals(createdChatRoom.nickName, "testMember2")
        assertEquals(createdChatRoom.partnerName, "testMember1")
        assertEquals(createdChatRoom.memberId, 2)
    }

    @Test
    @Transactional
    fun 채팅방삭제() {
        //when
        log.info("member1" + memberRepository.findByNickname("testMember1"))
        log.info("member2" + memberRepository.findByEmail("test2@test.com"))
        val chatRoomDTO = chatRoomService.createChatRoomByNickName("testMember2", "test3@test.com")
        var chatRoom = modelMapper.map(chatRoomDTO, ChatRoom::class.java)
        chatRoomRepository.save(chatRoom)
        val result = chatRoomService.deleteChatRoom(chatRoomDTO.chatRoomId, "testMember3", "test2@test.com")
        val foundChatRoom = chatRoomRepository.findById(chatRoomDTO.chatRoomId)
        //then
        assertTrue(result)
        assertTrue(foundChatRoom.isEmpty)
    }

    @Test
    @Transactional
    fun `채팅방 삭제 채팅방 다른ID 실패`() {
        //given
        val chatRoomDTO = chatRoomService.createChatRoomByNickName("testMember2", "test3@test.com")
        var chatRoom = modelMapper.map(chatRoomDTO, ChatRoom::class.java)
        chatRoomRepository.save(chatRoom)
        //when
        assertThrows<NoSuchElementException> {
            val result = chatRoomService.deleteChatRoom(999, "testMember3", "test2@test.com")

            val foundChatRoom = chatRoomRepository.findById(chatRoomDTO.chatRoomId)
            //then
            assertFalse(result)
            assertFalse(foundChatRoom.isEmpty)
        }
    }

    @Test
    @Transactional
    fun `채팅방 삭제 다른 상대방 실패`() {
        //given
        val chatRoomDTO = chatRoomService.createChatRoomByNickName("testMember5", "test6@test.com")
        var chatRoom = modelMapper.map(chatRoomDTO, ChatRoom::class.java)
        chatRoomRepository.save(chatRoom)
        //when
        val result = chatRoomService.deleteChatRoom(chatRoomDTO.chatRoomId, "testMember1", "test5@test.com")
        val foundChatRoom = chatRoomRepository.findById(chatRoomDTO.chatRoomId)
        //then
        assertFalse(result)
        assertFalse(foundChatRoom.isEmpty)
    }
}