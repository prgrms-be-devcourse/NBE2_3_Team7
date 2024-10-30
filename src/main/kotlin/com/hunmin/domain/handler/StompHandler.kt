package com.hunmin.domain.handler

import com.hunmin.domain.dto.chat.ChatMessageDTO
import com.hunmin.domain.dto.member.CustomUserDetails
import com.hunmin.domain.entity.Member
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

@Component
class StompHandler : ChannelInterceptor {
    private val jwtUtil: JWTUtil? = null
    private val memberRepository: MemberRepository? = null

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor: StompHeaderAccessor = StompHeaderAccessor.wrap(message)

        //websocket 연결시
        if (StompCommand.CONNECT == accessor.getCommand()) {
            val jwtToken: String = accessor.getFirstNativeHeader("Authorization")
            if (jwtUtil.isExpired(jwtToken)) {
                val role: String = jwtUtil.getRole(jwtToken)
                val chatMessageDTO: ChatMessageDTO = message.payload as ChatMessageDTO
                val foundMember: Member = memberRepository.findById(chatMessageDTO.memberId)
                    .orElseThrow<RuntimeException>(MemberException.NOT_FOUND::get)
                val customUserDetails: CustomUserDetails = CustomUserDetails(foundMember)

                val authToken: Authentication = UsernamePasswordAuthenticationToken(
                    customUserDetails, null,
                    customUserDetails.getAuthorities()
                )
                SecurityContextHolder.getContext().setAuthentication(authToken)
            }
        }
        return message
    }
}
