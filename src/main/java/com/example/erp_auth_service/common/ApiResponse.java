package com.example.erp_auth_service.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse<T> {
    public T data;
    public String message;
    public int statusCode;
    public String error;
}
