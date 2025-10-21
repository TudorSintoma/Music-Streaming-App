package com.tudor.demo.controller;

import com.tudor.demo.dto.DiscoveryFilterDTO;
import com.tudor.demo.dto.UserDisplayDTO;
import com.tudor.demo.model.Genre;
import com.tudor.demo.model.User;
import com.tudor.demo.service.UserService;
import com.tudor.demo.service.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/discovery")
@CrossOrigin(origins = "http://localhost:3000")
public class DiscoveryController {

    private final DiscoveryService discoveryService;
    private final UserService userService;

    @Autowired
    public DiscoveryController(DiscoveryService discoveryService, UserService userService) {
        this.discoveryService = discoveryService;
        this.userService = userService;
    }

    /**
     * Search for artists by name (delegated to ArtistService).
     */
    @GetMapping("/search")
    public ResponseEntity<UserDisplayDTO> searchArtistByName(@RequestParam String name) {
        Optional<User> userOpt = userService.getProfile(name);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        UserDisplayDTO dto = new UserDisplayDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setImagePath(user.getImagePath());

        return ResponseEntity.ok(dto);
    }

    /**
     * Filter artists based on genre, debut year period, popularity, and sort order.
     */
    @PostMapping("/filter")
    public List<UserDisplayDTO> filterArtists(@RequestBody DiscoveryFilterDTO filterDTO) {
        return discoveryService.filterArtists(filterDTO);
    }
}
