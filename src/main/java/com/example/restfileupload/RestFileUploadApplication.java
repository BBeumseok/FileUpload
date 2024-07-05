package com.example.restfileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  //AuditingEntityListeners 활성화 어노테이션
public class RestFileUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestFileUploadApplication.class, args);
    }

}
