package com.create.chacha;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Base64;

@Slf4j
@EnableJpaAuditing
@SpringBootApplication
public class ChachacreateApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ChachacreateApplication.class);

        // ApplicationContextInitializer를 사용해 컨텍스트 초기화 전에 프로퍼티 설정
        app.addInitializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
            @Override
            public void initialize(ConfigurableApplicationContext applicationContext) {
                Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

                String jasyptPassword = dotenv.get("JASYPT_ENCRYPTOR_PASSWORD"); // .env파일에서 JASYPT_ENCRYPTOR_PASSWORD를 가져옴
                String base64AesPassword = dotenv.get("DATABASE_AES_PASSWORD"); // .env파일에서 DATABASE_AES_PASSWORD를 가져옴

                if (jasyptPassword != null) {
                    System.setProperty("JASYPT_ENCRYPTOR_PASSWORD", jasyptPassword);
                    log.info("JASYPT from .env: " + (jasyptPassword != null ? "존재함" : "null"));
                }

                if (base64AesPassword != null) {
                    String decodedPassword = new String(Base64.getDecoder().decode(base64AesPassword));
                    System.setProperty("DATABASE_AES_PASSWORD", decodedPassword);
                    log.info("DATABASE_AES from .env: " + (base64AesPassword != null ? "존재함" : "null"));
                }

                log.info("프로퍼티 설정 완료");
            }
        });

        app.run(args);
    }
}
