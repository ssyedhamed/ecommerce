package com.syedhamed.ecommerce.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL) //nulls don’t show up unless needed.
@Data
public class APIResponse<T> {
    private String message;
    private boolean status;
    private T data;
    public APIResponse() {
    }

    public APIResponse(String message, boolean status) {
        this.message = message;
        this.status = status;
    }

    public APIResponse(T data, String message, boolean status){
        this.data = data;
        this.message = message;
        this.status= status;
    }


}
