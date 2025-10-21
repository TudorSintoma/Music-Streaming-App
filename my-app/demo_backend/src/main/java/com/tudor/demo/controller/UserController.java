package com.tudor.demo.controller;

import com.tudor.demo.dto.UserCreateDTO;
import com.tudor.demo.model.User;
import com.tudor.demo.repository.UserRepository;
import com.tudor.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tudor.demo.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@RestController
@AllArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/users/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/users/fullName/{fullName}")
    public ResponseEntity<?> getUserByFullName(@PathVariable String fullName) {
        try {
            Optional<User> userOptional = userService.getProfile(fullName);

            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "User not found with fullName: " + fullName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Internal server error occurred"));
        }
    }



    @PostMapping("/users")
    public User addUser(@Valid @RequestBody UserCreateDTO userDTO) {
        return userService.addUser(userDTO);
    }

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody UserCreateDTO userDTO) {
        try {
            User createdUser = userService.createAccount(userDTO.getFullName(), userDTO.getEmail(), userDTO.getPassword());

            // Optionally you can return a DTO with only needed fields
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("userId", createdUser.getUserId());
            responseBody.put("fullName", createdUser.getFullName());
            responseBody.put("email", createdUser.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);

        } catch (Exception e) {
            Map<String, String> errorBody = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
        }
    }


    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Integer id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @PostMapping("/add-account-type")
    public ResponseEntity<Map<String, String>> addAccountType(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String accountType = body.get("accountType");

            // Update user's role in DB
            String resultMessage = userService.addAccountType(email, accountType);

            // Retrieve user again after update
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty() || userOpt.get().getRole() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User role not set properly"));
            }

            String role = userOpt.get().getRole();

            // Create JWT token with updated role
            String token = jwtUtil.createToken(email, role);

            Map<String, String> response = new HashMap<>();
            response.put("message", resultMessage);
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public Optional<User> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Optional<User> userOpt = userService.login(email, password);

        if (userOpt.isPresent()) {
            return userOpt;
        } else {
            throw new RuntimeException("Invalid email or password");
        }
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> userOpt = userService.login(email, password);

        if (userOpt.isPresent()) {
            String token = jwtUtil.createToken(email, userOpt.get().getRole());
            Map<String, String> response = Map.of("token", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @GetMapping("/users/{userId}/role")
    public ResponseEntity<Map<String, String>> getUserRole(@PathVariable Integer userId) {
        Optional<String> role = userService.getUserRoleById(userId);
        if (role.isPresent()) {
            Map<String, String> response = Collections.singletonMap("role", role.get());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
