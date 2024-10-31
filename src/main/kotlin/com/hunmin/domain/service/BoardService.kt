package com.hunmin.domain.service

import com.hunmin.domain.dto.board.BoardRequestDTO
import com.hunmin.domain.dto.board.BoardResponseDTO
import com.hunmin.domain.dto.page.PageRequestDTO
import com.hunmin.domain.entity.Board
import com.hunmin.domain.exception.BoardException
import com.hunmin.domain.exception.MemberException
import com.hunmin.domain.repository.BoardRepository
import com.hunmin.domain.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

@Service
@Transactional
class BoardService(
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository
) {
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
        return try {
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

            boardRepository.save(board)

            BoardResponseDTO(board)
        } catch (e: Exception) {
            throw BoardException.NOT_CREATED.toException()
        }
    }

    //게시글 조회
    fun readBoard(boardId: Long): BoardResponseDTO {
        val board = boardRepository.findByIdWithComments(boardId).orElseThrow { BoardException.NOT_FOUND.toException() }

        return BoardResponseDTO(board)
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

            return BoardResponseDTO(board)
        } catch (e: Exception) {
            throw BoardException.NOT_DELETED.toException()
        }
    }

    //게시글 목록 조회
    fun readBoardList(pageRequestDTO: PageRequestDTO): Page<BoardResponseDTO> {
        val sort = Sort.by(Sort.Direction.DESC, "createdAt")
        val pageable: Pageable = pageRequestDTO.getPageable(sort)
        val boards: Page<Board> = boardRepository.findAll(pageable)

        return boards.map { BoardResponseDTO(it) }
    }

    //회원 별 작성글 목록 조회
    fun readBoardListByMember(memberId: Long, pageRequestDTO: PageRequestDTO): Page<BoardResponseDTO> {
        val sort = Sort.by(Sort.Direction.DESC, "createdAt")
        val pageable: Pageable = pageRequestDTO.getPageable(sort)

        return boardRepository.findByMemberId(memberId, pageable).map { BoardResponseDTO(it) }
    }
}
