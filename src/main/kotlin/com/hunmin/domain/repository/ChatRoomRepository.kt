package com.hunmin.domain.repository

import com.hunmin.domain.entity.ChatRoom
import com.hunmin.domain.entity.Member
import com.hunmin.domain.repository.search.ChatRoomSearch
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository : JpaRepository<ChatRoom, Long>,ChatRoomSearch
