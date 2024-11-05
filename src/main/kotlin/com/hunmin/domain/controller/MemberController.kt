package com.hunmin.domain.controller

import com.hunmin.domain.dto.member.MemberDTO
import com.hunmin.domain.dto.member.MemberUpdateDTO
import com.hunmin.domain.dto.member.PasswordFindRequestDto
import com.hunmin.domain.dto.member.PasswordUpdateRequestDto
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.entity.RefreshEntity
import com.hunmin.domain.jwt.JWTUtil
import com.hunmin.domain.repository.RefreshRepository
import com.hunmin.domain.service.MemberService
import com.hunmin.global.s3.S3FileManagement
import io.jsonwebtoken.ExpiredJwtException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

// 회원 가입, 회원 정보 수정 컨트롤러 구현
@RestController
@RequestMapping("/api/members")
@Tag(name = "회원", description = "회원 CRUD")
class MemberController(
    private val memberService: MemberService,
    private val jwtUtil: JWTUtil,
    private val refreshRepository: RefreshRepository,
    private val s3FileManagement: S3FileManagement

    ) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @PostMapping("/uploads")
    @Operation(summary = "프로필 사진 등록", description = "회원 가입 시 프로필 사진을 등록할 때 사용하는 API")
    fun uploadImage(@RequestParam("image") image: MultipartFile): ResponseEntity<String> {
        return try {
            val imageUrl = s3FileManagement.uploadImage(image)
            logger.info("=== 이미지 업로드 성공: $imageUrl ===")
            ResponseEntity.ok(imageUrl)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패")
        }
    }

    @PostMapping("/register")
    @Operation(summary = "회원 가입", description = "회원 가입할 때 사용하는 API")
    fun registerProcess(
        @RequestPart(value="memberInfo") memberDTO: MemberDTO,
        @RequestPart(value="profileImage", required = false) profileImage: MultipartFile?
    ): ResponseEntity<String> {
        return try {
            logger.info("=== 회원가입 시작: ${memberDTO.email} ===")
            profileImage?.let {
                val imageUrl = s3FileManagement.uploadImage(it)  // S3FileManagement 사용
                memberDTO.image = imageUrl
                logger.info("=== 프로필 이미지 업로드 완료: $imageUrl ===")
            }
            memberService.registerProcess(memberDTO)
            logger.info("=== 회원가입 성공 ===")

            ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 완료")
        } catch (e: Exception) {
            logger.error("=== 회원가입 실패 (유효성 검사): ${e.message} ===")
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }

    @PutMapping("/{memberId}")
    fun updateMember(
        @PathVariable memberId: Long,
        @RequestPart("memberInfo") updateDTO: MemberUpdateDTO,
        @RequestPart("profileImage", required = false) newProfileImage: MultipartFile?,
    ): ResponseEntity<String> {
        return try {
            // 이미지가 있으면 먼저 업로드
            newProfileImage?.let {
                val imageUrl = s3FileManagement.uploadImage(it)
                updateDTO.image = imageUrl
                logger.info("=== 새 프로필 이미지 업로드 완료: $imageUrl ===")
            }

            memberService.updateMember(memberId, updateDTO)
            logger.info("=== 회원정보 수정 성공 ===")

            ResponseEntity.ok("회원정보가 수정되었습니다")
        } catch (e: Exception) {
            logger.error("=== 회원 정보 수정 실패: ${e.message} ===")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("회원정보 수정 실패: ${e.message}")
        }
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "refresh token으로 access token 재발급하는 API")
    fun reissueToken(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        // refresh token을 쿠키에서 추출
        val cookies = request.cookies
        val refresh = if (cookies != null) {
            var refreshToken: String? = null
            for (cookie in cookies) {
                if (cookie.name == "refresh") {
                    refreshToken = cookie.value
                    break
                }
            }
            refreshToken
        } else null

        if (refresh == null) {
            return ResponseEntity.badRequest().body("refresh token이 없습니다.")
        }

        return try {
            // refresh token 만료 확인
            jwtUtil.isExpired(refresh)

            // 토큰이 refresh token인지 category 확인
            val category = jwtUtil.getCategory(refresh)
            if (category != "refresh") {
                return ResponseEntity.badRequest().body("잘못된 refresh token 입니다.")
            }

            // DB에 저장되어 있는지 확인
            val isExist = refreshRepository.existsByRefresh(refresh)
            if (!isExist) {
                return ResponseEntity.badRequest().body("Refresh Token이 유효하지 않습니다.")
            }

            // 토큰에서 유저 정보 추출
            val email = jwtUtil.getEmail(refresh)
            val role = jwtUtil.getRole(refresh).replace("ROLE_", "")

            // 새로운 토큰 발급
            val newAccess = jwtUtil.createJwt("access", email, MemberRole.valueOf(role), 6000000L) // 100분
            val newRefresh = jwtUtil.createJwt("refresh", email, MemberRole.valueOf(role), 86400000L) // 24시간

            // DB 업데이트
            refreshRepository.deleteByRefresh(refresh)
            addRefreshEntity(email, newRefresh, 86400000L) // 24시간

            // 응답 설정
            response.setHeader("access", newAccess)
            response.addCookie(createCookie("refresh", newRefresh))

            ResponseEntity.ok("토큰이 재발급되었습니다")

        } catch (e: ExpiredJwtException) {
            ResponseEntity.badRequest().body("refresh token이 만료되었습니다.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 재발급 실패")
        }
    }

    @PostMapping("/password/verify")
    fun verifyUser(@RequestBody passwordFindRequestDto: PasswordFindRequestDto): ResponseEntity<*> {
        return memberService.verifyUserForPasswordReset(passwordFindRequestDto)
    }

    @PostMapping("/password/update")
    fun updatePassword(@RequestBody passwordUpdateRequestDto: PasswordUpdateRequestDto): ResponseEntity<*> {
        return memberService.updatePassword(passwordUpdateRequestDto)
    }

    private fun createCookie(key: String, value: String): Cookie {
        val cookie = Cookie(key, value)
        cookie.maxAge = 24 * 60 * 60
        cookie.isHttpOnly = true
        return cookie
    }

    private fun addRefreshEntity(email: String, refresh: String, expiredMs: Long) {
        val date = Date(System.currentTimeMillis() + expiredMs)
        val refreshEntity = RefreshEntity()
        refreshEntity.email = email
        refreshEntity.refresh = refresh
        refreshEntity.expiration = date.toString()
        refreshRepository.save(refreshEntity)
    }
}