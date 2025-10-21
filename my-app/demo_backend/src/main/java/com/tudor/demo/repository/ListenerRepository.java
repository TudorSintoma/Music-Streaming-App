package com.tudor.demo.repository;

import com.tudor.demo.model.Listener;
import com.tudor.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListenerRepository extends JpaRepository<Listener, Integer> {
    Optional<Listener> findByUser(User user);
}
