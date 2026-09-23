package com.airbnb.project.restAdvices;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponseAPI<T> {
    private ApiError error;
    private T data;
    private LocalDateTime timestamp;


    public ResponseAPI() {
        this.timestamp = LocalDateTime.now();
    }



    public ResponseAPI(T data) {
        this();
        if( data instanceof ApiError) {
            this.error = (ApiError) data;
        }
        else
            this.data = data;
    }


}
