package com.hunmin.domain.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hunmin.domain.dto.board.BoardRequestDTO
import com.hunmin.domain.dto.board.BoardResponseDTO
import com.hunmin.domain.dto.notification.NotificationSendDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.Follow
import com.hunmin.domain.entity.NotificationType
import com.hunmin.domain.exception.BoardException
import com.hunmin.domain.exception.MemberException
import com.hunmin.domain.handler.SseEmitters
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.FollowRepository
import com.hunmin.domain.repository.MemberRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
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
class BoardService(
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository,
    private val followRepository: FollowRepository,
    private val notificationService: NotificationService,
    private val sseEmitters: SseEmitters,
    private val redisTemplate: RedisTemplate<String, Any>

) {

    private val hashOps: HashOperations<String, String, Map<String, Any>> = redisTemplate.opsForHash()

    //Redis에 저장된 게시글을 읽기
    private fun readBoardFromRedis(boardId: String): BoardResponseDTO? {
        val boardData = hashOps.get("board", boardId)
        return boardData?.let {
            ObjectMapper().convertValue(it, BoardResponseDTO::class.java)
        }
    }

    // 게시글 이미지 첨부
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
        file.inputStream.use { input ->
            Files.copy(input, filePath)
        }

        return "/uploads/$fileName"
    }

    // 파일 확장자 추출
    private fun getFileExtension(fileName: String?): String {
        require(!fileName.isNullOrEmpty() && fileName.contains(".")) {
            "Invalid file name: $fileName"
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1)
    }

    // 게시글 이미지 삭제
    @Throws(IOException::class)
    fun deleteImage(imageUrl: String) {
        val uploadDir = Paths.get("uploads").toAbsolutePath().normalize().toString()
        val fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1)
        val filePath = Paths.get(uploadDir, fileName)

        Files.deleteIfExists(filePath)
    }

    //게시글 등록
    fun createBoard(boardRequestDTO: BoardRequestDTO): BoardResponseDTO {

        try {
            val member = memberRepository.findById(boardRequestDTO.memberId).orElseThrow{ MemberException.NOT_FOUND.get() }


            val board = Board.builder()
                .member(member)
                .nickname(member.nickname)
                .title(boardRequestDTO.title)
                .content(boardRequestDTO.content)
                .location(boardRequestDTO.location)
                .latitude(boardRequestDTO.latitude)
                .longitude(boardRequestDTO.longitude)
                .imageUrls(boardRequestDTO.imageUrls ?: ArrayList())
                .build()

            val savedBoard = boardRepository.save(board)

            // 알림
            val sender = member

            val allFollow = followRepository.findAll()
            val followList = mutableListOf<Follow>()
            for (follow in allFollow) {
                if (follow.follower == sender){
                    followList.add(follow)
                }
            }
            for (follow in followList) {
                if (!follow.isBlock && follow.notification) {
                    val notificationSendDTO = NotificationSendDTO(
                        message = follow.follower!!.nickname + "님 : " + "새로운 게시글을 등록하였습니다",
                        notificationType = NotificationType.BOARD,
                        url = "/board/" + board.boardId
                    ).apply {
                        this.memberId = follow.followee!!.memberId
                    }
                    notificationService.send(notificationSendDTO)
                    // emitter
                    val emitterId = follow.followee!!.memberId.toString() + "_"
                    val emitter = sseEmitters.findSingleEmitter(emitterId)

                    log.info("emitter $emitter")
                    if (emitter != null) {
                        try {
                            emitter.send(BoardResponseDTO(board))
                        } catch (e: IOException) {
                            log.error("Error sending comment to client via SSE: ${e.message}")
                            sseEmitters.delete(emitterId)
                        }
                    }
                }
            }
            val responseDTO = BoardResponseDTO(board)
            redisTemplate.opsForHash<Any, BoardResponseDTO>().put("board", board.boardId.toString(), responseDTO)
            return BoardResponseDTO(board)
        } catch (e: Exception) {
            throw BoardException.NOT_CREATED.toException()
        }
    }

    //게시글 조회
    fun readBoard(boardId: Long): BoardResponseDTO? {
        val cachedBoard = readBoardFromRedis(boardId.toString())
        if (cachedBoard != null) {
            return cachedBoard
        } else {
            log.info("Redis에 게시글이 없음, DB에서 조회")
            val board = boardRepository.findByIdWithComments(boardId)
                .orElseThrow { BoardException.NOT_FOUND.toException() }
            val responseDTO = BoardResponseDTO(board)

            //새로 조회된 게시글을 Redis에 저장
            redisTemplate.opsForHash<String, BoardResponseDTO>()
                .put("board", board.boardId.toString(), responseDTO)

            return responseDTO
        }
    }

    //게시글 수정
    fun updateBoard(boardId: Long, boardRequestDTO: BoardRequestDTO): BoardResponseDTO {
        val board = boardRepository.findById(boardId).orElseThrow { BoardException.NOT_FOUND.toException() }

        return try {
            val existingImageUrls = board.imageUrls.toMutableList()

            boardRequestDTO.imageUrls?.let { newImageUrls ->
                val urlsToDelete = existingImageUrls.filterNot { newImageUrls.contains(it) }

                for (url in urlsToDelete) {
                    deleteImage(url)
                    existingImageUrls.remove(url)
                }

                existingImageUrls.addAll(newImageUrls)
                board.changeImgUrls(existingImageUrls)
            }

            board.changeTitle(boardRequestDTO.title)
            board.changeContent(boardRequestDTO.content)
            board.changeLocation(boardRequestDTO.location)
            board.changeLatitude(boardRequestDTO.latitude)
            board.changeLongitude(boardRequestDTO.longitude)

            boardRepository.save(board)

            val responseDTO = BoardResponseDTO(board)
            redisTemplate.opsForHash<Any, BoardResponseDTO>().put("board", board.boardId.toString(), responseDTO)

            BoardResponseDTO(board)
        } catch (e: Exception) {
            throw BoardException.NOT_UPDATED.toException()
        }
    }

    //게시글 삭제
    fun deleteBoard(boardId: Long): BoardResponseDTO {
        val board = boardRepository.findById(boardId).orElseThrow { BoardException.NOT_FOUND.toException() }

        return try {
            boardRepository.delete(board)
            redisTemplate.opsForHash<Any, BoardResponseDTO>().delete("board", boardId.toString())

            BoardResponseDTO(board)
        } catch (e: Exception) {
            throw BoardException.NOT_DELETED.toException()
        }
    }

    //게시글 목록 조회
    fun readBoardList(pageRequestDTO: PageRequestDTO): Page<BoardResponseDTO> {
        val pageable: Pageable = pageRequestDTO.getPageable(Sort.by(Sort.Direction.DESC, "createdAt"))
        val boardResponseDTOs = mutableListOf<BoardResponseDTO>()

        //Redis에서 조회
        for (boardIdObj in redisTemplate.opsForHash<Any, BoardResponseDTO>().keys("board")) {
            if (boardIdObj is String) {
                val cachedBoard = readBoardFromRedis(boardIdObj)
                if (cachedBoard != null) {
                    boardResponseDTOs.add(cachedBoard)
                }
            }
        }

        //Redis에 없을 경우 DB에서 조회
        if (boardResponseDTOs.size < pageable.pageSize) {
            val boards = boardRepository.findAll(pageable)
            boards.content.mapTo(boardResponseDTOs) { BoardResponseDTO(it) }

            //새로 조회된 게시글을 Redis에 저장
            boards.content.forEach { board ->
                redisTemplate.opsForHash<Any, BoardResponseDTO>().put("board", board.boardId.toString(), BoardResponseDTO(board))
            }
        }

        boardResponseDTOs.sortByDescending { it.createdAt }

        val start = pageable.offset.toInt()
        val end = Math.min(start + pageable.pageSize.toInt(), boardResponseDTOs.size)
        val pagedResponse = boardResponseDTOs.subList(start, end)

        return PageImpl(pagedResponse, pageable, boardResponseDTOs.size.toLong())
    }

    //회원 별 작성글 목록 조회
    fun readBoardListByMember(memberId: Long, pageRequestDTO: PageRequestDTO): Page<BoardResponseDTO> {
        val pageable: Pageable = pageRequestDTO.getPageable(Sort.by(Sort.Direction.DESC, "createdAt"))
        val boardResponseDTOs = mutableListOf<BoardResponseDTO>()

        //Redis에서 조회
        for (boardIdObj in redisTemplate.opsForHash<Any, BoardResponseDTO>().keys("board")) {
            if (boardIdObj is String) {
                val cachedBoard = readBoardFromRedis(boardIdObj)
                if (cachedBoard != null && cachedBoard.memberId == memberId) {
                    boardResponseDTOs.add(cachedBoard)
                }
            }
        }

        //Redis에 없을 경우 DB에서 조회
        if (boardResponseDTOs.size < pageable.pageSize) {
            val boards = boardRepository.findByMemberId(memberId, pageable)
            boards.content.mapTo(boardResponseDTOs) { BoardResponseDTO(it) }

            // 새로 조회된 게시글을 Redis에 저장
            boards.content.forEach { board ->
                redisTemplate.opsForHash<Any, BoardResponseDTO>().put("board", board.boardId.toString(), BoardResponseDTO(board))
            }
        }

        boardResponseDTOs.sortByDescending { it.createdAt }

        val start = pageable.offset.toInt()
        val end = Math.min(start + pageable.pageSize.toInt(), boardResponseDTOs.size)
        val pagedResponse = boardResponseDTOs.subList(start, end)

        return PageImpl(pagedResponse, pageable, boardResponseDTOs.size.toLong())
    }

    // 게시글 제목별 검색기능
    fun searchBoardByTitle(pageable: PageRequestDTO, title: String): Page<BoardResponseDTO> {
        val title = title
        try {
            val sort = Sort.by(Sort.Direction.DESC, "title")
            return boardRepository.searchBoard(pageable.getPageable(sort), title)
        } catch (e: Exception) {
            log.error("게시글 검색 실패 $e.message")
            throw BoardException.NOT_FOUND.toException()
        }
    }
}
