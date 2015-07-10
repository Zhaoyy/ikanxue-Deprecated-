package com.mislead.ikanxue.app.model;

/**
 * ForumTitleObject
 *
 * @author Mislead
 *         DATE: 2015/7/10
 *         DESC:
 **/
public class ForumTitleObject {

  private static String TAG = "ForumTitleObject";
  private String name = "";  // title
  private int id = 0;        // id
  private int imageId = 0;   // imageid
  private int type = 0;      // type:0-group, 1-child

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getImageId() {
    return imageId;
  }

  public void setImageId(int imageId) {
    this.imageId = imageId;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}
