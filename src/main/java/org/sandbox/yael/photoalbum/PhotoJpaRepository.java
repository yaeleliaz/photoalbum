package org.sandbox.yael.photoalbum;

import org.sandbox.yael.photoalbum.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Yael
 */
public interface PhotoJpaRepository extends JpaRepository<Photo, Long> {

  //List<Photo> findById(int id);
  List<Photo> findByAlbumId(int albumId);
  public List<Photo> findAll();

  @Query(value = "SELECT local_path FROM photos p WHERE p.id= :id", nativeQuery = true)
  String findLocalPathByPhotoId(@Param("id") Long id);

}
