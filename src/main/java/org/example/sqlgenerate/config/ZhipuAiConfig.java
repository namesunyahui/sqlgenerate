package org.example.sqlgenerate.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@Data
@Configuration
public class ZhipuAiConfig {


    @Value("${spring.cloud.zhipu.api-key}")
    private String apiKey ;

    @Bean
    public ClientV4 clientV4() {
        return  new ClientV4.Builder(apiKey)
                .networkConfig(300, 100, 100, 100, TimeUnit.SECONDS)
                .connectionPool(new okhttp3.ConnectionPool(8, 1, TimeUnit.SECONDS))
                .build();
    }


}
