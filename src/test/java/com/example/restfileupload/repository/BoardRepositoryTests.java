package com.example.restfileupload.repository;

import com.example.restfileupload.domain.Board;
import com.example.restfileupload.domain.BoardImage;
import com.example.restfileupload.dto.BoardListAllDTO;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    /*
        testInsertImages()는 게시물 하나에 3개의 첨부파일을 추가하는 경우를 가정
        영속성 전이가 일어나기 때문에 테스트 실행 시 board 테이블에 1번, board_image 테이블에 3번
        insert가 일어나게 된다.
     */
    @Test
    public void testInsertImages() {
        Board board = Board.builder()
                .title("Image Title")
                .content("첨부파일 테스트")
                .writer("testuser")
                .build();

        for(int i = 0; i < 3; i ++) {
            board.addImage(UUID.randomUUID().toString(), "file" +i+".jpg");
        }   //end for

        boardRepository.save(board);
    }

    @Test
    public void testReadWithImages() {

        //  반드시 존재하는 bno로 확인
        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        log.info(board);
        log.info("------------------");
        for(BoardImage boardImage : board.getImageSet()) {
            log.info(boardImage);
        }

        /*
            테스트 시 board 테이블에서 select가 일어난 후에 에러가 발생
            log.info()로 Board의 출력까지 끝난 후에 다시 select를 실행하려고 하는데 DB와 연결이
            끝난 상태이므로 'no session' 이라는 메시지가 뜨는 것을 볼 수 있음

            에러를 해결하는 가장 간단한 방법은 테스트 코드에 @Transactional을 추가
            @Transactional 적용 시 필요할 때마다 메소드 내에서 추가적인 쿼리를 여러 번 실행하는 것이
            가능해지기 때문이다.

            BoardRepository에 findyByIdWithImages() 메소드를 직접 정의하여 추가
            테스트 코드를 수정 후 실행하면 board 테이블과 board_image 테이블의 조인 처리가 된 상태로 select가
            실행되면서 Board와 BoardImage를 한 번에 처리할 수 있게 된 것을 확인할 수 있다.
        */
    }

    //  특정 게시물의 첨부파일을 다른 파일들로 수정하는 테스트
    @Transactional
    @Commit
    @Test
    public void testModifyImages() {
        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        //  기존의 첨부파일들은 삭제
        //  Board 객체, 이것과 관련된 BoardImage 객체들을 수정
        board.clearImages();

        //  새로운 첨부파일들
        for(int i = 0; i < 2; i++) {
            board.addImage(UUID.randomUUID().toString(), "updatefile"+i+".jpg");
        }
        boardRepository.save(board);

        /*
            테스트 실행 시 예상한 것과는 다른 결과가 나오는 것을 확인할 수 있는데
            현재 cascade 속성이 ALL로 지정되었기 때문에 상위 엔티티(Board)의 상태 변화가 하위 엔티티(BoardImage)까지
            영향을 주긴했지만 삭제되지는 않는다.

            하위 엔티티의 참조가 더 이상 없는 상태가 되면 @OneToMany에 orphanRemoval 속성값을 true로 지정해 주어야만
            실제 삭제가 이루어지게 된다.
         */
    }

    //  먼저 Reply 엔티티들을 삭제한 후에 Board를 삭제하도록 테스트 코드 작성
    @Test
    @Transactional
    @Commit
    public void testRemoveAll() {

        Long bno = 1L;

        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);
    }

    //  목록 데이터를 처리하기 전 예제로 사용할만한 충분한 Board와 BoardImage, Reply가 필요하기 때문에
    //  기존의 테이블을 삭제 후 새로운 데이터들을 추가
    //  다음과 같이 메소드를 작성해서 필요한 데이터들을 추가
    //  번호가 5, 10, 15 ... 의 경우 첨부파일이 없는 게시물이 작성되고, 나머지는 3개의 첨부파일이 있는 상태가 되도록 구성
    @Test
    public void testInsertAll() {
        for(int i = 1; i <= 100; i++) {

            Board board = Board.builder()
                    .title("Title..." + i)
                    .content("Content..." + i)
                    .writer("Writer...." + i)
                    .build();

            for(int j = 0; j < 3; j++) {

                if(i % 5 == 0) {
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(), i+"file"+j+".jpg");
            }
            boardRepository.save(board);
        }   //end for
    }

    @Transactional
    @Test
    public void testSearchImageReplyCount() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

//        boardRepository.searchWithAll(null, null, pageable);

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(null, null, pageable);

        log.info("----------------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(boardListAllDTO -> log.info(boardListAllDTO));
    }



}
