package com.hunmin.domain

import com.hunmin.domain.entity.*
import com.hunmin.domain.repository.ChatMessageRepository
import com.hunmin.domain.repository.ChatRoomRepository
import com.hunmin.domain.repository.FollowRepository
import com.hunmin.domain.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
class Nbe23Team7ApplicationTests {

    @Test
    fun contextLoads() {
    }
}

@Component
class TestTaskExecutor(
    val chatMessageRepository: ChatMessageRepository,
    val chatRoomRepository: ChatRoomRepository,
    val memberRepository: MemberRepository,
    val followRepository: FollowRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        // member 추가
        for (i in 1L..100L) {
            val member = Member(
                nickname = "testMember" + i, password = "1234" + i,
                country = "Korea", email = "test" + i + "@test.com", level = MemberLevel.BEGINNER
            ).apply {
                memberRepository.save(this)
            }
        }
        // follow 추가
        for (i in 1L..99L) {
            val follow = Follow().apply {
                follower = memberRepository.findById(i).get()
                followee = memberRepository.findById(i + 1).get()
            }.apply {
                followRepository.save(this)
            }
        }
        // chatRoom 추가
        for (i in 1L..100) {
            val chatRoom = ChatRoom(member = memberRepository.findById(1).get()).apply {
                chatRoomRepository.save(this)
            }
        }
        // chatMessage 추가
        for (i in 1L..100L) {
            val chatMessage = ChatMessage(
                chatRoom = chatRoomRepository.findById(1).get(),
                member = memberRepository.findById(1).get()
            ).apply {
                message = "testMessage" + i
                chatMessageRepository.save(this)
            }
        }
    }
}


