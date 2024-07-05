package com.example.restfileupload.controller;

import com.example.restfileupload.dto.upload.UploadResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class UpDownController {

    //  apllication.properties 파일의 설정 정보를 읽어서 변수의 값으로 사용 가능
    //  uploadPath는 나중에 파일을 업로드 하는 경로로 사용
    @Value("${org.example.upload.path}")    //  import 시에 springframework로 시작하는 Value
    private String uploadPath;

    /*
        UpDownController는 파일 업로드와 파일을 보여주는 기능을 메소드로 처리할 것
        @RestController로 설정하고 파일의 업로드를 처리하기 위해 upload()를 작성
     */
    @Operation(summary = "파일 등록", description = "POST 방식으로 파일을 등록합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(@RequestParam("files") List<MultipartFile> files) {

        if (files != null && !files.isEmpty()) {
            List<UploadResultDTO> list = new ArrayList<>();

            files.forEach(multipartFile -> {
                String originalName = multipartFile.getOriginalFilename();
                log.info(originalName);

                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);
                boolean image = false;

                try {
                    multipartFile.transferTo(savePath);

                    //이미지 파일의 종류라면
                    if(Files.probeContentType(savePath).startsWith("image")){

                        image = true;

                        File thumbFile = new File(uploadPath, "s_" + uuid+"_"+ originalName);

                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200,200); // 썸네일 표기??
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                list.add(UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(image).build()
                );
            });
            return list;
        }
        return null;
    }

    /*
        첨부파일 조회는 가능하면 GET 방식으로 가능하도록 설정
        추후 보안 문제를 고려하여 코드를 통해 접근 여부를 허용하도록 컨트롤러 이용
        첨부파일 조회 시 '/view/파일이름'으로 동작하도록 Controller 구성
     */
    @Operation(summary = "view 파일 - GET 방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {

        Resource resource = new FileSystemResource(uploadPath+File.separator + fileName);

        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType( resource.getFile().toPath()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    /*
        조회와 비슷한 DELETE 방식의 호출하는 형태로 첨부파일 삭제를 구현할 수 있음
        첨부파일을 삭제 시 해당 파일이 이미지라면 섬네일이 존재할 수 있으므로 같이 삭제하도록 구현
        마찬가지로 "/remove/{fileName}" 으로 동작하도록 Controller 구성
     */
    @Operation(summary = "remove 파일 - DELETE 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String, Boolean> removeFile(@PathVariable String fileName) {

        Resource resource = new FileSystemResource(uploadPath+File.separator + fileName);

        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();

        boolean removed = false;

        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();

            //  썸네일이 존재한다면
            if(contentType.startsWith("image")) {
                File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);

                thumbnailFile.delete();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        resultMap.put("result", removed);

        return resultMap;
    }

    /*
        프로젝트 개발 시 파일 업로드는 필수로 필요한 기능
        여러 곳에서 사용할 수 있어야 하고, 예제의 경우 이미지 파일에 한정 지어서 구성했지만
        일반 파일에 맞게 커스터마이징할 수도 있다.

        첨부파일을 일반 파일에도 사용하려면 이미지 파일이 아닐 때는 섬네일을 생성하지 않도록 해야 하고,
        GET 방식으로 이미지를 전송하는 방식 대신에 파일을 내려받도록 처리해야 한다.
     */
}
