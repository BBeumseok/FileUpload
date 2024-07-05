package com.example.restfileupload.repository;

import com.example.restfileupload.domain.Todo;
import com.example.restfileupload.repository.search.TodoSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {

}
