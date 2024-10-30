package com.hunmin.domain.service

import com.hunmin.domain.dto.chat.ChatMessageDTO
import com.hunmin.domain.repository.ChatMessageRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@TestPropertySource(locations = ["classpath:application-test.properties"])
class ChatMessageServiceTest {

    @Autowired
    private lateinit var chatMessageService: ChatMessageService

    @Autowired
    private lateinit var chatMessageRepository: ChatMessageRepository

    @Test
    fun 채팅메세지발송() {
        //given
        val foundMessage = chatMessageRepository.findById(1).orElseThrow()
        //when
        chatMessageService.sendChatMessage(ChatMessageDTO(foundMessage))
        //then

    }

}