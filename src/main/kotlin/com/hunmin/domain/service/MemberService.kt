package com.hunmin.domain.service

import com.hunmin.domain.dto.member.MemberDTO
import com.hunmin.domain.dto.member.PasswordFindRequestDto
import com.hunmin.domain.dto.member.PasswordUpdateRequestDto
import com.hunmin.domain.entity.Member
import com.hunmin.domain.entity.MemberLevel
import com.hunmin.domain.entity.MemberRole
import com.hunmin.domain.repository.MemberRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
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

        require(!memberRepository.existsByEmail(memberDTO.email)) { "이미 존재하는 이메일입니다." }

        val member = Member(
            email = email,
            password = bCryptPasswordEncoder.encode(password),
            nickname = memberDTO.nickname,
            country = memberDTO.country,
            memberRole = MemberRole.USER,
            level = memberDTO.level ?: MemberLevel.BEGINNER,
            image = memberDTO.image
        )
        memberRepository.save(member)
    }

    // 회원 정보 업데이트
    fun updateMember(id: Long, memberDTO: MemberDTO) {
        val member = memberRepository.findById(id)
            .orElseThrow { NoSuchElementException("회원 정보를 찾을 수 없습니다: $id") }

        with(member) {
            if (memberDTO.password.isNotEmpty()) {
                password = bCryptPasswordEncoder.encode(memberDTO.password)
            }
            nickname = memberDTO.nickname
            country = memberDTO.country
            level = memberDTO.level
            memberDTO.image?.let { image = it }  // image만 nullable이므로 safe call 사용
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
}