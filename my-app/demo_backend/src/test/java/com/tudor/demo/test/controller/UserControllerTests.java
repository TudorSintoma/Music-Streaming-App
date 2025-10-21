package com.tudor.demo.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tudor.demo.controller.UserController;
import com.tudor.demo.dto.UserCreateDTO;
import com.tudor.demo.model.User;
import com.tudor.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserCreateDTO userCreateDTO;

    @BeforeEach
    public void setUp() {
        testUser = new User("John Doe", "john.doe@example.com", "password123!");
        userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFullName("John Doe");
        userCreateDTO.setEmail("john.doe@example.com");
        userCreateDTO.setPassword("password123!");
    }

    @Test
    public void testGetUsers() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$[0].fullName").value(testUser.getFullName()))
                .andExpect(jsonPath("$[0].email").value(testUser.getEmail()));
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(1)).thenReturn(testUser);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getUserId()))
                .andExpect(jsonPath("$.fullName").value(testUser.getFullName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(testUser);

        mockMvc.perform(get("/users/email/{email}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getUserId()))
                .andExpect(jsonPath("$.fullName").value(testUser.getFullName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    public void testCreateUser() throws Exception {
        when(userService.addUser(any(UserCreateDTO.class))).thenReturn(testUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.fullName").value(testUser.getFullName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    public void testUpdateUser() throws Exception {
        when(userService.updateUser(eq(1), any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.fullName").value(testUser.getFullName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    public void testDeleteUser() throws Exception {

        doNothing().when(userService).deleteUser(1);


        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(1);
    }

    @Test
    public void testGetUserByFullName() throws Exception {

        when(userService.getProfile("John Doe")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/fullName/{fullName}", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.fullName").value(testUser.getFullName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    public void testAddAccountType() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", "john.doe@example.com");
        requestBody.put("accountType", "premium");

        when(userService.addAccountType("john.doe@example.com", "premium")).thenReturn("Account type updated");

        mockMvc.perform(post("/add-account-type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account type updated"));
    }

    @Test
    public void testLogin() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "john.doe@example.com");
        loginRequest.put("password", "password");

        when(userService.login("john.doe@example.com", "password")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()))
                .andExpect(jsonPath("$.fullName").value(testUser.getFullName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }
}