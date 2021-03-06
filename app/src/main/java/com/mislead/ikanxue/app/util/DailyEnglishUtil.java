package com.mislead.ikanxue.app.util;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mislead.ikanxue.app.model.DailyEnglishObject;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.util.Date;

/**
 * DailyEnglishUtil
 * AUTHOR:Zhaoyy  2015/6/27
 * DESC:
 **/
public class DailyEnglishUtil {

  private static String TAG = "DailyEnglishUtil";

  private static final String DAILY_URL = "http://open.iciba.com/dsapi";

  public static final String SH_LAST_DAILY_EN = "last_daily_en";
  public static final String SH_LAST_DAILY_ZH = "last_daily_zh";
  public static final String SH_LAST_DAILY_PIC_URL = "last_daily_pic_url";
  public static final String SH_LAST_DAILY_DATE = "last_daily_date";

  private VolleyHelper.ResponseListener<String> listener = null;

  public DailyEnglishUtil prepareListener() {
    listener = new VolleyHelper.ResponseListener<String>() {
      @Override public void onErrorResponse(VolleyError volleyError) {
        LogHelper.e(volleyError.getMessage());
      }

      @Override public void onResponse(String s) {
        Gson gson = new Gson();
        DailyEnglishObject object = gson.fromJson(s, DailyEnglishObject.class);
        // if can not get iciba daily, try to get youdao daily
        if (object != null) {

          ShPreUtil.setString(SH_LAST_DAILY_EN, object.getContent());
          ShPreUtil.setString(SH_LAST_DAILY_ZH, object.getNote());
          ShPreUtil.setString(SH_LAST_DAILY_PIC_URL, object.getPicture());
          ShPreUtil.setString(SH_LAST_DAILY_DATE, object.getDateline());
          // cache the image
          VolleyHelper.requestAndCacheImage(object.getPicture(),
              AndroidHelper.getSplashImageCache());
        } else {
          new DailyEnglish().getTodayDaily();
        }
      }
    };

    return this;
  }

  public DailyEnglishObject GetDailyEnglish() {

    String today = DateHelper.formateDateString(new Date());
    // just request, if haven't request today
    if (!today.equals(ShPreUtil.getString(SH_LAST_DAILY_DATE))) {
      getDailyEnglishFromNet();
    }
    if (ShPreUtil.getString(SH_LAST_DAILY_EN).isEmpty()) {
      return null;
    } else {
      DailyEnglishObject dailyEnglishObject = new DailyEnglishObject();
      dailyEnglishObject.setContent(ShPreUtil.getString(SH_LAST_DAILY_EN));
      dailyEnglishObject.setNote(ShPreUtil.getString(SH_LAST_DAILY_ZH));
      dailyEnglishObject.setPicture(ShPreUtil.getString(SH_LAST_DAILY_PIC_URL));

      return dailyEnglishObject;
    }
  }

  private void getDailyEnglishFromNet() {
    prepareListener();
    VolleyHelper.requestStringGet(DAILY_URL, listener);
  }
}
