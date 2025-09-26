package com.example.fcgusersapp.constants;

/**
 * Holds constants related to the database tables and their columns
 * used throughout the application.
 * <p>
 * This is a utility class and cannot be instantiated.
 */
public class DatabaseTableColumns {

    /**
     * Name of the users table.
     */
    public static final String USERS_TABLE = "users";

    /**
     * Schema of the user's table.
     */
    public static final String USERS_TABLE_SCHEMA = "public";

    /**
     * Column name for the user's email address.
     */
    public static final String USERS_MAIL_NAME = "email";

    /**
     * Column name for the user's first name.
     */
    public static final String USERS_NAME_COLUMN = "name";

    /**
     * Column name for the user's last name.
     */
    public static final String USER_SURNAME_COLUMN = "surname";

    /**
     * Column name for the user's address.
     */
    public static final String USER_ADDRESS_COLUMN = "address";

    /**
     * Private constructor to prevent instantiation.
     *
     * @throws IllegalStateException always, since this class is a utility
     *                               class and should not be instantiated.
     */
    private DatabaseTableColumns() {
        throw new IllegalStateException("This class is a utility class and cannot be instantiated");
    }
}
