package org.site.forum.common.exception;

import lombok.Getter;

@Getter
public class InvalidSortDirectionException extends IllegalArgumentException {

    private final String invalidDirection;
    private final Iterable<String> allowedDirections;

    public InvalidSortDirectionException(String invalidDirection, Iterable<String> allowedDirections) {
        super("Invalid sort direction: " + invalidDirection + ". Allowed directions: " + String.join(", ", allowedDirections));
        this.invalidDirection = invalidDirection;
        this.allowedDirections = allowedDirections;
    }

}