package com.mislead.ikanxue.app.util;

import android.util.Log;

/**
 * LogHelper
 * AUTHOR:Zhaoyy  2015/6/27
 * DESC:
 **/
public class LogHelper {

  private static String TAG = "Mislead";

  public static boolean isDebug = true;

  public static void e(String msg) {
    e(TAG, msg);
  }

  public static void e(String tag, String msg) {
    if (isDebug) Log.e(tag, msg);
  }

  public static void i(String tag, String msg) {
    if (isDebug) Log.i(tag, msg);
  }

  public static void i(String msg) {
    i(TAG, msg);
  }
}
