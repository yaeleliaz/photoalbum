package org.sandbox.yael.photoalbum.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Yael
 */
@Entity
@Table(name = "photos")
public class Photo {

  @Id
  @Column(name = "id")
  private long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "url", nullable = false)
  private String url;

  @Column(name = "thumbnailUrl", nullable = false)
  private String thumbnailUrl;

  @Column(name = "localPath", nullable = false)
  private String localPath;

  @Column(name = "fileSize", nullable = false)
  private Long fileSize;

  @Column(name = "album_id")
  private int albumId;

  @Column(name = "download_date")
  private Date downloadDate;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  public String getLocalPath() {
    return localPath;
  }

  public void setLocalPath(String localPath) {
    this.localPath = localPath;
  }

  public Long getFileSize() {
    return fileSize;
  }

  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }

  public int getAlbumId() {
    return albumId;
  }

  public void setAlbumId(int albumId) {
    this.albumId = albumId;
  }

  public Date getDownloadDate() {
    return downloadDate;
  }

  public void setDownloadDate(Date downloadDate) {
    this.downloadDate = downloadDate;
  }
}
