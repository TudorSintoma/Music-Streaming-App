package com.tudor.demo.controller;

import com.tudor.demo.model.Artist;
import com.tudor.demo.model.ArtistInfluence;
import com.tudor.demo.dto.ArtistInfluencesDTO;
import com.tudor.demo.service.ArtistInfluencesService;
import com.tudor.demo.service.ArtistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/artist-influences")
public class ArtistInfluencesController {

    private final ArtistInfluencesService artistInfluencesService;
    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<List<ArtistInfluence>> getAllArtistInfluences() {
        return ResponseEntity.ok(artistInfluencesService.getAllInfluences());
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<ArtistInfluence>> getInfluencesByArtist(@PathVariable Integer artistId) {
        Artist artist = artistService.getArtistById(artistId);
        List<ArtistInfluence> influences = artistInfluencesService.getInfluencesByArtist(artist);
        return ResponseEntity.ok(influences);
    }

    @GetMapping("/influenced-by/{influencedById}")
    public ResponseEntity<List<ArtistInfluence>> getInfluencesByInfluencedBy(@PathVariable Integer influencedById) {
        Artist influencedBy = artistService.getArtistById(influencedById);
        List<ArtistInfluence> influences = artistInfluencesService.getInfluencesByInfluencedBy(influencedBy);
        return ResponseEntity.ok(influences);
    }

    @PostMapping
    public ResponseEntity<ArtistInfluence> createArtistInfluence(@RequestBody ArtistInfluencesDTO dto) {
        Artist artist = artistService.getArtistById(dto.getArtistId());
        Artist influencedBy = artistService.getArtistById(dto.getInfluencedById());

        ArtistInfluence artistInfluences = new ArtistInfluence();
        artistInfluences.setArtist(artist);
        artistInfluences.setInfluencedBy(influencedBy);

        ArtistInfluence newArtistInfluence = artistInfluencesService.addInfluence(dto);

        return ResponseEntity.ok(newArtistInfluence);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistInfluence> updateArtistInfluence(
            @PathVariable Integer id,
            @RequestBody ArtistInfluencesDTO dto) {

        ArtistInfluence updatedArtistInfluence = artistInfluencesService.updateInfluence(id, dto);

        return ResponseEntity.ok(updatedArtistInfluence);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtistInfluence(@PathVariable Integer id) {
        artistInfluencesService.deleteInfluence(id);
        return ResponseEntity.noContent().build();
    }
}