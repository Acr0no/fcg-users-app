package com.example.fcgusersapp.constants;

public class DatabaseTableColumns {


    public static final String USERS_TABLE = "users";
    public static final String USERS_TABLE_SCHEMA = "public";
    public static final String USERS_MAIL_NAME = "email";
    public static final String USERS_NAME_COLUMN = "name";
    public static final String USER_SURNAME_COLUMN = "surname";
    public static final String USER_ADDRESS_COLUMN = "address";

    private DatabaseTableColumns() {
        throw new IllegalStateException("Thi class is an utility class and cannot be instantiated");
    }
}
