package com.example.restfileupload.apiMain;

public class FileUpload {
    /*
        게시물과 댓글, 게시물과 첨부파일의 관계를 테이블의 구조로 바라볼 경우
        완전히 같은 구조지만 JPA에서는 게시글을 중심으로 해석하는지, 첨부파일을 중심으로 해석하는지에 따라
        다른 결과가 나올 수 있다.

        게시글과 댓글의 경우 @ManyToOne을 이용하여 처리했지만 이 예제에서는
        게시물의 관점에서 첨부파일들을 바라보는 @OneToMany에 대해 알아보도록 함

        @OneToMany는 기본적으로 상위 엔티티(게시물)와 여러 개의 하위 엔티티(첨부파일)의 구조로
        이루어져있고, @ManyToOne과 결정적으로 다른 점은 @ManyToOne은 다른 엔티티 객체의 참조로
        FK(Foriegn Key)를 가지는 쪽에서 하는 방식이지만 @OneToMany는 PK를 가진 쪽에서 사용한다는 점이다.
     */

    /*
        @OneToMany를 사용하는 구조는 다음과 같은 특징을 가진다.

        - 상위 엔티티에서 하위 엔티티들을 관리한다.

        - JPA의 Repository를 상위 엔티티 기준으로 생성한다.
          하위 엔티티에 대한 Repository의 생성이 잘못된 것은 아니지만 하위 엔티티들의 변경은 상위 엔티티에도 반영되어야 한다.

        - 상위 엔티티 상태가 변경되면 하위 엔티티들의 상태들도 같이 처리해야 한다.

        - 상위 엔티티 하나와 하위 엔티티 여러 개를 처리하는 경우 "N + 1" 문제가 발생할 수 있으므로 주의해야 한다.
     */

    /*
        @OneToMany는 기본적으로 각 엔티티에 해당하는 테이블을 독립적으로 생성하고, 중간에 매핑해주는 테이블이 생성된다.
        확인을 위해 기존의 데이터베이스에 board 테이블, reply 테이블 삭제

        BoardImage Entity가 추가된 상태에서 프로젝트를 실행하면 Board와 BoardImage 엔티티에 해당하는 테이블 외에
        추가적인 테이블(board_image_set)이 생성되는 것을 확인할 수 있다.

        board_image_set과 같이 엔티티 테이블 사이에 추가적으로 생성되는 테이블을 '매핑 테이블' 이라고 하는데
        매핑 테이블을 생성하지 않는 방법은 아래와 같다.

        1. 단방향으로 @OneToMany를 이용하는 경우 @JoinColumn을 이용

        2. mappedBy 라는 속성을 이용하는 방법

        mappedBy의 경우 Board와 BoardImage가 서로 참조를 유지하는 양방향 참조 상황에서 사용하는데
        mappedBy는 '어떤 엔티티의 속성으로 매핑되는지'를 의미한다.

        mappedBy를 적용하기 전 기존 테이블들을 삭제한다.

     */

    /*
        영속성의 전이(cascade)
        Board와 BoardImage처럼 상위 엔티티와 하위 엔티티의 연관관계를 상위 엔티티에서 관리하는 경우
        신경써야 할 부분은 상위 엔티티의 상태가 변경되었을 때 하위 엔티티 객체도 객체들 역시 같이 영향을 받는다는 점

        JPA에서는 '영속성의 전이(cascade)' 라는 용어로 이를 표현
        가장 대표적인 영속성의 전이가 지금부터 작성하는 Board와 BoardImage의 저장이다.

        예를 들어 BoardImage 객체가 JPA에 의해 관리되면 BoardImage를 참조하고 있는 Board 객체도
        같이 처리되어야 하고, 반대로 Board 객체가 변경되면 BoardImage 객체 역시 영향을 받을 수 있어야 한다.

        JPA에서는 이러한 경우 연관 관계에 cascade 속성을 부여하여 제어하도록 한다.

        cascade 속성값                                             설명
        PERSIST                            상위 엔티티가 영속 처리될 때 하위 엔티티들도 같이 영속 처리
        REMOVE                                                     〃

        MERGE
        REFRESH                            상위 엔티티의 상태가 변경될 때 하위 엔티티들도 같이 상태 변경
        DETACH                             (merge, refresh, detach)

        ALL                                상위 엔티티의 모든 상태 변경이 하위 엔티티에 적용

     */

