package com.example.fcgusersapp.service;

import com.example.fcgusersapp.entity.User;
import com.example.fcgusersapp.exceptions.CsvImportException;
import com.example.fcgusersapp.repository.UserRepository;
import com.example.fcgusersapp.utils.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    @Transactional
    public User editUser(User user, Long id) {
        User userToEdit = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User " + id + " not found"));
        userToEdit.setEmail(user.getEmail());
        userToEdit.setName(user.getName());
        userToEdit.setSurname(user.getSurname());
        userToEdit.setAddress(user.getAddress());
        return userRepository.save(userToEdit);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    @Transactional
    public void deleteUser(Long id) {
        User userToEdit = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User " + id + " not found"));
        this.userRepository.deleteById(userToEdit.getId());
    }


    public Map<String, Object> importUsersFromCsv(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null && !filename.toLowerCase().endsWith(".csv")) {
            throw new CsvImportException("Estensione file non valida: richiesto .csv");
        }
        int usersInserted = 0;
        List<Map<String, Object>> errors = new ArrayList<>();
        try (var reader = new BufferedReader(getFileInputStream(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] cols = line.split(",", -1);
                if (cols.length < 4) {
                    throw new CsvImportException("Errore nel formato del file CSV");
                }
                User userToSave = new User();
                userToSave.setEmail(cols[0].trim()); //email
                userToSave.setName(cols[1].trim()); //nome
                userToSave.setSurname(cols[2].trim()); //cognome
                userToSave.setAddress(cols[3].trim()); //indirizzo
                try {
                    userRepository.save(userToSave);
                    usersInserted++;
                } catch (DataIntegrityViolationException e) {
                    errors.add(Map.of("email", userToSave.getEmail()));
                }
            }
        } catch (IOException e) {
            throw new CsvImportException("Errore durante l'import CSV");
        }

        return ApiResponse.csvImportResponse(usersInserted, errors);

    }


    private InputStreamReader getFileInputStream(MultipartFile file) throws IOException {
        return new InputStreamReader(file.getInputStream());
    }
}
