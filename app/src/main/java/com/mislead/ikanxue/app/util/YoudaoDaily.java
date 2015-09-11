package com.mislead.ikanxue.app.util;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mislead.ikanxue.app.model.YoudaoDailyObject;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * YoudaoDaily
 * AUTHOR:Zhaoyy  2015/6/12
 * DESC:
 **/
public class YoudaoDaily {

  private static String TAG = "YoudaoDaily";
  private static final String DAILY_URL = "http://xue.youdao.com/w?page=%1$s&type=%2$s";
  private static final Pattern pattern = Pattern.compile("var loaded_data = \\{(.*?)\\};");
  private static final String TYPE[] = new String[] {
      "all", "word", "test", "sentence"
  };

  private static Gson gson = new GsonBuilder().create();

  public void getTodayDaily() {
    //try {
    VolleyHelper.requestStringGet(String.format(DAILY_URL, 1, TYPE[3]),
        new VolleyHelper.ResponseListener<String>() {
          @Override public void onErrorResponse(VolleyError volleyError) {

          }

          @Override public void onResponse(String s) {
            s = s.replaceAll("\\s\\s+", ""); // to one line
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
              String json = matcher.group(1).substring(13);
              YoudaoDailyObject dailyObject = gson.fromJson(json, YoudaoDailyObject.class);
              ShPreUtil.setString(DailyEnglishUtil.SH_LAST_DAILY_EN, dailyObject.getSen());
              ShPreUtil.setString(DailyEnglishUtil.SH_LAST_DAILY_ZH, dailyObject.getTrans());
              ShPreUtil.setString(DailyEnglishUtil.SH_LAST_DAILY_PIC_URL, dailyObject.getImage());
              ShPreUtil.setString(DailyEnglishUtil.SH_LAST_DAILY_DATE, dailyObject.getDate());
              // cache the image
              VolleyHelper.requestAndCacheImage(dailyObject.getImage(),
                  AndroidHelper.getSplashImageCache());
            }
          }
        });
  }
}
