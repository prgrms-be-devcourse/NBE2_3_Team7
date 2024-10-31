package com.hunmin.domain

import com.hunmin.domain.entity.*
import com.hunmin.domain.entity.QFollow.follow
import com.hunmin.domain.repository.ChatMessageRepository
import com.hunmin.domain.repository.ChatRoomRepository
import com.hunmin.domain.repository.FollowRepository
import com.hunmin.domain.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component

@SpringBootTest
class Nbe23Team7ApplicationTests {

	@Test
	fun contextLoads() {
	}
}
@Component
class TestTaskExecutor: ApplicationRunner {
	@Autowired
	private lateinit var chatMessageRepository: ChatMessageRepository
	@Autowired
	private lateinit var chatRoomRepository: ChatRoomRepository
	@Autowired
	private lateinit var memberRepository: MemberRepository
	@Autowired
	private lateinit var followRepository: FollowRepository

	override fun run(args: ApplicationArguments?) {
		// member 추가
		for(i in 1L..100L) {
			val member = Member(nickname = "testMember"+i, password = "1234"+i,
				country = "Korea", email = "test"+i+"@test.com", level = MemberLevel.BEGINNER).apply{
			}
			memberRepository.save(member)
		}
		// follow 추가
		for(i in 1L..99L) {
			val follow = Follow().apply{
				follower =memberRepository.findById(i).get()
				followee =memberRepository.findById(i+1).get()
			}
			followRepository.save(follow)
		}
		// chatRoom 추가
		for(i in 1L..100) {
			val chatRoom = ChatRoom(member = memberRepository.findById(1).get()).apply{
			}
			chatRoomRepository.save(chatRoom)
		}
		// chatMessage 추가
		for(i in 1L..100L) {
			val chatMessage = ChatMessage(chatRoom = chatRoomRepository.findById(1).get(),
											member = memberRepository.findById(1).get()).apply{
				message = "testMessage"+i
			}
			chatMessageRepository.save(chatMessage)
		}
	}
}
