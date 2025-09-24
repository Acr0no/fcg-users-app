package com.example.fcgusersapp.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiResponse {

    private static final String ERROR_KEY = "error";
    private static final String DESCRIPTION_KEY = "error_description";
    private static final String STATUS_KEY = "status";
    private static final String USER_KEY = "user";
    private static final String USERS_SAVED_BY_CSV_KEY = "numero utenti inseriti";
    private static final String USERS_NOT_SAVED_BY_CSV_KEY = "numero utenti non inseriti";
    private static final String USERS_DUPLICATE_KEY = "duplicates";

    private ApiResponse() {
        throw new IllegalStateException("Thi class is an utility class and cannot be instantiated");
    }

    public static Map<String, String> errorResponse(String description, String error) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(STATUS_KEY, "ko");
        errorMap.put(DESCRIPTION_KEY, description);
        errorMap.put(ERROR_KEY, error);
        return errorMap;
    }

    public static Map<String, Object> successResponse(String message, Object user) {
        return Map.of(
                STATUS_KEY, "ok",
                DESCRIPTION_KEY, message,
                USER_KEY, user
        );
    }

    public static Map<String, Object> successResponse(String message) {
        return Map.of(
                STATUS_KEY, "ok",
                DESCRIPTION_KEY, message
        );
    }

    public static Map<String, Object> csvImportResponse(int usersInserted, List<Map<String, Object>> errors) {
        return Map.of(
                STATUS_KEY, "ok",
                USERS_SAVED_BY_CSV_KEY, usersInserted,
                USERS_NOT_SAVED_BY_CSV_KEY, errors.size(),
                USERS_DUPLICATE_KEY, errors);
    }
}
