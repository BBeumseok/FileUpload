package com.example.restfileupload.repository.search;

import com.example.restfileupload.domain.Board;

import com.example.restfileupload.domain.QBoard;
import com.example.restfileupload.domain.QReply;
import com.example.restfileupload.dto.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl() {super(Board.class);}

    @Override
    public Page<Board> search1(Pageable pageable) {

        // Q도메인을 이용한 쿼리 작성 및 테스트
        // Querydsl의 목적은 "타입" 기반으로 "코드"를 이용해서 JPQL 쿼리를 생성하고 실행한다...
        // Q도메인은 이 때에 코드를 만든는 대신 클래스가 Q도메인 클래스...

        // 1. Q도메인 객체 생성
        QBoard board = QBoard.board;

        // 2. Query 작성....
        JPQLQuery<Board> query = from(board);   // select .. from board

        // BooleanBuilder() 사용
        BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

        booleanBuilder.or(board.title.contains("11"));   // title like ...
        booleanBuilder.or(board.content.contains("11")); // content like ...

//        query.where(board.title.contains("1")); // where title like ...

        query.where(booleanBuilder);                     // )
        query.where(board.bno.gt(0L));              // bno > 0

        // paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> title = query.fetch();      // JPQLQuery에 대한 실행

        long count = query.fetchCount();        // 쿼리 실행....

        return null;
    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {

        // 1. Qdomain 객체 생성
        QBoard board = QBoard.board;

        // 2. QL 작성...
        JPQLQuery<Board> query = from(board);  // select ... from board

        if( ( types != null && types.length > 0) && keyword != null ) {
            // 검색 조건과 키워드가 있는 경우....

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));  // title like concat('%',keyword,'%')
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));// content like concat('%',keyword,'%')
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword)); // writer like concat('%',keyword,'%')
                        break;
                }
            }  // for end

            query.where(booleanBuilder);  // )

        }// if end

        // bno > 0
        query.where(board.bno.gt(0L));

        // paging...
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();

        // Page<T> 형식으로 반환 : Page<Board>
        // PageImpl을 통해서 반환 : (list - 실제 목록 데이터, pageable, total -전체 개수)
        return new PageImpl<>(list,pageable,count);
    }

    //  Board와 Reply를 left join 처리 후 쿼리를 실행하여 내용을 확인
    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types,
                                                      String keyword,
                                                      Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board);
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board));   //  leaf join

        //  검색 조건과 키워드를 사용하는 코드 추가
        if ( (types != null && types.length > 0) &&  keyword != null ) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();   //   (

            for(String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }   //  end for
            boardJPQLQuery.where(booleanBuilder);

            getQuerydsl().applyPagination(pageable, boardJPQLQuery);    //  paging

        }

        boardJPQLQuery.groupBy(board);  //  튜플 처리를 위한 추가된 코드 1

        getQuerydsl().applyPagination(pageable, boardJPQLQuery);    //  paging

        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(board, reply.countDistinct());  //  튜풀 추가 코드 2

        List<Tuple> tupleList = tupleJPQLQuery.fetch(); //  튜플 추가 코드 3

        List<BoardListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            Board board1 = (Board) tuple.get(board);
            long replyCount = tuple.get(1, Long.class);

            BoardListAllDTO dto = BoardListAllDTO.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .writer(board1.getWriter())
                    .regDate(board1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            //  BoardImage를 BoardImageDTO 처리할 부분
            List<BoardImageDTO> imageDTOS = board1.getImageSet().stream().sorted()
                    .map(boardImage -> BoardImageDTO.builder()
                            .uuid(boardImage.getUuid())
                            .fileName(boardImage.getFileName())
                            .ord(boardImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setBoardImages(imageDTOS);  //  처리된 BoardImageDTO 들을 추가
            return dto;

        }).collect(Collectors.toList());

        long totalCount = boardJPQLQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);

//        List<Board> boardList = boardJPQLQuery.fetch();
//
//        boardList.forEach(board1 -> {
//            System.out.println(board1.getBno());
//            System.out.println(board1.getImageSet());
//            System.out.println("---------------------");
//        });

    }
}
