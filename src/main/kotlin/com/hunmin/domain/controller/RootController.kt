package com.hunmin.domain.controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RootController {

    @GetMapping("/health")
    fun healthCheck(): String {
        return "health"
    }
}