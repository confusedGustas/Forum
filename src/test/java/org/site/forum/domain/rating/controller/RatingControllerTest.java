package org.site.forum.domain.rating.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.site.forum.domain.rating.service.RatingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingControllerTest {

    @Mock
    private RatingService ratingService;
    @InjectMocks
    private RatingController ratingController;

    private final UUID testTopicId = UUID.randomUUID();

    @Test
    void rateTopicEndpoint_CallsServiceWithParameters() {
        int testRatingValue = 1;
        ratingController.rateTopic(testTopicId, testRatingValue);

        verify(ratingService).rateTopic(testTopicId, testRatingValue);
    }

    @Test
    void controllerClassAnnotations() {
        RestController restAnnotation = RatingController.class.getAnnotation(RestController.class);
        RequestMapping requestMapping = RatingController.class.getAnnotation(RequestMapping.class);

        assertNotNull(restAnnotation);
        assertEquals("/ratings", requestMapping.value()[0]);
    }

    @Test
    void rateTopicMethodAnnotations() throws NoSuchMethodException {
        PostMapping postMapping = RatingController.class.getMethod("rateTopic", UUID.class, Integer.class)
                .getAnnotation(PostMapping.class);
        PreAuthorize preAuthorize = RatingController.class.getMethod("rateTopic", UUID.class, Integer.class)
                .getAnnotation(PreAuthorize.class);

        assertNotNull(postMapping);
        assertEquals("hasRole('client_user')", preAuthorize.value());
    }

}