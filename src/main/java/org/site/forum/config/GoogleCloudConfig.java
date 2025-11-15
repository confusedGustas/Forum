package org.site.forum.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spring.vision.CloudVisionTemplate;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class GoogleCloudConfig {

    @Bean
    public CloudVisionTemplate cloudVisionTemplate() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ClassPathResource("google.json").getInputStream()
        );

        ImageAnnotatorSettings imageAnnotatorSettings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        ImageAnnotatorClient imageAnnotatorClient = ImageAnnotatorClient.create(imageAnnotatorSettings);

        return new CloudVisionTemplate(imageAnnotatorClient);
    }
}
