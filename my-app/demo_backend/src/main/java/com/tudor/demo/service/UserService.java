package com.tudor.demo.service;

import com.tudor.demo.dto.ArtistDTO;
import com.tudor.demo.dto.ListenerDTO;
import com.tudor.demo.dto.UserDisplayDTO;
import com.tudor.demo.dto.UserCreateDTO;
import com.tudor.demo.exception.DuplicateResourceException;
import com.tudor.demo.exception.ResourceNotFoundException;
import com.tudor.demo.model.User;
import com.tudor.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private ArtistService artistService;

    private ListenerService listenerService;

    private PasswordEncoder passwordEncoder;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createAccount(String fullName, String email, String password) {
        if (fullName == null || email == null || password == null) {
            throw new IllegalArgumentException("All fields are required");
        }
        return Optional.of(new User())
                .map(user -> {
                    user.setFullName(fullName);
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode(password));
                    return user;
                })
                .map(userRepository::save)
                .orElseThrow();
    }

    @Transactional
    public String addAccountType(String email, String accountType) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    if ("Artist".equalsIgnoreCase(accountType)) {
                        user.setRole("artist");
                    } else if ("Listener".equalsIgnoreCase(accountType)) {
                        user.setRole("listener");
                    } else {
                        throw new IllegalArgumentException("Invalid account type");
                    }

                    // Save user with updated role first
                    User savedUser = userRepository.save(user);

                    // Now proceed with creating Artist or Listener
                    if ("artist".equals(savedUser.getRole())) {
                        ArtistDTO artistDTO = new ArtistDTO();
                        artistDTO.setUser(savedUser);
                        artistService.addArtist(artistDTO);
                    } else if ("listener".equals(savedUser.getRole())) {
                        ListenerDTO listenerDTO = new ListenerDTO();
                        listenerDTO.setUser(savedUser);
                        listenerService.addListener(listenerDTO);
                    }

                    return accountType + " added successfully";
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> BCrypt.checkpw(password, user.getPassword()));
    }

    public Optional<User> getProfile(String name) {
        return userRepository.findByFullName(name);
    }

    public User addUser(UserCreateDTO userDTO) {
        return Optional.ofNullable(userDTO)
                .filter(dto -> userRepository.findByEmail(dto.getEmail()).isEmpty())
                .map(dto -> {
                    User user = new User();
                    user.setFullName(dto.getFullName());
                    user.setEmail(dto.getEmail());
                    user.setPassword(dto.getPassword());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new DuplicateResourceException("A user with email " + userDTO.getEmail() + " already exists."));
    }

    public User updateUser(Integer id, User user) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setFullName(user.getFullName());
                    existing.setEmail(user.getEmail());
                    existing.setPassword(user.getPassword());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    public List<UserDisplayDTO> getUserSummariesByIds(List<Integer> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(this::convertToUserDisplayDTO)
                .collect(Collectors.toList());
    }

    private UserDisplayDTO convertToUserDisplayDTO(User user) {
        return Optional.of(new UserDisplayDTO())
                .map(dto -> {
                    dto.setUserId(user.getUserId());
                    dto.setFullName(user.getFullName());
                    dto.setImagePath(user.getImagePath());
                    return dto;
                }).orElseThrow();
    }

    public Optional<String> getUserRoleById(Integer userId) {
        return userRepository.findById(userId)
                .map(User::getRole)
                .map(Optional::ofNullable)
                .orElse(Optional.empty());
    }
}
