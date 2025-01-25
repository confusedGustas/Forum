package org.site.forum.constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestConstants {

    public static final String UUID_CONSTANT = "123e4567-e89b-12d3-a456-426614174000";
    public static final String TITLE = "Test title";
    public static final String CONTENT = "Test content";
    public static final LocalDateTime CREATED_AT = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")));

}
