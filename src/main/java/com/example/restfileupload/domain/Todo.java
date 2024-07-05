package com.example.restfileupload.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Todo{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tno;

    private String title;

    private LocalDate dueDate;

    private String writer;

    private boolean complete;


    //  Entity 내에서 변경 가능한 부분들을 메소드로 생성
    public void changeComplete(boolean complete){this.complete = complete;}

    public void changeDueDate(LocalDate dueDate){this.dueDate = dueDate;}

    public void changeTitle(String title){this.title = title;}
}
