package org.sandbox.yael.photoalbum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sandbox.yael.photoalbum.model.Photo;
import org.sandbox.yael.photoalbum.PhotoJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

/**
 * @author Yael
 */
@Service
public class PhotoAlbumServiceImpl implements PhotoAlbumService {
  @Value("${photoalbum.photos.url}")
  private String photosUrl;

  @Value("${photoalbum.local.photo.folder}")
  private String localPhotosFolder;

  @Autowired
  private PhotoJpaRepository photoRepository;

  @Override
  @Transactional
  public void getAndSavePhotos() {
    System.out.println("Downloading photos from " + photosUrl + " into " + localPhotosFolder);
    try {
      // Get the file listing from the URL
      String jsonFileListing = fetchFromUrl(photosUrl);
      // Parse the JSON string
      List<Photo> photos = parseFileListing(jsonFileListing);

      // Download all photos to a local folder and persist their objects in the database
      for (Photo photo : photos) {
        dumpPhotoFromUrl(photo);
        photoRepository.save(photo);
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return The file object with the given photo id, or null if not found in the database or in the local folder.
   */
  @Override
  public File getPhotoFile(long id) {
    String fileLocalPath = photoRepository.findLocalPathByPhotoId(id);
    if (fileLocalPath == null) {
      return null;
    }
    File img = new File(fileLocalPath);
    if (img.exists() == false) {
      return null;
    }
    return img;
  }

  private String fetchFromUrl(String urlName) {
    StringBuilder sb = new StringBuilder();
    URLConnection urlConnection = null;
    InputStreamReader in = null;
    try {
      URL url = new URL(urlName);
      urlConnection = url.openConnection();
      if (urlConnection != null) {
        urlConnection.setReadTimeout(10 * 1000); // 10 seconds timeout
      }
      if (urlConnection != null && urlConnection.getInputStream() != null) {
        in = new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset());
        BufferedReader bufferedReader = new BufferedReader(in);
        if (bufferedReader != null) {
          int cp;
          while ((cp = bufferedReader.read()) != -1) {
            sb.append((char) cp);
          }
          bufferedReader.close();
        }
      }
      in.close();
    }
    catch (Exception e) {
      throw new RuntimeException("Exception while fetching from " + urlName, e);
    }
    return sb.toString(); // The content
  }

  private List<Photo> parseFileListing(String jsonFileListing) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<Photo> list = objectMapper.readValue(jsonFileListing,
        new TypeReference<List<Photo>>() { });
    return list;
  }

  /**
   * Download the photo from its URL and updates the state data fields (path, time and size).
   */
  private void dumpPhotoFromUrl(Photo photo) throws IOException {
    URL photoUrl = new URL(photo.getUrl());
    InputStream in = new BufferedInputStream(photoUrl.openStream());
    String photoFileName = photo.getUrl().substring(photo.getUrl().lastIndexOf('/'));
    File photoFile = new File(localPhotosFolder, photoFileName);
    OutputStream out = new BufferedOutputStream(new FileOutputStream(photoFile));
    for (int i; (i = in.read()) != -1; ) {
      out.write(i);
    }
    in.close();
    out.close();
    
    photo.setLocalPath(photoFile.getAbsolutePath());
    photo.setDownloadDate(new Date());
    photo.setFileSize(photoFile.length());
  }
}
