package com.hunmin.domain.jwt

import com.hunmin.domain.dto.member.CustomUserDetails
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.domain.service.MemberService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

// 주 생성자로 jwtUtil, memberRepository 주입
class JWTFilter(
    private val jwtUtil: JWTUtil,
    private val memberRepository: MemberRepository
) : OncePerRequestFilter() {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    // 인증이 필요없는 경로들
    private val excludedUrls = listOf(
        "/api/members/register",
        "/api/members/login",
        "/api/members/password",
        "/api/members/reissue"
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.info("=== JWTFilter - Request URI: {${request.requestURI}}")
        logger.info("=== JWTFilter - Request Method: {${request.method}}")
        logger.info("=== JWTFilter - Access Token: {${request.getHeader("Authorization")}}")

        // 비밀번호 찾기/변경 관련, reissue 엔드포인트 요청 필터 제외
        if (request.requestURI.startsWith("/api/members/password/") || request.requestURI == "/api/members/reissue") {
            filterChain.doFilter(request, response)
            return
        }

        // 제외할 URL인 경우 바로 통과
        val currentPath = request.requestURI
        if (excludedUrls.any { currentPath.startsWith(it) }) {
            logger.info("=== 인증 제외 경로: $currentPath ===")
            filterChain.doFilter(request, response)
            return
        }

        val authorizationHeader: String? = request.getHeader("Authorization")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val accessToken = authorizationHeader.substring(7)

            try {
                if (!jwtUtil.isExpired(accessToken) && jwtUtil.getCategory(accessToken) == "access") {
                    val email = jwtUtil.getEmail(accessToken)
                    val role = jwtUtil.getRole(accessToken)
                    val member = memberRepository.findByEmail(email)
                    val customUserDetails = CustomUserDetails(member)
                    val authToken = UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                    )

                    SecurityContextHolder.getContext().authentication = authToken
                }
            } catch (e: Exception) {
                logger.error("Token validation failed", e)
                response.apply {
                    status = HttpServletResponse.SC_UNAUTHORIZED
                    writer.write("토큰 검증 실패: ${e.message}")
                }
                return
            }
        }
        filterChain.doFilter(request, response)
    }
}
