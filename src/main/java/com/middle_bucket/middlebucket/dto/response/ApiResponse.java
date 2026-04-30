package com.middle_bucket.middlebucket.dto.response;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ApiResponse <T> {
    private boolean succes;
    private String message;
    private T data;
    private String timeStamp;

    public ApiResponse(boolean succes, String message, T data) {
        this.succes = succes;
        this.message = message;
        this.data = data;
        this.timeStamp = LocalDateTime.now().toString();
    }

    public static <T> ApiResponse<T> succes(T data) {
        return new ApiResponse<>(true, "succes", data);
    }

    public  static  <T> ApiResponse<T> succes(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>(false, message, null);
    }
}
