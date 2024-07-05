package com.example.restfileupload.service;

import com.example.restfileupload.dto.PageRequestDTO;
import com.example.restfileupload.dto.PageResponseDTO;
import com.example.restfileupload.dto.TodoDTO;
import jakarta.transaction.Transactional;

@Transactional
public interface TodoService {

    //  등록 기능
    Long register(TodoDTO todoDTO);

    //  조회와 목록 처리
    TodoDTO read(Long tno);

    //  서비스 계층 구현
    PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO);

    //  삭제
    void remove(Long tno);

    //  수정
    void modify(TodoDTO todoDTO);
}
