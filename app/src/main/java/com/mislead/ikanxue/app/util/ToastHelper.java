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

  private static Toast toast;

  private static void showToast(Context context, String msg, int showTime) {
    if (toast == null) {
      toast = Toast.makeText(context, msg, showTime);
    } else {
      toast.setText(msg);
      toast.setDuration(showTime);
    }
    toast.show();
  }

  public static void toastLong(Context context, String msg) {
    showToast(context, msg, Toast.LENGTH_LONG);
  }

  public static void toastShort(Context context, String msg) {
    showToast(context, msg, Toast.LENGTH_SHORT);
  }
}
