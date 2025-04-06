package org.site.forum.common.exception;

public class InvalidCommentRequestException extends RuntimeException {

    public InvalidCommentRequestException(String message) {
        super(message);
    }
}
