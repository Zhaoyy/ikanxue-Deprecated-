package com.mislead.ikanxue.app.model;

/**
 * ForumThreadObject
 *
 * @author Mislead
 *         DATE: 2015/7/11
 *         DESC:
 **/
public class ForumThreadObject {

  private static String TAG = "ForumThreadObject";

  private String threadtitle = "";    // title
  private int threadid = 0;           // title id
  private String postusername = "";   // post user name
  private int postuserid = 0;         // user id
  private int avatar;                 // user have head pic or not
  private long avatardateline;
  private String lastpostdate = "";   // last reply date
  private int views;                  // view count
  private int replaycount;            // replay count
  private int globalsticky;           // is global sticky (top hot)
  private int sticky;                 // is sticky
  private int goodness;
  private int open;

  public String getThreadtitle() {
    return threadtitle;
  }

  public void setThreadtitle(String threadtitle) {
    this.threadtitle = threadtitle;
  }

  public int getThreadid() {
    return threadid;
  }

  public void setThreadid(int threadid) {
    this.threadid = threadid;
  }

  public String getPostusername() {
    return postusername;
  }

  public void setPostusername(String postusername) {
    this.postusername = postusername;
  }

  public int getPostuserid() {
    return postuserid;
  }

  public void setPostuserid(int postuserid) {
    this.postuserid = postuserid;
  }

  public int getAvatar() {
    return avatar;
  }

  public void setAvatar(int avatar) {
    this.avatar = avatar;
  }

  public long getAvatardateline() {
    return avatardateline;
  }

  public void setAvatardateline(long avatardateline) {
    this.avatardateline = avatardateline;
  }

  public String getLastpostdate() {
    return lastpostdate;
  }

  public void setLastpostdate(String lastpostdate) {
    this.lastpostdate = lastpostdate;
  }

  public int getViews() {
    return views;
  }

  public void setViews(int views) {
    this.views = views;
  }

  public int getReplaycount() {
    return replaycount;
  }

  public void setReplaycount(int replaycount) {
    this.replaycount = replaycount;
  }

  public int getGlobalsticky() {
    return globalsticky;
  }

  public void setGlobalsticky(int globalsticky) {
    this.globalsticky = globalsticky;
  }

  public int getSticky() {
    return sticky;
  }

  public void setSticky(int sticky) {
    this.sticky = sticky;
  }

  public int getGoodness() {
    return goodness;
  }

  public void setGoodness(int goodness) {
    this.goodness = goodness;
  }

  public int getOpen() {
    return open;
  }

  public void setOpen(int open) {
    this.open = open;
  }
}
