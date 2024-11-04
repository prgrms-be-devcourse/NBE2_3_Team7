package com.hunmin.domain.service

import com.hunmin.domain.dto.member.*
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.repository.MemberRepository
import com.hunmin.global.s3.S3FileManagement
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val s3FileManagement: S3FileManagement,
) : UserDetailsService {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    // 이미지 업로드
    @Throws(IOException::class)
    fun uploadImage(file: MultipartFile): String {
        val uploadDir = Paths.get("uploads").toAbsolutePath().normalize().toString()
        val directory = File(uploadDir)

        if (!directory.exists()) {
            val created = directory.mkdirs()
            if (!created) {
                throw IOException("Failed to create directory")
            }
        }

        val fileName = "${UUID.randomUUID()}.${getFileExtension(file.originalFilename)}"
        val filePath = Paths.get(uploadDir, fileName)
        Files.copy(file.inputStream, filePath)

        return "/uploads/$fileName"
    }

    // 파일 확장자 추출
    private fun getFileExtension(fileName: String?): String {
        if (fileName == null || !fileName.contains(".")) {
            throw IllegalArgumentException("Invalid file name: $fileName")
        }

        return fileName.substring(fileName.lastIndexOf('.') + 1)
    }

    // 회원 가입
    fun registerProcess(memberDTO: MemberDTO) {
        val email = memberDTO.email
        val password = memberDTO.password

        logger.info("=== 회원가입 서비스 시작 ===")
        logger.info("Email: ${memberDTO.email}")

        require(!memberRepository.existsByEmail(memberDTO.email)) { "이미 존재하는 이메일입니다." }

        val member = Member(
            email = email,
            password = bCryptPasswordEncoder.encode(password),
            nickname = memberDTO.nickname,
            country = memberDTO.country,
            memberRole = MemberRole.USER,
            level = memberDTO.level,
            image = memberDTO.image
        )
        logger.info("Member 엔티티 생성: $member")

        memberRepository.save(member)
        logger.info("=== DB 저장 완료 ===")
    }

    // 회원 정보 업데이트
    fun updateMember(id: Long, updateDTO: MemberUpdateDTO) {
        val member = memberRepository.findById(id)
            .orElseThrow { NoSuchElementException("회원 정보를 찾을 수 없습니다: $id") }

        // null이 아닌 필드만 업데이트
        with(member) {
            updateDTO.password?.let {
                password = bCryptPasswordEncoder.encode(it)
            }
            updateDTO.nickname?.let { nickname = it }
            updateDTO.country?.let { country = it }
            updateDTO.level?.let { level = MemberLevel.valueOf(it) }

            // 새 이미지 URL이 있는 경우
            updateDTO.image?.let { newImageUrl ->
                // 기존 이미지가 있다면 S3에서 삭제
                image?.let { oldImageUrl ->
                    try {
                        s3FileManagement.delete(oldImageUrl)
                    } catch (e: Exception) {
                        logger.error("기존 이미지 삭제 실패: ${e.message}")
                    }
                }
                image = newImageUrl
            }
        }

        memberRepository.save(member)
    }

    fun readUserInfo(email: String): MemberDTO =
        MemberDTO.from(memberRepository.findByEmail(email))

    // 비밀번호 재설정을 위한 사용자 검증
    fun verifyUserForPasswordReset(passwordFindRequestDto: PasswordFindRequestDto): ResponseEntity<*> =
        if (memberRepository.existsByEmailAndNickname(
                passwordFindRequestDto.email,
                passwordFindRequestDto.nickname
            )
        ) {
            ResponseEntity.ok("사용자 확인 완료")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.")
        }

    // 비밀번호 재설정
    fun updatePassword(passwordUpdateRequestDto: PasswordUpdateRequestDto): ResponseEntity<*> {
        return try {
            val member = memberRepository.findUserByEmailAndNickname(
                passwordUpdateRequestDto.email,
                passwordUpdateRequestDto.nickname
            )
                ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")

            val encodedPassword = bCryptPasswordEncoder.encode(passwordUpdateRequestDto.newPassword)
            member.password = encodedPassword
            memberRepository.save(member)

            ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.")
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
        }
    }

    override fun loadUserByUsername(email: String): UserDetails {
        val member = memberRepository.findByEmail(email)
        return CustomUserDetails(member)
    }
}