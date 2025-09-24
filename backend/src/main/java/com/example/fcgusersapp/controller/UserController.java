package com.example.fcgusersapp.controller;

import com.example.fcgusersapp.constants.Endpoint;
import com.example.fcgusersapp.entity.User;
import com.example.fcgusersapp.exceptions.CsvImportException;
import com.example.fcgusersapp.service.UserService;
import com.example.fcgusersapp.utils.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping(Endpoint.USERS_ENDPOINT_ROOT)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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

    @PutMapping(Endpoint.EDIT_USER)
    public ResponseEntity<?> editUser(@RequestBody User user, @PathVariable("id") Long id) {
        try {
            User updateUser = this.userService.editUser(user, id);
            return ResponseEntity.status(200).body(
                    ApiResponse.successResponse("Utente modificato con successo", updateUser)
            );
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(ApiResponse.errorResponse("Errore nella modifica dell'utente " + user.getName() + " " + user.getSurname(), e.getMessage()));
        }

    }

    @GetMapping(Endpoint.GET_USERS)
    public Page<User> findAllUsers(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return userService.findAllUsers(pageable);
    }

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
