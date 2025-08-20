package com.create.chacha.config.app;

import com.create.chacha.common.util.S3Uploader;
import com.create.chacha.config.aws.AwsS3Properties;
import com.create.chacha.config.aws.UploadProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AppConfig {

    private final AwsS3Properties s3Properties;
    private final UploadProperties uploadProperties;
    private final S3Client s3Client;

    public AppConfig(AwsS3Properties s3Properties, UploadProperties uploadProperties, S3Client s3Client) {
        this.s3Properties = s3Properties;
        this.uploadProperties = uploadProperties;
        this.s3Client = s3Client;
    }

    @Bean
    public S3Uploader s3Uploader() {
        return new S3Uploader(
                s3Client,
                s3Properties.getBucketName(),
                uploadProperties.getMaxFileSize()
        );
    }
}
