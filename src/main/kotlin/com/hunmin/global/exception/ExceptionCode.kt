package com.hunmin.global.exception

import org.springframework.http.HttpStatus

enum class ExceptionCode (
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "F001", "파일이 비어있습니다"),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "F002", "파일 크기가 제한을 초과했습니다"),
    NOT_EXISTS_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "F003", "파일 확장자가 없습니다"),
    NOT_SUPPORT_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "F004", "지원하지 않는 파일 형식입니다"),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "F005", "잘못된 파일명입니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "F006", "잘못된 파일 타입입니다"),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F007", "파일 업로드에 실패했습니다");
}
