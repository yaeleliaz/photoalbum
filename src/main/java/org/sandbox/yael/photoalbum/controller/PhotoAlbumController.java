package org.sandbox.yael.photoalbum.controller;

import org.sandbox.yael.photoalbum.model.Photo;
import org.sandbox.yael.photoalbum.PhotoJpaRepository;
import org.sandbox.yael.photoalbum.service.PhotoAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
/**
 * Main application controller.
 *
 * @author Yael
 */
public class PhotoAlbumController {
  @Autowired
  private PhotoJpaRepository photoRepository;

  @Autowired
  private PhotoAlbumService photoAlbumService;

  /**
   * @return All the photos or by album, if an album id is specified,
   */
  @GetMapping("/photos")
  public ResponseEntity<List<Photo>> getAllPhotos(@RequestParam(required = false) String albumId) {
    try {
      List<Photo> photos = new ArrayList<>();

      if (albumId == null) {
        // All photos
        photoRepository.findAll().forEach(photos::add);
      }
      else {
        // List of the photos for the given album
        List<Photo> photoListByAlbumId = photoRepository.findByAlbumId(Integer.parseInt(albumId));
        if (photoListByAlbumId != null && !photoListByAlbumId.isEmpty()) {
          photoListByAlbumId.forEach(photos::add);
        }
        else {
          return new ResponseEntity<>(HttpStatus.OK);
        }

        if (photos.isEmpty()) {
          return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
      }
      return new ResponseEntity<>(photos, HttpStatus.OK); // <<<
    }
    catch (Exception e) {
      return new ResponseEntity<>((List<Photo>) null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * @return The object representation of the given photo.
   */
  @GetMapping("/photos/{id}")
  public ResponseEntity<Photo> getPhotoById(@PathVariable("id") long id) {
    Optional<Photo> photoData = photoRepository.findById(id);

    if (photoData.isPresent()) {
      return new ResponseEntity<>(photoData.get(), HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * This will download and parse all the photos, saving them to a local folder.
   */
  @GetMapping("/download")
  public ResponseEntity<HttpStatus> downloadPhotos() {
    try {
      photoAlbumService.getAndSavePhotos();
      return new ResponseEntity<>(HttpStatus.OK);
    }
    catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * This API will return the actual photo binary to be displayed.
   */
  @GetMapping(value = "/photo/show/{id}")
  public ResponseEntity<byte[]> showPhoto(@PathVariable("id") int id) throws IOException {
    File img = photoAlbumService.getPhotoFile(id);
    if (img == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok().
            contentType(MediaType.valueOf(MediaType.IMAGE_JPEG_VALUE)).
            body(Files.readAllBytes(img.toPath()));
  }
}
