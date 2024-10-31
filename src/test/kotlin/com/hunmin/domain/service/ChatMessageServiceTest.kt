package com.hunmin.domain.service

import com.hunmin.domain.dto.chat.ChatMessageDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.MessageType
import com.hunmin.domain.repository.ChatMessageRepository
import com.hunmin.domain.repository.ChatRoomRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class ChatMessageServiceTest {

    @Autowired
    private lateinit var chatRoomRepository: ChatRoomRepository

    @Autowired
    private lateinit var chatMessageService: ChatMessageService

    @Autowired
    private lateinit var chatMessageRepository: ChatMessageRepository

    @Test
    fun 모든채팅기록조회() {
        //given
        val foundChatRoom = chatRoomRepository.findById(1).orElseThrow()
        val foundChatMessage = chatMessageRepository.findById(1).orElseThrow()
        foundChatRoom.chatMessage!!.add(foundChatMessage)
        //when
        val readAllMessages = chatMessageService.readAllMessages(1)
        //then
        assertNotNull(readAllMessages)
        assertEquals(readAllMessages.count().toLong(), 101)
        assertEquals(readAllMessages.first().message, "testMessage1")
    }

    @Test
    fun 채팅조회() {
        //when
        val readChatMessage = chatMessageService.readChatMessage(1)
        //then
        assertNotNull(readChatMessage)
        assertEquals(readChatMessage.message, "testMessage1")
        assertEquals(readChatMessage.chatMessageId, 1)
        assertEquals(readChatMessage.chatRoomId, 1)
        assertEquals(readChatMessage.type, MessageType.TALK)
        assertEquals(readChatMessage.nickName, "testMember1")
    }
    @Test
    fun 채팅수정() {
        //given
        var chatMessageDTO = ChatMessageDTO(chatMessageRepository.findById(1).orElseThrow())
        //when
        chatMessageDTO.message = "updatedMessage1"
        chatMessageDTO.type = MessageType.ENTER

        val updateChatMessage = chatMessageService.updateChatMessage(chatMessageDTO)

        //then
        assertNotNull(updateChatMessage)
        assertEquals(updateChatMessage.message, "updatedMessage1")
        assertEquals(updateChatMessage.chatMessageId, 1)
        assertEquals(updateChatMessage.chatRoomId, 1)
        assertEquals(updateChatMessage.type, MessageType.TALK)
        assertEquals(updateChatMessage.nickName, "testMember1")
    }
    @Test
    fun 채팅삭제() {
        //given
        var chatMessageDTO = ChatMessageDTO(chatMessageRepository.findById(1).orElseThrow())
        //when
        val updateChatMessage = chatMessageService.deleteChatMessage(1)

        //then
        val deletedChat = chatMessageRepository.findById(1)
        assertTrue(deletedChat.isEmpty)
    }
    @Test
    fun 채팅페이징() {
        //given
        var pageRequestDTO = PageRequestDTO(1, 10)

        //when
        val chatPage = chatMessageService.getList(chatRoomId = 1, pageRequestDTO = pageRequestDTO)

        //then
        assertNotNull(chatPage)
        assertEquals(chatPage.totalElements, 100)
        assertEquals(chatPage.size, 10)
    }
}