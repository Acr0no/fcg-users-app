package com.example.fcgusersapp.repository;

import com.example.fcgusersapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
