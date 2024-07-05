package com.example.restfileupload.repository;

import com.example.restfileupload.domain.Board;
import com.example.restfileupload.repository.search.BoardSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    /*
        @Query 어노테이션에서 사용하는 구문은 JPQL을 이용
        JPQL은 SQL과 유사하게 JPA에서 사용하는 쿼리 언어
        @Query를 이용하는 경우
        1. 조인과 같이 복잡한 쿼리를 실행하려고 할 때
        2. 원하는 속성만 추출해서 Object[]로 처리하거나 DTO로 처리가 가능
        3. 속성값 중 nativeQuery 속성값을 true로 지정하면 SQL 구문으로 사용이 가능함.
     */

    @Query("select b from Board b where b.title like concat('%', :keyword, '%') ")
    Page<Board> findKeyword(String keyword, Pageable pageable);

    @Query(value = "select now()", nativeQuery = true)
    String getTime();

    //  findByIdWithImages()를 직접 정의하고 @EntityGraph 적용
    //  attributePaths 속성을 이용해서 같이 로딩해야 하는 속성을 명시할 수 있음
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.bno = :bno")
    Optional<Board> findByIdWithImages(Long bno);

}
