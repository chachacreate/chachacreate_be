package com.create.chacha.config.aws;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    private final AwsS3Properties properties;

    public AwsS3Config(AwsS3Properties properties) {
        this.properties = properties;
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                properties.getAccessKeyId(),
                                properties.getSecretAccessKey()
                        )
                ))
                .build();
    }
}



