package com.hunmin.domain.handler

import com.hunmin.domain.dto.chat.ChatMessageDTO
import com.hunmin.domain.dto.member.CustomUserDetails
import com.hunmin.domain.exception.MemberException
import com.hunmin.domain.jwt.JWTUtil
import com.hunmin.domain.repository.MemberRepository
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import javax.naming.AuthenticationException
//@Component
//class StompHandler(
//    private val jwtUtil: JWTUtil,
//    private val memberRepository: MemberRepository
//) : ChannelInterceptor {
//
//    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
//        val accessor = StompHeaderAccessor.wrap(message)
//
//        // WebSocket 연결 시
//        if (accessor.command == StompCommand.CONNECT) {
//            val jwtToken = accessor.getFirstNativeHeader("Authorization")
//                ?: throw AuthenticationException("Authorization header is missing")
//
//            if (jwtUtil.isExpired(jwtToken)) {
//                val role = jwtUtil.getRole(jwtToken)
//                val chatMessageDTO = message.payload as? ChatMessageDTO
//                    ?: throw IllegalArgumentException("Invalid payload type")
//                val foundMember = memberRepository.findById(chatMessageDTO.memberId)
//                    .orElseThrow(MemberException.NOT_FOUND::get)
//                val customUserDetails = CustomUserDetails(foundMember)
//
//                val authToken = UsernamePasswordAuthenticationToken(
//                    customUserDetails, null, customUserDetails.authorities
//                )
//                SecurityContextHolder.getContext().authentication = authToken
//            } else {
//                throw AuthenticationException("JWT token is expired")
//            }
//        }
//        return message
//    }
//}
@Component
class StompHandler(
    private val jwtUtil: JWTUtil,
    private val memberRepository: MemberRepository
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)

        // WebSocket 연결 시
        if (accessor.command == StompCommand.CONNECT) {
            val jwtToken = accessor.getFirstNativeHeader("Authorization")
                ?: throw AuthenticationException("Authorization header is missing")

            if (!jwtUtil.isExpired(jwtToken)) {
                val email = jwtUtil.getEmail(jwtToken) // JWT에서 이메일 추출
                val foundMember = memberRepository.findByEmail(email)
                    ?: throw MemberException.NOT_FOUND.get()
                val customUserDetails = CustomUserDetails(foundMember)

                val authToken = UsernamePasswordAuthenticationToken(
                    customUserDetails, null, customUserDetails.authorities
                )
                SecurityContextHolder.getContext().authentication = authToken
            } else {
                throw AuthenticationException("JWT token is expired")
            }
        }
        return message
    }
}
