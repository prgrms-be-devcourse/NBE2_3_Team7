package com.hunmin.domain.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notices")
@Tag(name = "공지사항", description = "공지사항 CRUD")
class NoticeController {
}