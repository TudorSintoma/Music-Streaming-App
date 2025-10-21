package com.tudor.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tudor.demo.dto.AlbumDTO;
import com.tudor.demo.model.Album;
import com.tudor.demo.model.Song;
import com.tudor.demo.model.User;
import com.tudor.demo.repository.AlbumRepository;
import com.tudor.demo.repository.SongRepository;
import com.tudor.demo.repository.UserRepository;
import com.tudor.demo.service.AlbumService;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private AlbumService albumService;

    private SongRepository songRepository;

    private AlbumRepository albumRepository;

    private UserRepository userRepository;

    @GetMapping
    public List<Album> getAll() {
        return albumService.getAllAlbums();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getById(@PathVariable Integer id) {
        return albumService.getAlbumById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Album create(@RequestBody AlbumDTO dto) {
        return albumService.createAlbum(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Album> update(@PathVariable Integer id, @RequestBody AlbumDTO dto) {
        try {
            return ResponseEntity.ok(albumService.updateAlbum(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Album>> getAlbumsByUserId(@PathVariable int userId) {
        List<Album> albums = albumService.getAlbumsByUserId(userId);
        return ResponseEntity.ok(albums);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }

    private String saveImageToDisk(MultipartFile image) throws IOException {
        String uploadDir = "/images/album/";
        String filename = image.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, filename);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, image.getBytes());
        return uploadDir + filename;
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAlbum(
            @RequestParam("name") String name,
            @RequestParam("userId") Integer userId,
            @RequestParam("releaseYear") Integer releaseYear,
            @RequestParam("image") MultipartFile image,
            @RequestParam("songs") String songsJson
    ) {
        try {
            // 1. Save image to disk
            String imagePath = saveImageToDisk(image);

            // 2. Fetch user entity
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User not found with ID: " + userId);
            }
            User user = userOptional.get();

            // 3. Create and save Album
            Album album = new Album();
            album.setName(name);
            album.setUser(user); // Set full User object
            album.setReleaseYear(releaseYear);
            album.setImagePath(imagePath);

            Album savedAlbum = albumRepository.save(album);

            // 4. Parse songs JSON and save each Song with album reference
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> songs = mapper.readValue(songsJson, new TypeReference<>() {});
            for (Map<String, String> songData : songs) {
                Song song = new Song();
                song.setName(songData.get("name"));
                song.setAlbum(savedAlbum);
                song.setLikes(0);

                songRepository.save(song);
            }

            return ResponseEntity.ok("Album and songs uploaded successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading album: " + e.getMessage());
        }
    }




    @PostMapping("/upload-cover")
    public ResponseEntity<String> uploadCover(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = "/images/album/";
            File directory = new File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            Files.write(filePath, file.getBytes());

            return ResponseEntity.ok(filePath.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }
}
