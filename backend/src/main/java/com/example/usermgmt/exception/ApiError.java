package com.example.usermgmt.exception;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiError {
    OffsetDateTime timestamp;
    String path;
    int status;
    String code;
    String message;
    List<String> details;
}

