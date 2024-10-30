package com.hunmin.domain.repository

import com.hunmin.domain.entity.ChatMessage
import com.hunmin.domain.entity.ChatRoom
import com.hunmin.domain.entity.MessageType
import com.hunmin.domain.entity.QChatMessage.chatMessage
import com.hunmin.domain.entity.QChatRoom.chatRoom
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class ChatMessageRepositoryTest {
    @Autowired
    private lateinit var chatMessageRepository: ChatMessageRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var chatRoomRepository: ChatRoomRepository

    @Test
    fun 채팅추가() {
        //given
        val chatMessage = ChatMessage(chatRoom = chatRoomRepository.findById(1).get()
                                    , member=memberRepository.findById(1).get())
        chatMessage.message = "testMessage123"
        //when
        val savedChatMessage = chatMessageRepository.save(chatMessage)
        //then
        assertNotNull(savedChatMessage)
        assertEquals("testMessage123", savedChatMessage.message)
    }

    @Test
    fun 채팅조회() {
        //when
        val foundChatMessage = chatMessageRepository.findById(1).get()
        //then
        assertNotNull(foundChatMessage)
        assertEquals(1, foundChatMessage.chatMessageId)
        assertEquals(1, foundChatMessage.member.memberId)
        assertEquals(1, foundChatMessage.chatRoom.chatRoomId)
        assertEquals("testMessage1", foundChatMessage.message)
        assertEquals(MessageType.TALK, foundChatMessage.type)
    }

    @Test
    fun 채팅수정() {
        //given
        var foundChatMessage = chatMessageRepository.findById(1).get()
        //when
        foundChatMessage.apply {
            member = memberRepository.findById(2).get()
            chatRoom = chatRoomRepository.findById(2).get()
            message = "testMessage1234"
            type =MessageType.QUIT
        }
        //then
        assertNotNull(foundChatMessage)
        assertEquals(2, foundChatMessage.chatRoom.chatRoomId)
        assertEquals(2, foundChatMessage.member.memberId)
        assertEquals("testMessage1234", foundChatMessage.message)
        assertEquals(MessageType.QUIT, foundChatMessage.type)
    }

    @Test
    fun 채팅삭제() {
        //when
        chatMessageRepository.deleteById(1)
        var foundChatMessage = chatMessageRepository.findById(1)
        //then
        assertTrue(foundChatMessage.isEmpty)
    }

}