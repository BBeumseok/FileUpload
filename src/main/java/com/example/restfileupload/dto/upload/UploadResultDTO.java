package com.example.restfileupload.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO {
    /*
        여러 개의 파일이 업로드 되면 업로드 결과도 여러 개 발생하게 되고, 여러 정보를
        반환해야 하므로 별도의 DTO를 구성해서 반환
        UploadResultDTO는 업로드된 파일의 UUID 값과 파일 이름(fileName), 이미지 여부를
        객체로 구성, getLink()를 통해 첨부파일의 경로 처리에 사용함
     */
    
    private String uuid;

    private String fileName;

    private boolean img;

    //  JSON으로 처리될 때는 link 라는 속성으로 자동 처리된다.
    private String getLink() {
        if(img) {
            return "s_" + uuid + "_" + fileName;    //  이미지인 경우 썸네일
        }else {
            return uuid + "_" + fileName;
        }
    }
}
