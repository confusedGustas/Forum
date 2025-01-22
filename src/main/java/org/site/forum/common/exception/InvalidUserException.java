package org.site.forum.common.exception;

public class InvalidUserException extends RuntimeException {

    public InvalidUserException(String message) {
        super(message);
    }

}