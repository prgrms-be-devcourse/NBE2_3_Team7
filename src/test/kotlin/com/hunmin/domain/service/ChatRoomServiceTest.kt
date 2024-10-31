package com.hunmin.domain.service

import com.hunmin.domain.repository.ChatRoomRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class ChatRoomServiceTest {

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
    fun 관련채팅방조회null일때() {
        //when
        val findRoomById = chatRoomService.findRoomByEmail("test1@test.com")
        //then
        assertTrue(findRoomById.isEmpty())
    }

    @Test
    fun 관련채팅방조회깂존재시() {
        //when
        val findRoomById = chatRoomService.findRoomByEmail("test1@test.com")
        //then
        assertNotNull(findRoomById)
    }

    @Test
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
    fun 채팅방삭제() {
        //when
        val createdChatRoom = chatRoomService.createChatRoomByNickName("testMember1", "test2@test.com")
        val result = chatRoomService.deleteChatRoom(101, "testMember1", "test2@test.com")
        val foundChatRoom = chatRoomRepository.findById(101)
        //then
        assertTrue(result)
        assertTrue(foundChatRoom.isEmpty)
    }

    @Test
    fun 채팅방삭제실패() {
        //when
        val createdChatRoom = chatRoomService.createChatRoomByNickName("testMember1", "test2@test.com")
        val result = chatRoomService.deleteChatRoom(101, "testMember2", "test2@test.com")
        //then
        assertFalse(result)
    }
}