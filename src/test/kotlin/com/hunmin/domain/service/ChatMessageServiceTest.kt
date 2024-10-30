package com.hunmin.domain.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ChatMessageServiceTest {

    @Mock

    @InjectMocks
    private lateinit var chatMessageService : ChatMessageService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

}