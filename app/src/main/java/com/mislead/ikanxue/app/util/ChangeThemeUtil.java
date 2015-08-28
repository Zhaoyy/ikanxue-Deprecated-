package com.mislead.ikanxue.app.util;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * ThemeAttrUtil
 *
 * @author Mislead
 *         DATE: 2015/8/27
 *         DESC:
 **/
public class ChangeThemeUtil {

  private static String TAG = "ThemeAttrUtil";

  /**
   * get exactly attribute color value in current theme
   *
   * @param theme current theme
   * @param attrId attribute id
   * @return return color, 0 if not found
   */
  public static int getAttrColorValue(Resources.Theme theme, int attrId) {
    TypedArray array = theme.obtainStyledAttributes(new int[] { attrId });
    int color = array.getColor(0, 0);
    return color;
  }

  /**
   * change view's(instanceof TextView) textColor.
   */
  public static void ChangeViewTextColor(View view, int textColor) {
    ChangeViewTextColor(view, textColor, 0);
  }

  /**
   * change view's(instanceof TextView) textColor.
   *
   * @param view root view
   * @param textColor text color
   * @param exceptId view id not deal with
   */
  public static void ChangeViewTextColor(View view, int textColor, int exceptId) {
    if (view instanceof ViewGroup) {
      int count = ((ViewGroup) view).getChildCount();
      for (int i = 0; i < count; i++) {
        View v = ((ViewGroup) view).getChildAt(i);

        if (exceptId != 0 && v.getId() == exceptId) {
          continue;
        }

        ChangeViewTextColor(v, textColor, exceptId);
      }
    } else if (view instanceof TextView) {
      if (exceptId != 0 && view.getId() == exceptId) {
        return;
      }
      ((TextView) view).setTextColor(textColor);
    }
  }

  /**
   * change EditText hint text color
   */
  public static void ChangeETHintColor(View view, int hintColor) {
    if (view instanceof ViewGroup) {
      int count = ((ViewGroup) view).getChildCount();
      for (int i = 0; i < count; i++) {
        View v = ((ViewGroup) view).getChildAt(i);

        ChangeETHintColor(v, hintColor);
      }
    } else if (view instanceof EditText) {
      ((EditText) view).setHintTextColor(hintColor);
    }
  }
}
