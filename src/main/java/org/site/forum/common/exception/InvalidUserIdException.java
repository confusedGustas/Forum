package org.site.forum.common.exception;

public class InvalidUserIdException extends RuntimeException {

    public InvalidUserIdException(String message) {
        super(message);
    }

}