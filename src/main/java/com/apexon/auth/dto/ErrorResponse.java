package com.apexon.auth.dto;

import com.apexon.auth.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
    private int status;
}
