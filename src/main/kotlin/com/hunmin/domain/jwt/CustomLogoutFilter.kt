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

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        doFilter(request as HttpServletRequest, response as HttpServletResponse, chain)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        // 로그아웃 경로에서 오는 POST 요청인지 확인(아니라면 다음 필터로 넘어감)
        logger.info("=== Request URI: {${request.requestURI}}")
        logger.info("=== Request Method: {${request.method}}")
        if (!request.requestURI.matches(Regex("^\\/api/members/logout$")) || request.method != "POST") {
            chain.doFilter(request, response)
            return
        }

        // 쿠키에서 리프레시 토큰 확인
        val refresh = request.cookies
            ?.firstOrNull { it.name == "refresh" }
            ?.value

        // refresh null check
        if (refresh == null) {
            response.status = HttpServletResponse.SC_BAD_REQUEST
            return
        }

        // expired check
        try {
            jwtUtil.isExpired(refresh)
        } catch (e: ExpiredJwtException) {
            //response status code
            response.status = HttpServletResponse.SC_BAD_REQUEST
            return
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        val category = jwtUtil.getCategory(refresh)
        if (category != "refresh") {
            //response status code
            response.status = HttpServletResponse.SC_BAD_REQUEST
            return
        }

        // DB에 토큰이 저정되어 있는지 확인
        val isExist = refreshRepository.existsByRefresh(refresh)
        if (!isExist) {
            response.status = HttpServletResponse.SC_BAD_REQUEST
            return
        }

        // 로그아웃 진행
        // db에서 refresh 토큰 제거
        refreshRepository.deleteByRefresh(refresh)

        // Refresh 토큰 Cookie 값 0
        val cookie = Cookie("refresh", null)
        cookie.maxAge = 0
        cookie.path = "/api/members"

        response.addCookie(cookie)
        response.status = HttpServletResponse.SC_OK
        logger.info("===== 로그아웃 되었습니다.")
    }
}

