package com.example.fcgusersapp.constants;

/**
 * Holds constants related to REST API endpoints used in the application.
 * <p>
 * This is a utility class and cannot be instantiated.
 */
public class Endpoint {

    /**
     * Private constructor to prevent instantiation.
     *
     * @throws IllegalStateException since this class is always a utility class and should not be instantiated.
     */
    private Endpoint() {
        throw new IllegalStateException("This class is a utility class and cannot be instantiated");
    }

    /**
     * Root path for all user-related API endpoints.
     */
    public static final String USERS_ENDPOINT_ROOT = "api/v1/";

    /**
     * Endpoint for adding a new user.
     */
    public static final String ADD_USER = "/user";

    /**
     * Endpoint for finding or updating a user by ID.
     */
    public static final String FIND_OR_UPDATE_USER = "/user/{id}";

    /**
     * Endpoint for deleting a user by ID.
     */
    public static final String DELETE_USER = "/user/{id}";

    /**
     * Endpoint for retrieving all users.
     */
    public static final String GET_USERS = "/users";

    /**
     * Endpoint for uploading a CSV file with user data.
     */
    public static final String UPLOAD_USER_CSV = "/upload-user-csv";

    /**
     * URL allowed for CORS requests from the frontend application.
     */
    public static final String CORS_URL_FE = "http://localhost:4200";
}
