package org.site.forum.common.exception;

import lombok.Getter;

@Getter
public class InvalidSortFieldException extends IllegalArgumentException {

    private final String invalidField;
    private final Iterable<String> allowedFields;

    public InvalidSortFieldException(String invalidField, Iterable<String> allowedFields) {
        super("Invalid sort field: " + invalidField + ". Allowed fields: " + String.join(", ", allowedFields));
        this.invalidField = invalidField;
        this.allowedFields = allowedFields;
    }

}