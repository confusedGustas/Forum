package org.site.forum.common.exception;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

}