    /*
        현재 구조에서 BoardImage는 Board가 저장될 때 같이 저장되어야 하는 엔티티 객체
        상위 엔티티가 하위 엔티티 객체들을 관리하는 경우 별도의 JpaRepository를 생성하지 않고, Board 엔티티에
        하위 엔티티 객체들을 관리하는 기능을 추가해서 사용한다.

        Board Entity에 @OneToMany - CascadeType.ALL 설정을 통해 상위 엔티티 상태 변경 시 하위 엔티티도 같이 적용되도록 설정
        객체 관리를 위해 addImage(), clearImages() 생성

        상위 엔티티인 Board에서 BoardImage들을 관리하므로 테스트 역시 BoardRepository 자체를 이용하여 처리할 수 있다.
     */

    /*
        @OneToMany의 로딩 방식은 기본적으로 지연(Lazy) 로딩이다.
        게시물 조회 시 Board 객체와 BoardImage 객체들을 생성해야하므로
        2번의 select가 필요하게 된다.

        하위 엔티티를 로딩하는 가장 간단한 방법은 즉시(eager) 로딩을 적용하는 것이지만 가능하면
        지연(lazy) 로딩을 이용하는 것이 기본적인 방식이므로 @EntityGraph를 이용하도록 한다.
        지연(lazy) 로딩이라고 해도 한 번에 조인 처리 후 select가 이루어지도록 하는 방법을 이용하도록 함

        @OneToMany 구조를 사용하는 경우에 얻을 수 있는 장점 중에 하나가 바로 이러한 하위 엔티티의 처리이다.
     */

    /*
        게시물과 첨부파일 수정은 다른 엔티티들 간의 관계와는 조금 다른 점이 존재한다.
        실제 처리 과정에서 첨부파일은 그 자체가 변경되는 것이 아니라 아예 기존의 모든 첨부파일들이 삭제되고,
        새로운 첨부파일들로 추가되기 때문이다.

        이전에 Board에는 addImage(), clearImages()를 이용해 Board 객체를 통해서 BoardImage 객체들을
        처리하도록 설계되어 있음

        게시물 삭제는 게시물을 사용하는 댓글들을 먼저 삭제해야 한다.
        다만 이 경우 다른 사용자가 만든 데이터를 삭제하는 것은 문제가 될 수 있으므로 주의할 필요가 있다.
     */

    /*
        상위 엔티티에서 @OneToMany와 같은 연관 관게를 유지하는 경우 한 번에 게시물과 첨부파일을 같이 처리할 수 있다는
        장점도 있기는 하지만 목록을 처리할 때는 예상하지 못한 문제를 만들어내기 때문에 주의해야 한다.

        N + 1 문제
        1. 목록 데이터를 처리하기 위한 Querydsl을 이용하는 BoardSearch 인터페이스에 메소드 추가
        2. BoardSearchImpl 클래스에는 추가한 searchWithAll() 메소드 상세 구현
        3. BoardSearchImpl의 searchWithAll() 내용은 Board와 Reply를 left join 처리하고 쿼리를 실행하여 내용 확인
        4. BoardRepository에 테스트 코드 작성 후 실행되는 쿼리문 확인
        5. 쿼리 한 번과 하나의 게시물마다 board_image에 대한 쿼리가 실행되는 상황을 볼 수 있는데 이것을 'N + 1' 문제라고 함
            (N은 게시물마다 각각 실행되는 쿼리, 1은 목록을 가져오는 쿼리)

        테스트 결과는 다음과 같은 구조로 실행된다.
        1) Board에 대한 페이징 처리가 실행되면서 limit로 처리
        2) System.out.println()을 통해 Board의 bno 값을 출력
        3) Board 객체의 imageSet을 가져오기 위해서 board_image 테이블을 조회하는 쿼리 실행
        4) 2, 3의 과정이 반복적으로 실행

        @BatchSize
        'N + 1'로 실행되는 쿼리는 데이터베이스를 엄청나게 많이 사용하기 때문에 문제가 된다.
        이 문제에 대한 가장 간단한 보완책은 @BatchSize를 이용하는 것.

        @BatchSize에는 size 라는 속성을 지정하는데 이를 이용해 'N 번'에 해당하는 쿼리를 모아서 한 번에 실행할 수 있다.
        size 속성값은 지정된 수만큼은 BoardImage를 조회할 떄 한 번에 in 조건으로 사용된다.
        변경된 코드의 테스트 결과는 이전과 많은 차이가 발생한다.
        (in 조건은 조건의 범위를 지정하는 데 사용되며, 지정된 값 중에서 하나 이상과 일치하면 조건에 맞는 것으로 처리)

        1. 실행 결과를 확인해보면 목록을 처리하는 쿼리가 실행되고, Board 객체의 bno를 출력한다.
        2. Board의 imageSet을 출력할 때 @BatchSize가 지정되어 있으므로 목록에서 나온 10개의 Board 객체의 bno 값을 이용하여
            board_image 테이블을 조회
        3. BoardImage들이 모두 조회되었으므로 나머지 목록을 처리할 때는 별도의 쿼리문을 실행하지 않고 처리된 결과만 보여지게 된다.

        주의할 점.
        목록과 관련된 처리는 반드시 limit와 같은 페이징 처리가 실행되는지 체크해야 한다.
        limit가 없다면 테이블의 모든 데이터에 대한 처리가 이루어진다는 것을 의미하기 때문에 성능에 영향을 주게된다.
        @EntityGraph를 이용해서 목록을 처리하지 않는 이유이기도 하다.
     */

