package com.hunmin.domain.jwt;

import com.hunmin.domain.entity.MemberRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import io.jsonwebtoken.Jwts
import java.util.*

@Component
class JWTUtil(
    @Value("\${spring.jwt.secret}")
    private val secret: String
) {
    private val secretKey: SecretKey = SecretKeySpec(
        secret.toByteArray(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().algorithm
    )

    // JWT 토큰에서 email 정보 추출
    fun getEmail(token: String): String {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .get("email", String::class.java)
    }

    // JWT 토큰에서 role 정보 추출
    fun getRole(token: String): String {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .get("role", String::class.java)
    }

    // JWT 토큰 만료 여부 확인
    fun isExpired(token: String): Boolean {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .expiration
                .before(Date())
    }

    // 토큰 구분을 위한 카테고리
    fun getCategory(token: String): String {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .get("category", String::class.java)
    }

    // 이메일과 역할 정보 기반으로 JWT 토큰 생성
    fun createJwt(category: String, email: String, role:MemberRole, expiredMs: Long): String {
        return Jwts.builder()
                .claim("category", category)
                .claim("email", email)
                .claim("role", "ROLE_${role.name}")
                .issuedAt(Date(System.currentTimeMillis()))
                .expiration(Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact()
    }
}