package org.site.forum.common.exception;

import lombok.Getter;

@Getter
public class InvalidPageSizeException extends IllegalArgumentException {

    private final int maxPageSize;

    public InvalidPageSizeException(String message, int maxPageSize) {
        super(message);
        this.maxPageSize = maxPageSize;
    }

}