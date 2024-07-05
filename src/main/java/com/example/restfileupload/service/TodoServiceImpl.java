package com.example.restfileupload.service;

import com.example.restfileupload.domain.Todo;
import com.example.restfileupload.dto.PageRequestDTO;
import com.example.restfileupload.dto.PageResponseDTO;
import com.example.restfileupload.dto.TodoDTO;
import com.example.restfileupload.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class TodoServiceImpl implements TodoService{

    private final TodoRepository todoRepository;
    private final ModelMapper modelMapper;

    //  등록 기능 구현
    @Override
    public Long register(TodoDTO todoDTO) {
        Todo todo = modelMapper.map(todoDTO, Todo.class);
        Long tno = todoRepository.save(todo).getTno();

        return tno;
    }

    //  조회와 목록 처리 기능 구현
    @Override
    public TodoDTO read(Long tno) {
        Optional<Todo> result = todoRepository.findById(tno);
        Todo todo = result.orElseThrow();
        return modelMapper.map(todo, TodoDTO.class);
    }

    //  페이징 처리
    @Override
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {
        //  PageResponseDTO를 Page<TodoDTO> 타입으로 변환
        Page<TodoDTO> result = todoRepository.list(pageRequestDTO);
        return PageResponseDTO.<TodoDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.toList())
                .total((int)result.getTotalElements())
                .build();
    }

    //  삭제 기능 구현
    @Override
    public void remove(Long tno) {todoRepository.deleteById(tno);}

    //  수정 기능 구현
    @Override
    public void modify(TodoDTO boardDTO) {
        Optional<Todo> result = todoRepository.findById(boardDTO.getTno());

        Todo todo = result.orElseThrow();

        todo.changeTitle(boardDTO.getTitle());
        todo.changeDueDate(boardDTO.getDueDate());
        todo.changeComplete(boardDTO.isComplete());

        todoRepository.save(todo);
    }
}
