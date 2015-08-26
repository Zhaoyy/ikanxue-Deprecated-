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

  public static final String LOGIN_STATE_CHANGE_ACTION = "action_login_state_change";
  public static final String THEME_CHANGE_ACTION = "theme_change";

  @Override public void onCreate() {
    super.onCreate();
    ShPreUtil.init(getApplicationContext());
    VolleyHelper.init(getApplicationContext());
    AndroidHelper.init(getApplicationContext());

    CrashHandler.getInstance().init(getApplicationContext());

    Api.getInstance().setmCon(getApplicationContext());
  }
}
