package com.tudor.demo.service;

import com.tudor.demo.dto.ArtistDTO;
import com.tudor.demo.exception.ResourceNotFoundException;
import com.tudor.demo.model.Artist;
import com.tudor.demo.model.User;
import com.tudor.demo.repository.ArtistRepository;
import com.tudor.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;

    public List<Artist> getArtists() {
        return artistRepository.findAll();
    }

    public Artist addArtist(ArtistDTO artistDTO) {
        return Optional.of(artistDTO)
                .map(dto -> {
                    Artist artist = new Artist();
                    artist.setUser(dto.getUser());
                    return artist;
                })
                .map(artistRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Artist DTO"));
    }

    public Artist updateArtist(Integer id, Artist artist) {
        return artistRepository.findById(id)
                .map(existingArtist -> {
                    existingArtist.setUser(artist.getUser());
                    existingArtist.setGenre(artist.getGenre());
                    return artistRepository.save(existingArtist);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Artist with id " + id + " not found"));
    }

    public void deleteArtist(Integer id) {
        if (!artistRepository.existsById(id))
            throw new ResourceNotFoundException("Artist with id " + id + " not found");
        artistRepository.deleteById(id);
    }

    public Artist getArtistByUser(User user) {
        return artistRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Artist of user " + user.getFullName() + " not found"));
    }

    public Artist getArtistById(Integer id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist with id " + id + " not found"));
    }

    @Transactional
    public String updateGenre(String email, int genreId) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    artistRepository.findByUser(user)
                            .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
                    artistRepository.updateGenre(user.getUserId(), genreId);
                    return "Genre updated successfully";
                })
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<ArtistDTO> getArtistsByGenre(Integer genreId) {
        return artistRepository.findByGenreId(genreId).stream()
                .map(ArtistDTO::fromEntity)
                .collect(Collectors.toList());
    }
}