package org.site.forum.common.exception;

public class InvalidTopicRequestException extends RuntimeException {

    public InvalidTopicRequestException(String message) {
        super(message);
    }

}