    /*
        댓글의 개수와 DTO 처리
        추가로 한번 더 쿼리가 실행되긴 하지만 Board와 BoardImage들을 한 번에 처리할 수 있다는 점은 분명히
        장점이 될 수 있으므로 해당 결과에 댓글 개수를 처리하도록 수정해서 최종적으로 DTO를 구성하도록 한다.

        Entity 객체를 DTO로 변환하는 방식은 ModelMapper를 이용하거나, Projections를 이용했지만
        Board 객체 안에 Set과 같이 중첩된 구조를 처리할 경우에는 직접 튜플(Tuple)을 이용해서 DTO로 변환하는
        방식을 사용하는 것이 편리하다.

        - BoardListAllDTO 클래스
        1) dto 패키지에 Board와 BoardImage, replyCount를 모두 반영할 수 있는 BoardListAllDTO 클래스 추가
        2) BoardImage Entity 처리를 위한 BoardImageDTO 클래스 추가

        - BoardService 변경
        BoardService에 BoardListAllDTO를 이용할 수 있도록 새로운 listWithAll() 메소드를 추가
        
        - BoarServiceImpl 클래스에는 메소드 틀만 작성하고, Querydsl의 처리가 끝난 후에 수정
     */

    /*
        Queryldsl의 튜플 처리
        Querydsl을 이용해서 동적 쿼리를 처리하는 BoardSearch와 BoardSearchImpl 클래스의 리턴 타입은
        BoardListAllDTO 타입으로 수정
        
        이 과정에서 임시로 데이터를 튜플 타입으로 추출해서 처리하도록 한다
        BoardSearchImpl에는 searchWithAll()을 상세 구현
        
        List<Tuple>을 이용하는 방식은 Projections를 이용하는 방식보다 번거롭기는 하지만, 코드를 통해
        마음대로 커스터마이징 할 수 있다는 장점도 있다.

        BoardSearchImpl의 코드에서는 List<Tuple>의 결과를 List<BoardListAllDTO>로 변경하고 있다.
        (아직 BoardImage에 대한 처리는 하지 않은 상태)

        이전에 만들어둔 테스트 코드를 통해 searchWitAll() 동작을 확인
        테스트 결과는 BoardDTO에서 boardImages를 제외한 모든 처리가 완료된 형태로 출력됨.

     */

    /*
        - BoardImage 처리

        Board에 대한 처리 결과를 확인 후 Board 객체 내 BoardImage들을 추출해서 BoardImageDTO로 변환하는 코드 추가
        BoardSearchImpl - searchWithAll() 내 List<Tuple>을 처리하는 부분 작성

        작성 후에는 테스트 코드를 이용하여 쿼리들이 정상적으로 실행되는지 확인 ( testSearchImageReplyCount() )
        실행 결과에는 BoardListAllDTO 안에 BoardImageDTO 들이 존재하는 것을 확인할 수 있다.

        - 검색 조건 추가

        최종적으로 Qeurydsl을 이용해서 페이징 처리하기 전에 검색 조건과 키워드를 사용하는 부분의
        코드를 추가해서 searchWithAll()을 완성한다.
     */

