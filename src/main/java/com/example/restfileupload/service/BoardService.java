package com.example.restfileupload.service;

import com.example.restfileupload.domain.Board;
import com.example.restfileupload.dto.BoardDTO;
import com.example.restfileupload.dto.BoardListAllDTO;
import com.example.restfileupload.dto.PageRequestDTO;
import com.example.restfileupload.dto.PageResponseDTO;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
public interface BoardService {

    //  등록 기능
    Long register(BoardDTO boardDTO);

    //  조회와 목록 처리
    BoardDTO readOne(Long bno);

    //  수정
    void modify(BoardDTO boardDTO);

    //  삭제
    void remove(Long bno);

    PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);

    //  게시글의 이미지와 댓글의 숫자까지 처리
    PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    //  DTO 객체를 Entity 객체로 변환하기 위한 default Method
    default Board dtoToEntity(BoardDTO boardDTO) {

        Board board = Board.builder()
                .bno(boardDTO.getBno())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .writer(boardDTO.getWriter())
                .build();

        if(boardDTO.getFileNames() != null) {
            boardDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            });
        }
        return board;
    }

    // Entity 객체를 DTO 객체로 변환하기 위한 default Method
    default BoardDTO entityToDTO(Board board) {

        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .build();

        List<String> fileNames =
                board.getImageSet().stream().sorted().map(boardImage ->
                        boardImage.getUuid()+"_"+boardImage.getFileName()).collect(Collectors.toList());

        boardDTO.setFileNames(fileNames);
        return boardDTO;
    }

}
