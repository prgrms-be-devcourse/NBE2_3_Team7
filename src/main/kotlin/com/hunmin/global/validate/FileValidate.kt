package com.hunmin.global.validate

import com.hunmin.global.exception.ExceptionCode
import org.springframework.web.multipart.MultipartFile
import java.util.*

class FileValidate {
    companion object {
        // 허용 확장자 설정
        private val IMAGE_EXTENTIONS = listOf("jpg", "jpeg", "png", "gif", "webp")

        // 최대 파일 크기 설정: 3mb
        private val MAX_FILE_SIZE = 3_145_728L

        // 파일 검증
        fun validateImageFile(file: MultipartFile) {

            // 파일이 존재하는지 검증
            if (file.isEmpty) { // .isEmpty(): MultipartFile의 프로퍼티
                throw IllegalArgumentException(ExceptionCode.EMPTY_FILE.message)
            }

            // 파일 크기 검증
            if (file.size > MAX_FILE_SIZE) {
                throw IllegalArgumentException(ExceptionCode.FILE_TOO_LARGE.message)
            }

            // elvis 연산자로 file.originalFilename이 null이 아니면 그래도 사용, null이면 예외 발생
            val originalFilename = file.originalFilename
                ?: throw IllegalArgumentException(ExceptionCode.INVALID_FILE_NAME.message)

            // 파일의 '.' 이후 확장자 부분 소문자로 추출
            val extension = originalFilename.substringAfterLast('.', "").lowercase()

            // 추출한 확장자가 없다면 예외 발생
            if (extension.isEmpty()) {
                throw IllegalArgumentException(ExceptionCode.NOT_EXISTS_FILE_EXTENSION.message)
            }

            // IMAGE_EXTENTIONS에서 지정한 확장자인지 확인
            if (!IMAGE_EXTENTIONS.contains(extension)) {
                throw IllegalArgumentException(ExceptionCode.NOT_SUPPORT_FILE_EXTENSION.message)
            }
            
            // contentType: MultipartFile의 내장 프로퍼티
            // contentType이 image/로 시작하지 않으면 예외 발생
            if (file.contentType?.startsWith("image/") != true) {
                throw IllegalArgumentException(ExceptionCode.INVALID_FILE_TYPE.message)
            }
        }

        fun createUniqueFileName(originalFilename: String): String {
            val extension = originalFilename.substringAfterLast('.', "")
            return "${UUID.randomUUID()}.$extension"
        }
    }
}