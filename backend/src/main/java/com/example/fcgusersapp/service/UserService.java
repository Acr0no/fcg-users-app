package com.example.fcgusersapp.service;

import com.example.fcgusersapp.entity.User;
import com.example.fcgusersapp.exceptions.CsvImportException;
import com.example.fcgusersapp.repository.UserRepository;
import com.example.fcgusersapp.utils.ApiResponse;
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

/**
 * Service layer providing operations for managing {@link User} entities,
 * including CRUD actions and CSV import functionality.
 * <p>
 * This service interacts with the {@link UserRepository} for persistence
 * and contains transactional boundaries where required.
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Persists a new user in the database.
     *
     * @param user the user entity to be saved
     */
    @Transactional
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    /**
     * Finds a user by its ID.
     *
     * @param id the user ID
     * @return an {@link Optional} containing the {@link User} if found,
     * or empty if not present
     */
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Updates an existing user with new data.
     *
     * @param user the updated user data
     * @param id   the ID of the user to update
     * @return the updated {@link User}
     * @throws IllegalArgumentException if no user with the given ID exists
     */
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

    /**
     * Deletes a user by its ID.
     *
     * @param id the ID of the user to delete
     * @throws IllegalArgumentException if no user with the given ID exists
     */
    @Transactional
    public void deleteUser(Long id) {
        User userToEdit = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User " + id + " not found"));
        this.userRepository.deleteById(userToEdit.getId());
    }

    /**
     * Imports users from a CSV file. The file must have the following format:
     * <pre>
     * email,name,surname,address
     * </pre>
     * The first line (header) is skipped.
     *
     * @param file the uploaded CSV file
     * @return a report map {@link ApiResponse}  with the number of inserted users and any errors encountered
     * @throws CsvImportException if the file extension is not CSV, if the format is invalid, or if an I/O error occurs
     */
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
                User userToSave = User.builder()
                        .email(cols[0].trim())
                        .name(cols[1].trim())
                        .surname(cols[2].trim())
                        .address(cols[3].trim())
                        .build();
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

    /**
     * Opens an {@link InputStreamReader} for the uploaded CSV file.
     *
     * @param file the uploaded multipart file
     * @return an {@link InputStreamReader} for reading the file contents
     * @throws IOException if the file stream cannot be opened
     */
    private InputStreamReader getFileInputStream(MultipartFile file) throws IOException {
        return new InputStreamReader(file.getInputStream());
    }

    /**
     * Searches users by first name and surname with pagination.
     * The search is case-insensitive and matches partial values.
     *
     * @param name     optional filter for the user's first name (empty string if {@code null})
     * @param surname  optional filter for the user's surname (empty string if {@code null})
     * @param pageable pagination and sorting configuration
     * @return a {@link Page} of users matching the criteria
     */
    public Page<User> searchUsers(String name, String surname, Pageable pageable) {
        String nameToSearch = name == null ? "" : name;
        String surnameToSearch = surname == null ? "" : surname;
        return userRepository.findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(nameToSearch, surnameToSearch, pageable);
    }
}
