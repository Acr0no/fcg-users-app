package com.example.fcgusersapp.repository;

import com.example.fcgusersapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Perform the search by name and surname ignoring case with pagination
     */
    Page<User> findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(String name, String surname, Pageable pageable);
}
