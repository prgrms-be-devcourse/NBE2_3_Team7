package com.hunmin.domain.jwt

import com.hunmin.domain.repository.RefreshRepository
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.web.filter.GenericFilterBean

class CustomLogoutFilter(private val jwtUtil: JWTUtil, private val refreshRepository: RefreshRepository) :
    GenericFilterBean() {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private fun sendResponse(response: HttpServletResponse, status: Int, message: String) {
        response.status = status
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write("""{"message": "$message"}""")
    }

    // 여기가 중요! override 키워드를 추가하고 abstract method를 구현
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest && response is HttpServletResponse) {
            doFilterInternal(request, response, chain)
        } else {
            chain.doFilter(request, response)
        }
    }

    // 내부 구현을 별도 메소드로 분리
    private fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        logger.info("=== Request URI: {${request.requestURI}}")
        logger.info("=== Request Method: {${request.method}}")

        if (!request.requestURI.matches(Regex("^\\/api/members/logout$")) || request.method != "POST") {
            chain.doFilter(request, response)
            return
        }

        try {
            val refresh = request.cookies
                ?.firstOrNull { it.name == "refresh" }
                ?.value
                ?: run {
                    sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Refresh token not found")
                    return
                }

            try {
                jwtUtil.isExpired(refresh)
            } catch (e: ExpiredJwtException) {
                sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Expired refresh token")
                return
            }

            if (jwtUtil.getCategory(refresh) != "refresh") {
                sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid token category")
                return
            }

            if (!refreshRepository.existsByRefresh(refresh)) {
                sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token not found in database")
                return
            }

            // 로그아웃 처리
            refreshRepository.deleteByRefresh(refresh)

            // Refresh 토큰 Cookie 제거
            val cookie = Cookie("refresh", null).apply {
                maxAge = 0
                path = "/api/members"
                isHttpOnly = true
                secure = request.isSecure
            }

            response.addCookie(cookie)
            sendResponse(response, HttpServletResponse.SC_OK, "Successfully logged out")
            logger.info("===== 로그아웃 되었습니다.")

        } catch (e: Exception) {
            logger.error("Logout failed", e)
            sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error")
        }
    }
}