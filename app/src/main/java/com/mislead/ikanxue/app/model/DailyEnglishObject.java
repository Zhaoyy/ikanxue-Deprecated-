package com.mislead.ikanxue.app.model;

import java.util.List;

/**
 * DailyEnglish
 * AUTHOR:Zhaoyy  2015/6/27
 * DESC:
 **/
public class DailyEnglishObject {

  private static String TAG = "DailyEnglish";

  private String love;
  private String sp_pv;
  private String note;
  private String dateline;
  private String s_pv;
  private String caption;
  private String content;
  private String picture;
  private String sid;
  private String picture2;
  private List<TagsEntity> tags;
  private String tts;
  private String fenxiang_img;
  private String translation;

  public void setLove(String love) {
    this.love = love;
  }

  public void setSp_pv(String sp_pv) {
    this.sp_pv = sp_pv;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public void setDateline(String dateline) {
    this.dateline = dateline;
  }

  public void setS_pv(String s_pv) {
    this.s_pv = s_pv;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public void setSid(String sid) {
    this.sid = sid;
  }

  public void setPicture2(String picture2) {
    this.picture2 = picture2;
  }

  public void setTags(List<TagsEntity> tags) {
    this.tags = tags;
  }

  public void setTts(String tts) {
    this.tts = tts;
  }

  public void setFenxiang_img(String fenxiang_img) {
    this.fenxiang_img = fenxiang_img;
  }

  public void setTranslation(String translation) {
    this.translation = translation;
  }

  public String getLove() {
    return love;
  }

  public String getSp_pv() {
    return sp_pv;
  }

  public String getNote() {
    return note;
  }

  public String getDateline() {
    return dateline;
  }

  public String getS_pv() {
    return s_pv;
  }

  public String getCaption() {
    return caption;
  }

  public String getContent() {
    return content;
  }

  public String getPicture() {
    return picture;
  }

  public String getSid() {
    return sid;
  }

  public String getPicture2() {
    return picture2;
  }

  public List<TagsEntity> getTags() {
    return tags;
  }

  public String getTts() {
    return tts;
  }

  public String getFenxiang_img() {
    return fenxiang_img;
  }

  public String getTranslation() {
    return translation;
  }

  public class TagsEntity {
    /**
     * name : ������
     * id : 10
     */
    private String name;
    private String id;

    public void setName(String name) {
      this.name = name;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public String getId() {
      return id;
    }
  }
}
