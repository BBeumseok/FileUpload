package com.example.restfileupload.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {
    //  버전에 따라 SwaggerConfig 설정에 차이가 있음
    //  Docket 대신 OpenAPI 사용

    @Bean
    public OpenAPI openAPI() {
        //  Security Schema 설정
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("Authorization")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        //  Security 요청 설정
        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList("Authorization");

        return new OpenAPI()
                .components(
                        new Components().addSecuritySchemes("Authorization", bearerAuth)
                )
                //  API 마다 Security 인증 컴포넌트 설정
                .addSecurityItem(addSecurityItem)
                .info(new Info().title("SpringDoc SwaggerUI Example")
                        .description("Test SwaggerUI application")
                        .version("v0.0.1"));
    }

    /*
        Swagger-UI에서 Header 처리
        'Authorization'과 같이 보안과 관련된 헤더를 추가하기 위해서 config 패키지에 SwaggerConfig를 수정
        (import 할 때 Spring이 아닌 Swagger 관련 API를 이용해야 하므로 주의가 필요함)

        변경된 SwaggerConfig에서는 '/api'로 시작하는 경로들에 대해서 Authorization Header를 지정하도록 설정

        설정이 반영되면 '/swagger-ui/index.html'에서는 [Authorization] 버튼이 생성되고,
        Authorization 헤더의 값을 입력할 수 있는 모달창이 보이게 된다.

        SwaggerConfig 경로에는 '/api'로 시작하는 경로에서만 인증 처리가 핊요하기 때문에
        기존의 '/generateToken'에는 영향을 받지 않는다.
     */
}
