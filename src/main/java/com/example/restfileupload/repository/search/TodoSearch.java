package com.example.restfileupload.repository.search;

import com.example.restfileupload.dto.PageRequestDTO;
import com.example.restfileupload.dto.TodoDTO;
import org.springframework.data.domain.Page;

public interface TodoSearch {

    Page<TodoDTO> list(PageRequestDTO pageRequestDTO);
}
