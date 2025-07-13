package com.wetagustin.jwtsecurityservice.dtos.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper for all endpoints")
public class ApiResponse<T> {
    
    @Schema(description = "Response message describing the operation result", example = "Operation successful")
    private String message;
    
    @Schema(description = "Response data of generic type T")
    private T data;
    
    @Schema(description = "Additional metadata for the response")
    private Map<String, Object> extraData;
    
    @Schema(description = "Timestamp when the response was generated", example = "2024-01-01T12:00:00")
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