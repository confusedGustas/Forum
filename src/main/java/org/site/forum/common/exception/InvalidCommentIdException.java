package org.site.forum.common.exception;

public class InvalidCommentIdException extends RuntimeException {
    public InvalidCommentIdException(String message) {
        super(message);
    }
}
