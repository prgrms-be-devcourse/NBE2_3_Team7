package com.hunmin.domain.repository.search

import com.hunmin.domain.dto.follow.FollowRequestDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FollowSearch {
    fun getFollowPage(followId: Long, pageable: Pageable): Page<FollowRequestDTO>
}
