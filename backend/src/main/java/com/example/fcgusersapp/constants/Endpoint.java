package com.example.fcgusersapp.constants;

public class Endpoint {

    private Endpoint() {
        throw new IllegalStateException("Thi class is an utility class and cannot be instantiated");
    }

    public static final String USERS_ENDPOINT_ROOT = "api/v1/";
    public static final String ADD_USER = "/user";
    public static final String EDIT_USER = "/user/{id}";
    public static final String DELETE_USER = "/user/{id}";
    public static final String GET_USERS = "/users";
    public static final String UPLOAD_USER_CSV = "/upload-user-csv";
}
