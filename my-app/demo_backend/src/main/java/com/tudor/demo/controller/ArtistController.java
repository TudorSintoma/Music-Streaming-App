package com.tudor.demo.controller;

import com.tudor.demo.dto.UserDisplayDTO;
import com.tudor.demo.model.Artist;
import com.tudor.demo.dto.ArtistDTO;
import com.tudor.demo.model.User;
import com.tudor.demo.service.ArtistService;
import com.tudor.demo.service.UserService;
import io.micrometer.common.KeyValues;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin
public class ArtistController {
    private final ArtistService artistService;
    private final UserService userService;

    @GetMapping("/artists")
    public List<Artist> getAllArtists() {
        return artistService.getArtists();
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable Integer id) {
        Artist artist = artistService.getArtistById(id);
        return ResponseEntity.ok(artist);
    }

    @GetMapping("/artists/user/{userId}")
    public ResponseEntity<Artist> getArtistByUser(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        Artist artist = artistService.getArtistByUser(user);
        return ResponseEntity.ok(artist);
    }

    @PostMapping("/artists")
    public ResponseEntity<Artist> createArtist(@Valid @RequestBody ArtistDTO artistDTO) {
        Artist createdArtist = artistService.addArtist(artistDTO);
        return ResponseEntity.ok(createdArtist);
    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable Integer id, @Valid @RequestBody Artist artist) {
        Artist updatedArtist = artistService.updateArtist(id, artist);
        return ResponseEntity.ok(updatedArtist);
    }

    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Integer id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/artists/genre/{genreId}")
    public ResponseEntity<List<UserDisplayDTO>> getArtistsByGenre(@PathVariable Integer genreId) {
        List<ArtistDTO> artists = artistService.getArtistsByGenre(genreId);

        List<Integer> userIds = artists.stream()
                .map(ArtistDTO::getUser)
                .filter(Objects::nonNull)
                .map(User::getUserId)
                .collect(Collectors.toList());

        List<UserDisplayDTO> userSummaries = userService.getUserSummariesByIds(userIds);

        userSummaries.forEach(userDisplayDTO -> {
            User user = userService.getUserById(userDisplayDTO.getUserId());
            if (user != null && user.getImagePath() != null) {
                String encodedImagePath = URLEncoder.encode(user.getImagePath(), StandardCharsets.UTF_8);
                userDisplayDTO.setImagePath(encodedImagePath);
            }
        });

        return ResponseEntity.ok(userSummaries);
    }


    @PostMapping("/update-genre")
    public ResponseEntity<String> updateGenre(@RequestBody Map<String, String> body) throws Exception {
        String email = body.get("email");
        Integer genreId = Integer.parseInt(body.get("genreId"));

        String result = artistService.updateGenre(email, genreId);
        return ResponseEntity.ok(result);
    }
}
