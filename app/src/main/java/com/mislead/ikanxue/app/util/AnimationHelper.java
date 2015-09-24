package com.mislead.ikanxue.app.util;

import android.view.View;

/**
 * AnimationHelper
 *
 * @author Mislead
 *         DATE: 2015/9/23
 *         DESC:
 **/
public class AnimationHelper {

  private static String TAG = "AnimationHelper";

  public static void setTranslateX(View v, float tranX) {
    v.setTranslationX(tranX);
  }

  public static void setScaleY(View v, float scaleY) {
    v.setScaleY(scaleY);
  }

  public static void setScaleX(View v, float scaleX) {
    v.setScaleX(scaleX);
  }

  public static void setAlpha(View v, float alpha) {
    v.setAlpha(alpha);
  }
}
