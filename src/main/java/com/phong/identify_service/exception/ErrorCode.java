package com.phong.identify_service.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(999, "Uncategorized error"),
    INVALID_KEY(998, "Invalid message key"),
    USER_EXISTED(400, "User existed"),
    USERNAME_INVALID(401, "Username must be at least 3 characters"),
    PASSWORD_INVALID(402, "Password must be from 8 to 50 characters"),
    USER_NOT_EXISTED(403, "User is not existed"),
    UNAUTHENTICATED(404, "Unauthenticated")
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
