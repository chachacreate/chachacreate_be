package com.create.chacha;

import com.create.chacha.common.util.LegacyAPIUtil;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = ChachacreateApplicationTests.EnvInitializer.class)
class ChachacreateApplicationTests {

    static class EnvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            String jasyptPassword = dotenv.get("JASYPT_ENCRYPTOR_PASSWORD");
            if (jasyptPassword != null) {
                System.setProperty("JASYPT_ENCRYPTOR_PASSWORD", jasyptPassword);
            }

            String base64AesPassword = dotenv.get("DATABASE_AES_PASSWORD");
            if (base64AesPassword != null) {
                String decodedPassword = new String(java.util.Base64.getDecoder().decode(base64AesPassword));
                System.setProperty("DATABASE_AES_PASSWORD", decodedPassword);
            }
        }
    }

    @Autowired
    LegacyAPIUtil legacyAPIUtil;

    @Test
    void contextLoads() {
        System.out.println(legacyAPIUtil.getLegacySellerData("dojagi"));
    }
}
