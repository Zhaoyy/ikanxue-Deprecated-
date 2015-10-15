package com.mislead.ikanxue.app.model;

import java.io.Serializable;

/**
 * YoudaoDailyObject
 * AUTHOR:Zhaoyy  2015/6/12
 * DESC:
 **/
public class YoudaoDailyObject implements Serializable {

  private static String TAG = "YoudaoDailyObject";

  private String date;
  private String image;
  private String rel;
  private String type;
  private String sen;
  private String trans;

  public void setDate(String date) {
    this.date = date;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setRel(String rel) {
    this.rel = rel;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setSen(String sen) {
    this.sen = sen;
  }

  public void setTrans(String trans) {
    this.trans = trans;
  }

  public String getDate() {
    return date;
  }

  public String getImage() {
    return image;
  }

  public String getRel() {
    return rel;
  }

  public String getType() {
    return type;
  }

  public String getSen() {
    return sen;
  }

  public String getTrans() {
    return trans;
  }
}
