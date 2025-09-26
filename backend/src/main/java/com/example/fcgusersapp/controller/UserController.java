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
import java.util.Optional;

@CrossOrigin(origins = Endpoint.CORS_URL_FE)
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

    @GetMapping(Endpoint.FIND_OR_UPDATE_USER)
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @GetMapping(Endpoint.GET_USERS)
    public Page<User> findAllUsers(
            @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname
    ) {
        return userService.searchUsers(name, surname, pageable);
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
