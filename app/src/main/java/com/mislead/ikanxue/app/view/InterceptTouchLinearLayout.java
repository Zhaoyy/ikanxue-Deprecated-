package com.mislead.ikanxue.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * InterceptTouchLinearLayout
 *
 * @author Mislead
 *         DATE: 2015/9/24
 *         DESC:
 **/
public class InterceptTouchLinearLayout extends LinearLayout {

  private static String TAG = "InterceptTouchLinearLayout";
  private boolean isInterceptTouch = false;

  public InterceptTouchLinearLayout(Context context) {
    super(context);
  }

  public InterceptTouchLinearLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public InterceptTouchLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setIsInterceptTouch(boolean isInterceptTouch) {
    this.isInterceptTouch = isInterceptTouch;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public InterceptTouchLinearLayout(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {

    return isInterceptTouch || super.onInterceptTouchEvent(ev);
  }
}

