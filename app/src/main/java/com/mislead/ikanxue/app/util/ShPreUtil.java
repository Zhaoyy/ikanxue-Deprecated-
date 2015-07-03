package com.mislead.ikanxue.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * ShPreUtil
 * AUTHOR:Zhaoyy  2015/6/27
 * DESC:
 **/
public class ShPreUtil {

  private static String TAG = "ShPreUtil";

  private static SharedPreferences shPre = null;
  private static SharedPreferences.Editor editor = null;

  private static final String EMPTY_STRING = "";

  /**
   * init shared preference,you should init this in application
   * @param context
   */
  public static void init(Context context) {
    shPre = PreferenceManager.getDefaultSharedPreferences(context);
    editor = shPre.edit();
  }

  public static void setVal(String key, String val) {
    editor.putString(key, val);
    editor.apply();
  }

  public static void remove(String key) {
    editor.remove(key);
  }

  public static String getString(String key, String defVal) {
    return shPre.getString(key, defVal);
  }

  public static String getString(String key) {
    return getString(key, EMPTY_STRING);
  }

  public static int getInt(String key, int defVal) {
    return shPre.getInt(key, defVal);
  }

  public static int getInt(String key) {
    return getInt(key, 0);
  }
}
