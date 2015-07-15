package com.mislead.ikanxue.app.model;

import android.text.Spanned;
import java.util.List;

/**
 * ForumThreadObject
 *
 * @author Mislead
 *         DATE: 2015/7/11
 *         DESC:
 **/
public class ForumThreadObject {

  private static String TAG = "ForumThreadObject";
  private List<PostbitsEntity> postbits;

  private int time;
  private int pagenav;

  public void setPostbits(List<PostbitsEntity> postbits) {
    this.postbits = postbits;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public void setPagenav(int pagenav) {
    this.pagenav = pagenav;
  }

  public List<PostbitsEntity> getPostbits() {
    return postbits;
  }

  public int getTime() {
    return time;
  }

  public int getPagenav() {
    return pagenav;
  }

  public static class PostbitsEntity {
    private int postid;
    private int thumbnail;  //内容摘要
    private String username;
    private int userid;
    private int avatar;
    private long avatardateline;
    private String posttime;
    private String postdate;

    private String message;
    private String title;

    private Spanned thumbnailSpanned;//缩略信息

    private List<ThumbnailattachmentsEntity> thumbnailattachments;
    private List<ThumbnailattachmentsEntity> otherattachments;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public int getPostid() {
      return postid;
    }

    public void setPostid(int postid) {
      this.postid = postid;
    }

    public int getThumbnail() {
      return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
      this.thumbnail = thumbnail;
    }

    public int getUserid() {
      return userid;
    }

    public void setUserid(int userid) {
      this.userid = userid;
    }

    public int getAvatar() {
      return avatar;
    }

    public void setAvatar(int avatar) {
      this.avatar = avatar;
    }

    public Spanned getThumbnailSpanned() {
      return thumbnailSpanned;
    }

    public void setThumbnailSpanned(Spanned thumbnailSpanned) {
      this.thumbnailSpanned = thumbnailSpanned;
    }

    public List<ThumbnailattachmentsEntity> getThumbnailattachments() {
      return thumbnailattachments;
    }

    public void setThumbnailattachments(List<ThumbnailattachmentsEntity> thumbnailattachments) {
      this.thumbnailattachments = thumbnailattachments;
    }

    public List<ThumbnailattachmentsEntity> getOtherattachments() {
      return otherattachments;
    }

    public void setOtherattachments(List<ThumbnailattachmentsEntity> otherattachments) {
      this.otherattachments = otherattachments;
    }

    public long getAvatardateline() {
      return avatardateline;
    }

    public void setAvatardateline(long avatardateline) {
      this.avatardateline = avatardateline;
    }

    public String getPosttime() {
      return posttime;
    }

    public void setPosttime(String posttime) {
      this.posttime = posttime;
    }

    public String getPostdate() {
      return postdate;
    }

    public void setPostdate(String postdate) {
      this.postdate = postdate;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }
  }

  public class ThumbnailattachmentsEntity {
    private int attachmentid;
    private String filename;
    private int filesize;

    public int getAttachmentid() {
      return attachmentid;
    }

    public void setAttachmentid(int attachmentid) {
      this.attachmentid = attachmentid;
    }

    public String getFilename() {
      return filename;
    }

    public void setFilename(String filename) {
      this.filename = filename;
    }

    public int getFilesize() {
      return filesize;
    }

    public void setFilesize(int filesize) {
      this.filesize = filesize;
    }
  }
}
