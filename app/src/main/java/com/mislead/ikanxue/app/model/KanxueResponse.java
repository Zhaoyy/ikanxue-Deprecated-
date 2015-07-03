package com.mislead.ikanxue.app.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.cookie.Cookie;

/**
 * KanxueResponse
 *
 * @author Mislead
 *         DATE: 2015/7/3
 *         DESC:
 **/
public class KanxueResponse {

  private static String TAG = "KanxueResponse";

  private int status = 0;

  private String jsonString = "";

  private List<Cookie> cookies = new ArrayList<>();

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getJsonString() {
    return jsonString;
  }

  public void setJsonString(String jsonString) {
    this.jsonString = jsonString;
  }

  public List<Cookie> getCookies() {
    return cookies;
  }

  public void setCookies(List<Cookie> cookies) {
    this.cookies = cookies;
  }
}
