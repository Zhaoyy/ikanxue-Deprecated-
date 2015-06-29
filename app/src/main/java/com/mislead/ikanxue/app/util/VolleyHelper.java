package com.mislead.ikanxue.app.util;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * VolleyHelper
 * AUTHOR:Zhaoyy  2015/6/29
 * DESC:
 **/
public class VolleyHelper {

  private static String TAG = "VolleyHelper";

  private static RequestQueue queue = null;

  public static void init(Context context) {
    queue = Volley.newRequestQueue(context);
  }

  public static void requestStringGet(String url, RespinseListener<String> listener) {
    StringRequest request = new StringRequest(Request.Method.GET, url, listener, listener);
    request.setShouldCache(true);
    request.setTag(url);
    queue.add(request);
  }

  public static interface RespinseListener<T> extends Response.Listener<T>, Response.ErrorListener {

  }
}
