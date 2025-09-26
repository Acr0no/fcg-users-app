package com.example.fcgusersapp.controller;

import com.example.fcgusersapp.constants.Endpoint;
import com.example.fcgusersapp.entity.User;
import com.example.fcgusersapp.exceptions.CsvImportException;
import com.example.fcgusersapp.service.UserService;
import com.example.fcgusersapp.utils.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * REST controller exposing CRUD and CSV-import endpoints for {@link User} resources.
 * All endpoints are rooted at {@link Endpoint#USERS_ENDPOINT_ROOT} and allow CORS
 * from {@link Endpoint#CORS_URL_FE}.
 */
@CrossOrigin(origins = Endpoint.CORS_URL_FE)
@RestController
@RequestMapping(Endpoint.USERS_ENDPOINT_ROOT)
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     *
     * @param user the user payload to persist
     * @return {@code 200 OK} with a success {@link ApiResponse} containing the created user;
     * {@code 409 Conflict} with an error {@link ApiResponse} if a unique constraint (email) is violated
     * or a generic error occurs.
     */
    @PostMapping(Endpoint.ADD_USER)
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        try {
            this.userService.saveUser(user);
            return ResponseEntity.status(200).body(
                    ApiResponse.successResponse("Utente inserito con successo", user)
            );
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(ApiResponse.errorResponse("Utente con e-mail " + user.getEmail() + " già presente", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(409).body(ApiResponse.errorResponse("Errore nell'inserimento dell'utente", e.getMessage()));
        }
    }

    /**
     * Updates an existing user identified by ID.
     *
     * @param user the new data to apply to the user
     * @param id   the ID of the user to update
     * @return {@code 200 OK} with a success {@link ApiResponse} containing the updated user;
     * {@code 409 Conflict} with an error {@link ApiResponse} if the email is already used
     * or the provided arguments are invalid.
     */
    @PutMapping(Endpoint.FIND_OR_UPDATE_USER)
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable("id") Long id) {
        try {
            User updateUser = this.userService.editUser(user, id);
            return ResponseEntity.status(200).body(
                    ApiResponse.successResponse("Utente modificato con successo", updateUser)
            );
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(ApiResponse.errorResponse("Utente con e-mail " + user.getEmail() + " già presente", e.getMessage()));
        }

    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return {@code 200 OK} with the {@link User} if found
     * @throws ResponseStatusException {@code 404 NOT FOUND} if no user exists with the given ID
     */
    @GetMapping(Endpoint.FIND_OR_UPDATE_USER)
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * Searches users with optional filters and pagination.
     *
     * @param pageable Spring Data paging/sorting information. Defaults to page size 50, sorted by {@code id} DESC.
     * @param name     optional filter to match (part of) the user's first name; may be {@code null}
     * @param surname  optional filter to match (part of) the user's last name; may be {@code null}
     * @return a {@link Page} of users matching the criteria
     */
    @GetMapping(Endpoint.GET_USERS)
    public Page<User> findAllUsers(
            @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname
    ) {
        return userService.searchUsers(name, surname, pageable);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @return {@code 200 OK} with a success {@link ApiResponse} if deletion succeeds;
     * {@code 404 NOT FOUND} with an error {@link ApiResponse} if the user does not exist
     * or the input is invalid.
     */
    @DeleteMapping(Endpoint.DELETE_USER)
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        try {
            this.userService.deleteUser(id);
            return ResponseEntity.status(200).body(
                    ApiResponse.successResponse("Utente  cancellato con successo")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.errorResponse("Errore nella cancellazione dell'utente", e.getMessage()));
        }
    }

    /**
     * Imports users from a CSV file.
     *
     * @param file the uploaded CSV file (multipart/form-data) containing user records
     * @return {@code 200 OK} with an import report map (e.g., counts, errors);
     * {@code 400 BAD REQUEST} with an error {@link ApiResponse} if the CSV is invalid;
     * {@code 500 INTERNAL SERVER ERROR} with an error {@link ApiResponse} for unexpected failures.
     */
    @PostMapping(path = Endpoint.UPLOAD_USER_CSV, consumes = "multipart/form-data")
    public ResponseEntity<?> uploadUsersCsv(@RequestPart("file") MultipartFile file) {
        try {
            Map<String, Object> report = userService.importUsersFromCsv(file);
            return ResponseEntity.ok(report);
        } catch (CsvImportException e) {
            return ResponseEntity.status(400).body(ApiResponse.errorResponse("CSV non valido", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.errorResponse("Errore interno ", e.getMessage()));
        }
    }
}
