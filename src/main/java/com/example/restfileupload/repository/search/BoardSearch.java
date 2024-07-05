package com.example.restfileupload.repository.search;

import com.example.restfileupload.domain.Board;
import com.example.restfileupload.dto.BoardDTO;
import com.example.restfileupload.dto.BoardListAllDTO;
import com.example.restfileupload.dto.BoardListReplyCountDTO;
import com.example.restfileupload.dto.PageRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
    1. Querydsl과 기존의 JPARepository와 연동 작업 설정을 위한 인터페이스 생성
    2. 이 인터페이스를 구현하는 구현체 생성. 주의사항 구현체의 이름은 인터페이스 + Impl 로 작성함.
      이름이 다른 경우 제대로 동작하지 않을 수 있어요~~~~
    3. 마지막으로 BoardRepository의 선언부에서 BoardSearch 인터페이스를 추가 지정
 */
public interface BoardSearch {

    Page<Board> search1(Pageable pageable);

    // title과 content 의 내용을 검색...
    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);

    //  Board, BoardIamge 목록 데이터 처리를 위한 메소드 추가
    //  튶플처리를 위해 리턴타입을 변경
    Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable);

}
