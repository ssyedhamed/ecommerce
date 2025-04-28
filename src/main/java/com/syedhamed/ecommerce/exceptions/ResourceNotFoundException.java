package com.syedhamed.ecommerce.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String field;
    Object fieldValue;


    public ResourceNotFoundException(){}

    public ResourceNotFoundException(String resourceName, String field, Object fieldValue) {
        super(String.format("%s is not found with %s:%s ",resourceName,field,fieldValue));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldValue = fieldValue;
    }
}
