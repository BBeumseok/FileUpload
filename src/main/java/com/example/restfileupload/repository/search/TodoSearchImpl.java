package com.example.restfileupload.repository.search;

import com.example.restfileupload.domain.QTodo;
import com.example.restfileupload.domain.Todo;
import com.example.restfileupload.dto.PageRequestDTO;
import com.example.restfileupload.dto.TodoDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {
    //  TodoSearchImpl은 QuerydslRepositorySupport를 상속, TodoSearch 인터페이스를 구현하도록 선언
    public TodoSearchImpl() {super(Todo.class);}

    @Override
    public Page<TodoDTO> list(PageRequestDTO pageRequestDTO) {
        //QTodo를 이용해 from, to 검색조건 / complete에 해당하는 검색 조건을 구현

        QTodo todo = QTodo.todo;

        JPQLQuery<Todo> query = from(todo);

        if(pageRequestDTO.getFrom() != null && pageRequestDTO.getTo() != null) {

            BooleanBuilder fromToBuilder = new BooleanBuilder();
            fromToBuilder.and(todo.dueDate.goe(pageRequestDTO.getFrom()));
            fromToBuilder.and(todo.dueDate.loe(pageRequestDTO.getTo()));
            query.where(fromToBuilder);
        }

        if(pageRequestDTO.getCompleted() != null) {
            query.where(todo.complete.eq(pageRequestDTO.getCompleted()));
        }

        if(pageRequestDTO.getKeyword() != null) {
            query.where(todo.title.contains(pageRequestDTO.getKeyword()));
        }

        this.getQuerydsl().applyPagination(pageRequestDTO.getPageable("tno"), query);

        JPQLQuery<TodoDTO> dtoQuery = query.select(Projections.bean(TodoDTO.class,
                todo.tno,
                todo.title,
                todo.dueDate,
                todo.complete,
                todo.writer
        ));

        List<TodoDTO> list = dtoQuery.fetch();

        long count = dtoQuery.fetchCount();

        return new PageImpl<>(list, pageRequestDTO.getPageable("tno"), count);
    }
}
