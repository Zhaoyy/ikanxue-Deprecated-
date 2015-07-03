package com.mislead.ikanxue.app.util;

import android.net.Uri;
import java.util.Map;

/**
 * Urlhelper
 *
 * @author Mislead
 *         DATE: 2015/7/3
 *         DESC:
 **/
public class Urlhelper {

  private static String TAG = "Urlhelper";

  public static String encodeParamsInUrl(String url, Map<String, String> params) {

    if (params == null) return url;

    Uri.Builder builder = Uri.parse(url).buildUpon();
    for (String key : params.keySet()) {
      builder.appendQueryParameter(key, params.get(key));
    }
    return builder.toString();
  }
}
