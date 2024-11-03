package com.hunmin.global.s3

import com.hunmin.global.exception.ExceptionCode
import com.hunmin.global.validate.FileValidate
import com.hunmin.global.validate.FileValidate.Companion.createUniqueFileName
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Component
class S3FileManagement(
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    @Value("\${cloud.aws.region.static}")
    private val region: String,
    private val s3Client: S3Client, // AmazonS3는 v1, S3Client는 v2 최신 버전 사용을 권장
) {
    fun uploadImage(multipartFile: MultipartFile): String {

        // 파일 검증
        FileValidate.validateImageFile(multipartFile)

        // 원본 파일명을 추출해서 고유한 파일명으로 변경
        val originalFilename = multipartFile.originalFilename
            ?: throw IllegalArgumentException(ExceptionCode.INVALID_FILE_NAME.message)

        val fileName: String = createUniqueFileName(originalFilename)

        try {

            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(multipartFile.contentType)
                .build()

            val requestBody = RequestBody.fromInputStream(
                multipartFile.inputStream,
                multipartFile.size
            )

            s3Client.putObject(putObjectRequest, requestBody)

            return getFile(fileName)
        } catch (e: Exception) {
            throw IllegalArgumentException(ExceptionCode.FILE_UPLOAD_ERROR.message)
        }
    }

    private val baseUrl = "https://$bucket.s3.$region.amazonaws.com"

    fun getFile(fileName: String): String {
        return "$baseUrl/$fileName"
    }

    fun delete(fileName: String) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(fileName)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }
}