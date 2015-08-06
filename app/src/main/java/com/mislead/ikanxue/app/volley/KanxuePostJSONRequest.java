package com.mislead.ikanxue.app.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.mislead.ikanxue.app.model.KanxueResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * KanxuePostJSONRequest
 *
 * @author Mislead
 *         DATE: 2015/7/3
 *         DESC:
 **/
public class KanxuePostJSONRequest extends Request<KanxueResponse> {

  private static String TAG = "KanxuePostJSONRequest";

  private Map<String, String> params;

  private Map<String, String> headers = new HashMap<>();

  private String cookieFromResponse;

  //使用正则表达式从reponse的头中提取cookie内容的子串
  private static final Pattern pattern = Pattern.compile("Set-Cookie\\d*");

  private VolleyHelper.ResponseListener<KanxueResponse> listener;

  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  public KanxuePostJSONRequest(String url, VolleyHelper.ResponseListener<KanxueResponse> listener) {
    super(Method.POST, url, listener);
    this.listener = listener;
    headers.put("http.useragent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
  }

  @Override protected Map<String, String> getParams() throws AuthFailureError {
    if (params == null) {
      return super.getParams();
    } else {
      return params;
    }
  }

  @Override public Map<String, String> getHeaders() throws AuthFailureError {
    return headers;
  }

  @Override
  protected Response<KanxueResponse> parseNetworkResponse(NetworkResponse networkResponse) {
    try {
      KanxueResponse response = new KanxueResponse();
      String jsonString =
          new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));

      response.setJsonString(jsonString);
      // get all cookie
      List<Cookie> cookies = new ArrayList<>();
      for (String key : networkResponse.headers.keySet()) {
        Matcher matcher = pattern.matcher(key);
        if (matcher.find()) {
          cookieFromResponse = networkResponse.headers.get(key);
          cookieFromResponse = cookieFromResponse.substring(0, cookieFromResponse.indexOf(";"));
          String keyValue[] = cookieFromResponse.split("=", -1);
          Cookie cookie = new BasicClientCookie(keyValue[0], keyValue[1]);
          cookies.add(cookie);
        }
      }

      response.setCookies(cookies);

      return Response.success(response, HttpHeaderParser.parseCacheHeaders(networkResponse));
    } catch (UnsupportedEncodingException e) {
      return Response.error(new ParseError(e));
    }
  }

  @Override protected void deliverResponse(KanxueResponse object) {
    listener.onResponse(object);
  }
}
