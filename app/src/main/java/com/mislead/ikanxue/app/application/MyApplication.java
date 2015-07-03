package com.mislead.ikanxue.app.application;

import android.app.Application;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.CrashHandler;
import com.mislead.ikanxue.app.util.ShPreUtil;
import com.mislead.ikanxue.app.volley.VolleyHelper;

/**
 * MyApplication
 * AUTHOR:Zhaoyy  2015/6/29
 * DESC:
 **/
public class MyApplication extends Application {

  private static String TAG = "MyApplication";

  @Override public void onCreate() {
    super.onCreate();
    ShPreUtil.init(getApplicationContext());
    VolleyHelper.init(getApplicationContext());
    AndroidHelper.init(getApplicationContext());

    CrashHandler.getInstance().init(getApplicationContext());

    Api.getInstance().setmCon(getApplicationContext());
  }
}