    /*
        서비스 계층과 DTO

        첨부파일이 있는 게시물은 각 작업에 따라서 엔티티 설계와 다르게 처리될 부분이 많다.
        엔티티 클래스와 달리 DTO 클래스는 상황에 따라 여러 개의 클래스를 작성해서 처리하도록 한다.

        - 게시물 등록 처리
        게시물 등록 시 첨부파일은 이미 업로드된 파일의 정보를 문자열로 받아서 처리할 것
        따라서 등록에 사용할 BoardDTO에는 파일 이름을 리스트로 처리하도록 구성한다.

        BoardDTO의 List<String> fileNames는 Board에서 Set<BoardImages> 타입으로 변환되어야만 한다.

        - DTO를 Entity로 변환하기
        기존의 ModelMapper는 단순한 구조의 객체를 다른 타입의 객체로 만드는 데는 편리하지만 다양한 처리가 필요한
        경우에는 오히려 더 복잡하기 때문에 DTO 객체를 엔티티 객체로 변환하는 메소드를 작성하도록 한다.
        BoardService 인터페이스가 DTO와 Entity를 모두 처리하는 경우가 많으므로 BoardService 인터페이스의 default 메소드를
        이용해서 이를 처리하도록 한다.

        - 등록 처리와 테스트
        BoardService 인터페이스에 추가된 dtoToEntity()를 이용하여 BoardServiceImpl의 reigster()를
        수정하고 테스트 코드를 이용해서 확인한다.

        TestRegisterWithImages()는 하나의 게시물에 3개의 이미지 파일이 추가된 경우를 테스트한다.
        실행 결과에는 board 테이블과 board_image 테이블에 insert 문이 실행되는지 확인한다.
     */

    /*
        게시물 조회 처리

        Board Entity 객체를 BoardDTO 타입으로 변환하는 처리 역시 BoardService 인터페이스의
        default 메소드를 이용하여 처리하도록 한다.

        BoardServiceImpl에서는 entityToDTO()를 이용하여 BoardDTO를 반환하도록 수정한다.
        readOne()에서는 @EntityGraph를 이용하는 findByWithImages()를 이용

        테스트 코드를 통해 조회 시에 Board와 BoardImage들을 같이 처리하는지 확인한다.
        testReadOne() 테스트 시에 첨부파일이 있는 게시물의 번호를 이용하여 게시물과 첨부파일의 정보가
        한 번에 처리되는지를 확인하도록 한다.
     */

    /*
        게시물 수정 처리

        게시물 수정 시 첨부파일은 아예 새로운 파일들로 대체되기 때문에 Board의 clearImages()를 실행한 후에
        새로운 파일들의 정보를 추가하도록 구성한다.

        테스트 코드는 첨부파일이 있는 게시물을 대상으로 첨부파일을 변경하도록 한다.
        testModify()는 Board 객체의 BoardImage들을 삭제하고, 새로운 첨부파일 하나만을 가지도록 변경된다.
        testModify()를 실행한 후 testReadOne()을 이용해 수정된 게시물을 조회해본다.
        => 기존에 3개의 첨부파일을 가지고 있던 게시글이 테스트 코드 실행 후에는 1개의 첨부파일만을 가지고 있는 것을 확인할 수 있음
     */

    /*
        게시물 삭제 처리

        게시물의 삭제 처리는 '댓글'이; 존재하지 않는 경우만을 고려해서 작성한다.
        만일 댓글이 있는 경우에도 삭제하려면 ReplyRepository를 BoardService에 주입하고 특정한
        게시물의 모든 댓글을 삭제한 후에 게시물을 삭제하도록 작성해야 한다.

        Board 클래스에는 CascadeType.ALL과 orphanRemoval 속성값이 true로 지정되어 있으므로
        게시물이 삭제되면 자동으로 해당 게시물의 BoardImage 객체들도 같이 삭제되도록 구성되어 있다.

        testRemove()를 통한 테스트 실행 결과는 해당 게시글이 가지고 있는 첨부파일 수만큼 board_image에서
        delete가 실행되고 마지막으로 board 테이블에서 삭제가 일어나게 된다.
     */

    /*
        게시물 목록 처리

        BoardService의 마지막은 검색과 페이징 처리가 필요한 목록을 처리하는 기능의 구현이다.
        Querydsl을 적용하기 전에 이미 BoardService에 listWithAll()을 정의해 둔 상태

        BoardServiceImpl에서는 BoardListAllDTO 타입으로 반환되는 게시물 목록( listWithALll() )을
        PageResponseDTO로 처리한다.



     */



}
