package com.wetagustin.jwtsecurityservice.dtos.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private String message;
    private T data;
    private Map<String, Object> extraData;
    private LocalDateTime timestamp;
    
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ApiResponse(String message) {
        this();
        this.message = message;
    }
    
    public ApiResponse(String message, T data) {
        this(message);
        this.data = data;
    }
    
    public static <T> ApiResponse<T> of(String message) {
        return new ApiResponse<>(message);
    }
    
    public static <T> ApiResponse<T> of(String message, T data) {
        return new ApiResponse<>(message, data);
    }
    
    public ApiResponse<T> withExtraData(String key, Object value) {
        if (this.extraData == null) {
            this.extraData = new java.util.HashMap<>();
        }
        this.extraData.put(key, value);
        return this;
    }
    
    public ApiResponse<T> withExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
        return this;
    }
}