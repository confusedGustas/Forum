package org.site.forum.domain.file.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.vision.CloudVisionTemplate;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ImageModerationService {
    private final CloudVisionTemplate cloudVisionTemplate;
    private final ObjectMapper objectMapper;
    private Set<String> disallowedLabels;

    @SneakyThrows
    @PostConstruct
    private void loadDisallowedLabels() {
        var resource = new ClassPathResource("disallowed-content/disallowed-image-content.json");
        List<String> labels = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        disallowedLabels.addAll(labels);

    }

    @SneakyThrows
    public boolean validateImageContent(MultipartFile file) {
        byte[] imageBytes = file.getBytes();
        String detectedObject = detectObjectInImage(imageBytes);
        List<String> detectedLabels = detectLabelsInImage(imageBytes);

        if (detectedObject != null && disallowedLabels.contains(detectedObject)) {
            return false;
        }

        for (String label : detectedLabels) {
            if (disallowedLabels.contains(label)) {
                return false;
            }
        }

        return true;
    }

    private List<String> detectLabelsInImage(byte[] imageBytes) {
        ByteArrayResource byteArrayResource = new ByteArrayResource(imageBytes);

        AnnotateImageResponse response = cloudVisionTemplate.analyzeImage(byteArrayResource, Feature.Type.LABEL_DETECTION);

        return response.getLabelAnnotationsList()
                .stream()
                .map(EntityAnnotation::getDescription)
                .toList();
    }

    public String detectObjectInImage(byte[] image) {
        ByteArrayResource byteArrayResource = new ByteArrayResource(image);

        AnnotateImageResponse response = this.cloudVisionTemplate.analyzeImage(byteArrayResource, Feature.Type.OBJECT_LOCALIZATION);

        if(response.getLocalizedObjectAnnotationsCount() == 0) {
            return null;
        }

        return response.getLocalizedObjectAnnotations(0).getName();
    }
}
