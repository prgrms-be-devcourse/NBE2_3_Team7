package com.hunmin.domain.repository

import com.hunmin.domain.entity.ChatRoom
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
class ChatRoomRepositoryTest {
    @Autowired
    private lateinit var chatMessageRepository: ChatMessageRepository

    @Autowired
    private lateinit var chatRoomRepository: ChatRoomRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var followRepository: FollowRepository

    @Test
    fun 채팅방추가() {
        //given
        val chatRoom = ChatRoom(member=memberRepository.findById(1).get()).apply {
        }
        //when
        val savedChatRoom = chatRoomRepository.save(chatRoom)
        //then
        assertNotNull(savedChatRoom)
    }

    @Test
    fun 채팅방조회() {
        //when
        val foundChatRoom = chatRoomRepository.findById(1).get()
        //then
        assertNotNull(foundChatRoom)
        assertEquals(1, foundChatRoom.chatRoomId)
        assertEquals(1, foundChatRoom.member.memberId)
        assertEquals(1, foundChatRoom.userCount)
    }

    @Test
    fun 채팅방수정() {
        //given
        var foundChatRoom = chatRoomRepository.findById(1).get()
        //when
        foundChatRoom.apply {
            member = memberRepository.findById(2).get()
            chatMessage = null
            userCount = 2
        }
        //then
        assertNotNull(foundChatRoom)
        assertEquals(1, foundChatRoom.chatRoomId)
        assertEquals(2, foundChatRoom.member.memberId)
        assertEquals(2, foundChatRoom.userCount)
        assertEquals(null, foundChatRoom.chatMessage)
    }

    @Test
    fun 채팅방삭제() {
        //when
        chatRoomRepository.deleteById(1)
        var foundChatRoom = chatRoomRepository.findById(1)
        //then
        assertTrue(foundChatRoom.isEmpty)
    }

}

