package com.hunmin.domain.repository.search

import com.hunmin.domain.dto.board.BoardResponseDTO
import com.hunmin.domain.entity.Board
import com.hunmin.domain.entity.QBoard
import com.hunmin.domain.entity.QComment
import com.querydsl.jpa.JPQLQuery
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import java.util.stream.Collectors

class BoardSearchImpl : QuerydslRepositorySupport(Board::class.java), BoardSearch {
    override fun searchBoard(pageable: Pageable, title: String): Page<BoardResponseDTO> {
        val board: QBoard = QBoard.board
        val comment: QComment = QComment.comment

        val query: JPQLQuery<Board> = from(board)
            .leftJoin(board.member).fetchJoin()
            .leftJoin(board.comments, comment)
            .where(board.title.contains(title))
            .distinct()

        val total: Long = query.fetchCount()

        getQuerydsl().applyPagination(pageable, query)

        val boardList: List<Board> = query.fetch()

        val boardDtoList: List<BoardResponseDTO> = boardList.stream()
            .map({ board: Board -> BoardResponseDTO(board) })
            .collect(Collectors.toList())

        log.info("results 게시판 검색 페이징 ${boardDtoList}")

        return PageImpl(boardDtoList, pageable, total)
    }
}
