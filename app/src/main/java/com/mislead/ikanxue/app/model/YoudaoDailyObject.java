package com.mislead.ikanxue.app.model;

/**
 * YoudaoDailyObject
 * AUTHOR:Zhaoyy  2015/6/12
 * DESC:
 **/
public class YoudaoDailyObject {

  private static String TAG = "YoudaoDailyObject";

  /**
   * date : 2015-06-11
   * image : http://oimagec1.ydstatic.com/image?product=dict-treasury&id=271547039372573761&w=766&h=500
   * rel :
   * type : EXAMPLE
   * sen : Things work out the way they&#39;re meant to.
   * trans : 万物皆循其道-【此岸°守望草】译
   */
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
