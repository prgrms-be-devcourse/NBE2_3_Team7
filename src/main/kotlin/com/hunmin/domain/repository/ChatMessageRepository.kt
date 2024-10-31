package com.hunmin.domain.repository

import com.hunmin.domain.entity.ChatMessage
import com.hunmin.domain.repository.search.ChatMessageSearch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface ChatMessageRepository : JpaRepository<ChatMessage, Long>, ChatMessageSearch
