package com.mislead.ikanxue.app.util;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
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
    if (view instanceof ViewGroup) {
      int count = ((ViewGroup) view).getChildCount();
      for (int i = 0; i < count; i++) {
        ChangeViewTextColor(((ViewGroup) view).getChildAt(i), textColor);
      }
    } else if (view instanceof TextView) {
      ((TextView) view).setTextColor(textColor);
    }
  }
}
