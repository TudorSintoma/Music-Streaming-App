package com.tudor.demo.service;

import com.tudor.demo.dto.ListenerDTO;
import com.tudor.demo.exception.ResourceNotFoundException;
import com.tudor.demo.model.Listener;
import com.tudor.demo.model.User;
import com.tudor.demo.repository.ListenerRepository;
import com.tudor.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ListenerService {
    private final ListenerRepository listenerRepository;
    private final UserRepository userRepository;

    public List<Listener> getListeners() {
        return listenerRepository.findAll();
    }

    public Listener addListener(ListenerDTO listenerDTO) {
        return userRepository.findById(listenerDTO.getUser().getUserId())
                .map(user -> {
                    Listener listener = new Listener();
                    listener.setUser(user);
                    return listenerRepository.save(listener);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Listener updateListener(Integer id, Listener listener) {
        return listenerRepository.findById(id)
                .map(existing -> {
                    existing.setUser(listener.getUser());
                    return listenerRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Listener with id " + id + " not found"));
    }

    public void deleteListener(Integer id) {
        if (!listenerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Listener with id " + id + " not found");
        }
        listenerRepository.deleteById(id);
    }

    public Listener getListenerByUser(User user) {
        return listenerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Listener of user " + user.getFullName() + " not found"));
    }

    public Listener getListenerById(Integer id) {
        return listenerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listener with id " + id + " not found"));
    }
}