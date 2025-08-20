package com.create.chacha;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ChachacreateApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load(); // .env 파일을 가져옴
        System.setProperty("JASYPT_ENCRYPTOR_PASSWORD", dotenv.get("JASYPT_ENCRYPTOR_PASSWORD")); // .env파일에서 JASYPT_ENCRYPTOR_PASSWORD를 가져옴
        System.setProperty("DATABASE_AES_PASSWORD", dotenv.get("DATABASE_AES_PASSWORD")); // .env파일에서 DATABASE_AES_PASSWORD를 가져옴
        SpringApplication.run(ChachacreateApplication.class, args);
    }

}
