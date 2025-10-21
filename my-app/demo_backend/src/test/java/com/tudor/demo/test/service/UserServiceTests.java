package com.tudor.demo.test.service;

import com.tudor.demo.model.User;
import com.tudor.demo.dto.UserCreateDTO;
import com.tudor.demo.repository.UserRepository;
import com.tudor.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFullName("Test User");
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setPassword("password123!");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123!");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.addUser(userCreateDTO);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFindUserById() {
        User user = new User();
        user.setUserId(200);
        user.setEmail("tes1t@example.com");

        when(userRepository.findById(200)).thenReturn(Optional.of(user));

        User result = userService.getUserById(200);

        assertNotNull(result);
        assertEquals("test1@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(200);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setUserId(300);
        user.setEmail("test2@example.com");

        when(userRepository.findById(300)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(300, user);

        assertNotNull(result);
        assertEquals("test2@example.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser() {
        int userId = 300;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
