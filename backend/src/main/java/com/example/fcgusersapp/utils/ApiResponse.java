package com.example.fcgusersapp.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for building standard API response objects
 * to ensure consistent response formats across controllers.
 * This class cannot be instantiated.
 */
public class ApiResponse {

    //keys of map
    private static final String ERROR_KEY = "error";
    private static final String DESCRIPTION_KEY = "error_description";
    private static final String STATUS_KEY = "status";
    private static final String USER_KEY = "user";
    private static final String USERS_SAVED_BY_CSV_KEY = "numero utenti inseriti";
    private static final String USERS_NOT_SAVED_BY_CSV_KEY = "numero utenti non inseriti";
    private static final String USERS_DUPLICATE_KEY = "duplicates";


    private ApiResponse() {
        throw new IllegalStateException("This class is a utility class and cannot be instantiated");
    }

    /**
     * Builds a standardized error response map.
     *
     * @param description human-readable description of the error
     * @param error       technical error details
     * @return a map containing status = "ko", error description, and error details
     */
    public static Map<String, String> errorResponse(String description, String error) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(STATUS_KEY, "ko");
        errorMap.put(DESCRIPTION_KEY, description);
        errorMap.put(ERROR_KEY, error);
        return errorMap;
    }

    /**
     * Builds a standardized success response map including an attached object.
     *
     * @param message success message
     * @param user    object to include in the response (typically a {@code User})
     * @return a map containing status = "ok", the success message, and the user object
     */
    public static Map<String, Object> successResponse(String message, Object user) {
        return Map.of(
                STATUS_KEY, "ok",
                DESCRIPTION_KEY, message,
                USER_KEY, user
        );
    }

    /**
     * Builds a standardized success response map without an extra payload.
     *
     * @param message success message
     * @return a map containing status = "ok" and the success message
     */
    public static Map<String, Object> successResponse(String message) {
        return Map.of(
                STATUS_KEY, "ok",
                DESCRIPTION_KEY, message
        );
    }

    /**
     * Builds a response summarizing the results of a CSV import operation.
     *
     * @param usersInserted number of successfully inserted users
     * @param errors        list of errors encountered (duplicates)
     * @return a map containing status = "ok", counts of inserted and not-inserted users,
     * and a list of duplicates or errors
     */
    public static Map<String, Object> csvImportResponse(int usersInserted, List<Map<String, Object>> errors) {
        return Map.of(
                STATUS_KEY, "ok",
                USERS_SAVED_BY_CSV_KEY, usersInserted,
                USERS_NOT_SAVED_BY_CSV_KEY, errors.size(),
                USERS_DUPLICATE_KEY, errors
        );
    }
}
