package com.hunmin.domain.jwt

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.hunmin.domain.dto.member.CustomUserDetails
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.entity.RefreshEntity
import com.hunmin.domain.repository.RefreshRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetails
import java.io.IOException
import java.util.*

// 로그인 요청 처리 클래스
class LoginFilter(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JWTUtil,
    private val refreshRepository: RefreshRepository
) : UsernamePasswordAuthenticationFilter() {

    companion object{
        private val logger = KotlinLogging.logger {}
    }

    init {
        setFilterProcessesUrl("/api/members/login")
    }

    // 요청에서 email 파라미터 추출
    override fun obtainUsername(request: HttpServletRequest): String? {
        return request.getParameter("email")
    }

    // 위에서 추출한 이메일과 비밀번호 추출하여 인증 시도
    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        logger.info("========= attemptAuthentication 시작 =========")
        try {
            val requestBody = request.reader.readText()
            logger.info("===== Request Body: $requestBody =====")

            val requestMap = ObjectMapper().readValue(
                requestBody,
                object : TypeReference<Map<String, String>>() {}
            )

            val email = requestMap["email"] ?: throw AuthenticationServiceException("이메일이 없습니다.")
            val password = requestMap["password"] ?: throw AuthenticationServiceException("비밀번호가 없습니다.")

            logger.info("===== 이메일: $email =====")
            logger.info("===== 비밀번호: $password =====")

            val authToken = UsernamePasswordAuthenticationToken(email, password)
            authToken.details = WebAuthenticationDetails(request)

            logger.info("===== 인증 토큰 생성: $authToken =====")

            return authenticationManager.authenticate(authToken)
        } catch (e: Exception) {
            logger.error("Authentication failed", e)
            throw AuthenticationServiceException("인증 실패: ${e.message}")
        }
    }

    // 로그인 성공 시 사용자 정보를 기반으로 JWT 토큰을 생성하고, 이를 Authorization 헤더에 추가
    @Throws(IOException::class)
    public override fun successfulAuthentication(
        request: HttpServletRequest, response: HttpServletResponse,
        chain: FilterChain, authentication: Authentication
    ) {
        logger.info("========= successfulAuthentication 시작 =========")

        val customUserDetails: CustomUserDetails = authentication.principal as CustomUserDetails

        //유저 정보
        val email = authentication.name
        // 사용자 권한 정보 추출하고 "ROLE_" 접두사 제거
        val authorities: Collection<GrantedAuthority> = authentication.authorities
        val iterator: Iterator<GrantedAuthority> = authorities.iterator()
        val auth: GrantedAuthority = iterator.next()
        val role: String = auth.getAuthority().replace("ROLE_", "")

        logger.info("===== Authentication 성공!! email: {$email}, Role: {$role}")
        // 클라이언트 전송을 위한 추가 사용자 정보 추출
        val memberId: Long = customUserDetails.getMemberId()
        val nickname: String = customUserDetails.getNickname()
        val image = customUserDetails.getImage()
        val level: MemberLevel = customUserDetails.getLevel()
        val country: String = customUserDetails.getCountry()

        //토큰 생성
        val access = jwtUtil.createJwt("access", email, MemberRole.valueOf(role), 6000000L) // 100분
        val refresh = jwtUtil.createJwt("refresh", email, MemberRole.valueOf(role), 86400000L) // 24시간
        logger.info("생성된 access 토큰: $access")
        logger.info("생성된 refresh 토큰: $refresh")


        // refresh 토큰 저장
        addRefreshEntity(email, refresh, 86400000L)

        response.apply {
            contentType = "application/json"
            characterEncoding = "UTF-8"
            writer.write(
            """
                {
                    "token": "$access",
                    "refreshToken": "$refresh",
                    "memberId": $memberId,
                    "role": "$role",
                    "nickname": "$nickname",
                    "image": "$image",
                    "email": "$email",
                    "level": "$level",
                    "country": "$country"
                }
            """.trimIndent()
            )
            logger.info("===== 응답 완료 =====")
        }

        //응답 설정
        response.setHeader("access", access)
        response.addCookie(createCookie("refresh", refresh))
        response.setStatus(HttpStatus.OK.value())
    }

    private fun addRefreshEntity(email: String, refresh: String, expiredMs: Long) {
        val date = Date(System.currentTimeMillis() + expiredMs)

        RefreshEntity().apply {
            this.email = email
            this.refresh = refresh
            this.expiration = date.toString()
        }.let { refreshRepository.save(it) }
    }

    // 로그인 실패 시 HTTP 응답 401로 설정(유효한 자격 증명 미제공 시 요청 거부)
    public override fun unsuccessfulAuthentication(
        request: HttpServletRequest, response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        response.setStatus(401)
        logger.info("===== 인증 실패 =====")
    }

    private fun createCookie(key: String, value: String): Cookie {
        // key와 jwt를 매개로 받아 cookie 생성
        val cookie = Cookie(key, value)
        cookie.maxAge = 24 * 60 * 60
        cookie.isHttpOnly = true
        return cookie
    }
}
