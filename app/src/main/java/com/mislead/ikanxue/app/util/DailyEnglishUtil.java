package com.mislead.ikanxue.app.util;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mislead.ikanxue.app.model.DailyEnglish;
import java.util.Date;

/**
 * DailyEnglishUtil
 * AUTHOR:Zhaoyy  2015/6/27
 * DESC:
 **/
public class DailyEnglishUtil {

  private static String TAG = "DailyEnglishUtil";

  private static final String DAILY_URL = "http://open.iciba.com/dsapi";

  private static final String SH_LAST_DAILY_EN = "last_daily_en";
  private static final String SH_LAST_DAILY_ZH = "last_daily_zh";
  public static final String SH_LAST_DAILY_PIC_URL = "last_daily_pic_url";
  private static final String SH_LAST_DAILY_DATE = "last_daily_date";

  private VolleyHelper.ResponseListener<String> listener = null;

  public DailyEnglishUtil prepareListener() {
    listener = new VolleyHelper.ResponseListener<String>() {
      @Override public void onErrorResponse(VolleyError volleyError) {
        LogHelper.e(volleyError.getMessage());
      }

      @Override public void onResponse(String s) {

        Gson gson = new Gson();
        DailyEnglish object = gson.fromJson(s, DailyEnglish.class);

        ShPreUtil.setVal(SH_LAST_DAILY_EN, object.getContent());
        ShPreUtil.setVal(SH_LAST_DAILY_ZH, object.getNote());
        ShPreUtil.setVal(SH_LAST_DAILY_PIC_URL, object.getPicture());
        ShPreUtil.setVal(SH_LAST_DAILY_DATE, object.getDateline());
        // cache the image
        VolleyHelper.requestAndCacheImage(object.getPicture(), AndroidHelper.getImageDiskCache());
      }
    };

    return this;
  }

  public DailyEnglish GetDailyEnglish() {

    String today = DateHelper.formateDateString(new Date());
    // just request, if haven't request today
    if (!today.equals(ShPreUtil.getString(SH_LAST_DAILY_DATE))) {
      getDailyEnglishFromNet();
    }
    //LogHelper.e(SH_LAST_DAILY_EN + ShPreUtil.getString(SH_LAST_DAILY_EN));
    if (ShPreUtil.getString(SH_LAST_DAILY_EN).isEmpty()) {
      return null;
    } else {
      DailyEnglish dailyEnglish = new DailyEnglish();
      dailyEnglish.setContent(ShPreUtil.getString(SH_LAST_DAILY_EN));
      dailyEnglish.setNote(ShPreUtil.getString(SH_LAST_DAILY_ZH));
      dailyEnglish.setPicture(ShPreUtil.getString(SH_LAST_DAILY_PIC_URL));

      return dailyEnglish;
    }
  }

  private void getDailyEnglishFromNet() {
    VolleyHelper.requestStringGet(DAILY_URL, listener);
  }
}
