package com.example.fcgusersapp.entity;

import com.example.fcgusersapp.constants.DatabaseTableColumns;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing an application user.
 * <p>
 * Uses Lombok annotations for boilerplate code such as getters,
 * setters, constructors, and builder pattern.
 */
@Entity
@Table(name = DatabaseTableColumns.USERS_TABLE, schema = DatabaseTableColumns.USERS_TABLE_SCHEMA)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Primary key of the user.
     * Generated automatically with identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email address of the user.
     * <p>
     * This field is required and must be unique.
     */
    @Column(name = DatabaseTableColumns.USERS_MAIL_NAME, nullable = false)
    private String email;

    /**
     * First name of the user.
     * <p>
     * This field is required.
     */
    @Column(name = DatabaseTableColumns.USERS_NAME_COLUMN, nullable = false)
    private String name;

    /**
     * Last name of the user.
     * <p>
     * This field is required.
     */
    @Column(name = DatabaseTableColumns.USER_SURNAME_COLUMN, nullable = false)
    private String surname;

    /**
     * Physical address of the user.
     * <p>
     * This field is required.
     */
    @Column(name = DatabaseTableColumns.USER_ADDRESS_COLUMN, nullable = false)
    private String address;
}
