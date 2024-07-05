package com.example.restfileupload.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {
    /*
        ModelMapper란?
        어떤 Object에 있는 필드값들을 자동으로 원하는 Object로 Mapping 시켜준다.

        ModelMapper 전략 종류
        - MatchingStrategies.STANDARD : 지능적으로 매핑 해준다.
        source 속성을 destination 속성과 지능적으로 일치시킬 수 있으므로, 모든 destination 속성이 일치하고
        모든 source 속성 이름에 토큰이 하나 이상 일치해야 함

        - MatchingStrategies.STRICT : 정확히 일치하는 필드만 매핑 해준다.
        source 속성과 destination 속성의 이름이 정확히 일치할 때만 매핑 해준다.

        - MatchingStrategies.LOOSE : 가장 느슨한 결합으로 매핑 해준다.
        계층 구조의 마지막 destination 속성만 일치하도록 하여 source 속성을 destination 속성에 일치시킬 수 있다.
     */
    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

}
