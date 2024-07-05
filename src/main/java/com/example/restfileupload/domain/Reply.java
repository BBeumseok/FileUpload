package com.example.restfileupload.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table( indexes = {
        @Index(name =  "idx_reply_board_bno", columnList = "board_bno")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
public class Reply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    private String replyText;

    private String replyer;

    public void changeText(String text) {this.replyText = text;}

    //  board 값 설정을 위해 -> bno를 받아서 생성
    public void setBoard(Long bno){this.board = Board.builder().bno(bno).build();}
}
