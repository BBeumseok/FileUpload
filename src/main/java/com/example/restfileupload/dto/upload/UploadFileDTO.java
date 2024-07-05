package com.example.restfileupload.dto.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFileDTO {
    /*
        파일 업로드는 MultipartFile이라는 API를 이용하여 처리
        컨트롤러에서 파라미터를 MultipartFile로 지정해주면 간단한 파일 업로드 처리는 가능하지만
        Swagger-UI와 같은 프레임워크로 테스트하기 불편하므로 dto 패키지에 별도의 DTO로 선언
     */

    private List<MultipartFile> files;
}
