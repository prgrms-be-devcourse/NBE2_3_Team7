package com.hunmin.domain.jwt

import com.hunmin.domain.dto.member.CustomUserDetails
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.service.MemberService
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

// 주 생성자로 jwtUtil, memberService 주입
class JWTFilter(private val jwtUtil: JWTUtil, memberService: MemberService) : OncePerRequestFilter() {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // kotlin-logging 설정
        logger.info("=== JWTFilter - Request URI: {${request.getRequestURI()}}")
        logger.info("=== JWTFilter - Request Method: {${request.getMethod()}}")
        logger.info("=== JWTFilter - Access Token: {${request.getHeader("Authorization")}}")

        // 비밀번호 찾기/변경 관련, reissue 엔드포인트 요청 필터 제외
        if (request.requestURI.startsWith("/api/members/password/") || request.requestURI == "/api/members/reissue") {
            filterChain.doFilter(request, response)
            return
        }

        logger.info("*********************")
        logger.info(request.getHeader("Authorization"))

        val authorizationHeader: String = request.getHeader("Authorization")
        if (authorizationHeader.startsWith("Bearer ")) {
            val accessToken = authorizationHeader.substring(7)

            logger.info(accessToken)
            logger.info("&&&&&&&&&")
            logger.info(jwtUtil.getRole(accessToken))

            // 토큰이 있다면,
            // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음: return
            try {
                jwtUtil.isExpired(accessToken)
            } catch (e: ExpiredJwtException) {
                // 만료되면 다음 필터로 넘기지 않고 만료됐다는 메세지 출력: response body
                response.apply {
                    writer.print("===== 액세스 토큰 만료 =====")
                    status = HttpServletResponse.SC_UNAUTHORIZED
                }
                return
            }

            // 토큰이 만료가 안되었으면,
            // 토큰이 access인지 확인 (발급시 페이로드에 명시)
            if (jwtUtil.getCategory(accessToken) != "access") {
                // response body
                response.apply{
                    writer.print("유효한 토큰이 아닙니다.")
                    status = HttpServletResponse.SC_UNAUTHORIZED
                }
            }

            // 토큰에서 email, role 값으로 일시적인 세션 생성
            val email = jwtUtil.getEmail(accessToken)
            // role 값에서 "ROLE_" 제거
            val role = jwtUtil.getRole(accessToken).removePrefix("ROLE_")

            logger.info("===========================")
            logger.info(email)

            val member: Member = Member.create(
                email = email,
                memberRole = MemberRole.valueOf(role)
            )

            val customUserDetails = CustomUserDetails(member)
            val authToken: Authentication =
                UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())

            SecurityContextHolder.getContext().authentication = authToken
        }
        filterChain.doFilter(request, response)
    }
}
