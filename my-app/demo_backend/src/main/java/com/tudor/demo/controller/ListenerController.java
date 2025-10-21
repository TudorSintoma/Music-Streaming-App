package com.tudor.demo.controller;

import com.tudor.demo.dto.ListenerDTO;
import com.tudor.demo.model.Listener;
import com.tudor.demo.model.User;
import com.tudor.demo.service.ListenerService;
import com.tudor.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/listeners")
public class ListenerController {
    private final ListenerService listenerService;
    private final UserService userService;

    @GetMapping
    public List<Listener> getAllListeners() {
        return listenerService.getListeners();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Listener> getListenerById(@PathVariable Integer id) {
        Listener listener = listenerService.getListenerById(id);
        return ResponseEntity.ok(listener);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Listener> getListenerByUser(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        Listener listener = listenerService.getListenerByUser(user);
        return ResponseEntity.ok(listener);
    }

    @PostMapping
    public ResponseEntity<Listener> createListener(@Valid @RequestBody ListenerDTO listenerDTO) {
        Listener createdListener = listenerService.addListener(listenerDTO);
        return ResponseEntity.ok(createdListener);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Listener> updateListener(@PathVariable Integer id, @Valid @RequestBody Listener listener) {
        Listener updatedListener = listenerService.updateListener(id, listener);
        return ResponseEntity.ok(updatedListener);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListener(@PathVariable Integer id) {
        listenerService.deleteListener(id);
        return ResponseEntity.noContent().build();
    }
}
