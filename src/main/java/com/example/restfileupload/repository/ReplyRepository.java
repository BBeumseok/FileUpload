package com.example.restfileupload.repository;

import com.example.restfileupload.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> listOfBoard(Long bno, Pageable pageable);

    //  특정한 게시물에 해당하는 데이터들을 삭제할 수 있도록 쿼리 메소드 추가
    void deleteByBoard_Bno(Long bno);

}
