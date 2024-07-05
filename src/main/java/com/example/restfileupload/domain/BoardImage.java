package com.example.restfileupload.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
//Entity 연관관계 적용 시 항상 @ToString()에 exclude를 선언하여
//해당 객체를 참조하지 않도록 해야 한다.
public class BoardImage implements Comparable<BoardImage>{
    /*
        첨부파일을 의미하는 BoardImage Entity Class 선언
        BoardImage는 첨부파일의 고유한 uuid 값, 파일의 이름, 순번(ord)을 지정하고
        @ManyToOne으로 Board 객체를 지정

        BoardIamge는 특이하게도 Comparable 인터페이스를 적용하는데 이는
        @OneToMany 처리에서 순번에 맞게 정렬하기 위함이다.
     */
    @Id
    private String uuid;

    private String fileName;

    private int ord;

    @ManyToOne  //  ManyToOne 연관관계 적용
    private Board board;

    @Override
    public int compareTo(BoardImage other) {
        return this.ord = other.ord;
    }

    //  changeBoard()를 이용하여 Board 객체를 나중에 지정할 수 있게 하는데
    //  이것은 나중에 Board 엔티티 삭제 시 BoardImage 객체의 참조도 변경하기 위해서 사용
    public void changeBoard(Board board) {
         this.board = board;
    }
}
