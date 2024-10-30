package com.hunmin.domain.exception.follow

enum class FollowException(val followTaskException: FollowTaskException) {
    DUPLICATED_FOLLOW("팔로우가 이미 존재합니다.", 400),
    IMPOSSIBLE_FOLLOW("팔로우 할 수 없습니다.", 400),
    FAILED_REGISTER_FOLLOW("팔로우 요청에 실패하였습니다", 400),
    FAILED_ACCEPT_FOLLOW("팔로우 수락에 실패하였습니다", 400),
    NOT_FOUND("팔로우가 존재하지 않습니다.", 400);

    constructor(message: String, code: Int) : this(FollowTaskException(message, code))

    fun get(): FollowTaskException {
        return this.followTaskException
    }
}