package com.hunmin.domain.exception

enum class AdminException(
    private val message: String,
    private val code: Int
) {
    MEMBER_NOT_FOUND("회원이 존재하지 않습니다.", 404),
    MEMBERS_NOT_FOUND("회원 목록 조회에 실패 하였습니다.", 404),
    BOARDS_NOT_FOUND("회원의 게시글 목록 조회에 실패 하였습니다.", 404),
    COMMENTS_NOT_FOUND("회원의 댓글 목록 조회에 실패 하였습니다.", 404);



    private val adminTaskException = AdminTaskException(message, code)

    fun get(): AdminTaskException {
        return adminTaskException
    }
}