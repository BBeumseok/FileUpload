package com.example.restfileupload.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet")
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;
    
    /*
        IDENTITY : 데이터베이스에 위임( AUTO-INCREMENT)
        SEQUENCE : 데이터베이스 시퀀스 오브젝트를 사용 - @SequenceGenerator 필요
        TABLE : 키 생성용 테이블 사용. 모든 DB에서 사용 - @TableGenerator 필요
        AUTO : 방언에 따라 자동 지정됨. 기본값
     */

    @Column(length = 500, nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    //  Board 엔티티에서도 BoardImage에 대한 참조를 가지는 방식으로 작성(양방향)
    //  기존 테이블 board, board_image, board_image_set 테이블을 모두 삭제
    //  @OneToMany에 mappedBy 속성 적용 (BoardIamge의 board 변수)
    //  CascadeType.ALL을 설정해서 Board 엔티티의 모든 상태 변화에 BoardImage 역시 같이 변경되도록 구성
    @OneToMany(mappedBy = "board",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)   //  @BatchSize를 적용하여 'N + 1' 문제를 보완
    private Set<BoardImage> imageSet = new HashSet<>();

    //  Board 객체 자체에서 BoardImage 객체들을 관리하도록 addImage()와 clearImages() 추가
    //  addImage()는 내부적으로 BoardImage 객체 내부의 Board에 대한 참조를 this를 이용해서 처리함
    //  (양방향의 경우 참조 관계가 서로 일치하도록 작성해야만 한다)
    public void addImage(String uuid, String fileName) {
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }

    //  clearImages()는 첨부파일들을 모두 삭제하므로 BoardImage 객체의 Board 참조를 null로 변경하게 한다.
    //  (필수적인 것은 아니나 상위 엔티티와 하위 엔티티의 상태를 맞추는 것이 좋다)
    public void clearImages() {
        imageSet.forEach(boardImage -> boardImage.changeBoard(null));

        this.imageSet.clear();
    }

    //  엔티티 내에서 변경 가능한 title과 content 값을 수정하는 메서드
    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
}
