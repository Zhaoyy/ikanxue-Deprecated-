package com.mislead.ikanxue.app.util;

import android.content.Context;
import android.widget.Toast;

/**
 * ToastHelper
 *
 * @author Mislead
 *         DATE: 2015/7/2
 *         DESC:
 **/
public class ToastHelper {

  private static String TAG = "ToastHelper";

  private static void showToast(Context context, String msg, int showTime) {
    Toast.makeText(context, msg, showTime).show();
  }

  public static void toastLong(Context context, String msg) {
    showToast(context, msg, Toast.LENGTH_LONG);
  }

  public static void toastShort(Context context, String msg) {
    showToast(context, msg, Toast.LENGTH_SHORT);
  }
}
