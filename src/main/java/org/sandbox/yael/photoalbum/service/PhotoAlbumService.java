package org.sandbox.yael.photoalbum.service;

import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * @author Yael
 */
public interface PhotoAlbumService {
  /**
   * Fetch the photo list, download all photos to a local folder and persist them.
   */
  @Transactional
  void getAndSavePhotos();

  /**
   * @return The file object corresponding to the photo file with the given id.
   */
  File getPhotoFile(long id);
}
