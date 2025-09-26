package com.example.fcgusersapp.entity;

import com.example.fcgusersapp.constants.DatabaseTableColumns;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = DatabaseTableColumns.USERS_TABLE, schema = DatabaseTableColumns.USERS_TABLE_SCHEMA)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = DatabaseTableColumns.USERS_MAIL_NAME, nullable = false)
    private String email;

    @Column(name = DatabaseTableColumns.USERS_NAME_COLUMN, nullable = false)
    private String name;

    @Column(name = DatabaseTableColumns.USER_SURNAME_COLUMN, nullable = false)
    private String surname;

    @Column(name = DatabaseTableColumns.USER_ADDRESS_COLUMN, nullable = false)
    private String address;
}
