package com.example.restfileupload.repository;

import com.example.restfileupload.dto.*;
import com.example.restfileupload.service.BoardService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootTest
@Log4j2
@Transactional
public class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister() {
        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("File...Sample Title...")
                .content("Sample1 Content......")
                .writer("user00")
                .build();

        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aaa.jpg",
                        UUID.randomUUID()+"_bbb.jpg",
                        UUID.randomUUID()+"_ccc.jpg"
                )
        );

        Long bno = boardService.register(boardDTO);

        log.info("bno: "+bno);

    }

    @Test
    public void testReadOne() {
        long bno = 103L;

        BoardDTO boardDTO = boardService.readOne(bno);

        log.info(boardDTO);

        for(String fileName : boardDTO.getFileNames()) {
            log.info(fileName);
        }   //  end for
    }

    @Test
    public void testModify() {
        //변경에 필요한 데이터만...
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(103L)
                .title("uptdate..... 103")
                .content("update Content.... 103... ")
                .build();

        //  첨부파일을 하나 추가
        boardDTO.setFileNames(Arrays.asList(UUID.randomUUID()+"_zzz.jpg"));

        boardService.modify(boardDTO);
        // 확인...
        log.info(boardService.readOne(boardDTO.getBno()));
    }

    @Test
    public void testRemove() {
        long bno = 103L;

        boardService.remove(bno);
        Assertions.assertThrows(NoSuchElementException.class,
                () -> boardService.readOne(bno));

    }

    @Test
    public void testList() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();
        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
        log.info(responseDTO);
    }

    @Test
    public void testWithAll() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardListAllDTO> responseDTO =
                boardService.listWithAll(pageRequestDTO);

        List<BoardListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(boardListAllDTO -> {
            log.info(boardListAllDTO.getBno() + " : " + boardListAllDTO.getTitle());

            if(boardListAllDTO.getBoardImages() != null) {
                for(BoardImageDTO boardImage : boardListAllDTO.getBoardImages()) {
                    log.info(boardImage);
                }
            }
            log.info("-------------------------");
        });
    }
}
