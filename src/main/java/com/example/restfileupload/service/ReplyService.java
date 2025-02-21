package com.example.restfileupload.service;

import com.example.restfileupload.dto.PageRequestDTO;
import com.example.restfileupload.dto.PageResponseDTO;
import com.example.restfileupload.dto.ReplyDTO;

public interface ReplyService {

    Long register(ReplyDTO replyDTO);

    ReplyDTO read(Long rno);

    void modify(ReplyDTO replyDTO);

    void remove(Long rno);

    //  특정 게시물의 댓글 목록 불러오기
    PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO);
}
