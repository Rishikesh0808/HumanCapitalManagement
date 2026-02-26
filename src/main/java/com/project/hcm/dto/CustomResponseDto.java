package com.project.hcm.dto;

public class CustomResponseDto {
    private int statusCode;
    private String message;
    private Object data;

    public CustomResponseDto() {
    }

    public CustomResponseDto(int statusCode, Object data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
