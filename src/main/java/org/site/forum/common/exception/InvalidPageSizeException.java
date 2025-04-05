package org.site.forum.common.exception;

public class InvalidPageSizeException extends IllegalArgumentException {
    private final int maxPageSize;

    public InvalidPageSizeException(String message, int maxPageSize) {
        super(message);
        this.maxPageSize = maxPageSize;
    }

    public int getMaxPageSize() {
        return maxPageSize;
    }
}