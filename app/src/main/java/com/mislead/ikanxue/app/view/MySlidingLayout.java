package com.mislead.ikanxue.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.mislead.ikanxue.app.util.AnimationHelper;

/**
 * MySlidingLayout
 *
 * @author Mislead
 *         DATE: 2015/9/23
 *         DESC:
 **/
public class MySlidingLayout extends HorizontalScrollView implements View.OnClickListener {

  private static String TAG = "MySlidingLayout";

  private boolean once = true;
  private int menuWidth = 0;
  private ViewGroup menu;
  private ViewGroup content;
  private int mTouchSlop; //判断是否是滑动的界限。
  private int downX;
  private int downY;
  private int moveType = 0; //触摸滑动类型：0-不确定，1-上下，2-左右
  private boolean scrollEnable = false;//是否触摸来滚动

  private boolean isMenuOpen = false;

  public MySlidingLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MySlidingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setScrollEnable(boolean scrollEnable) {
    this.scrollEnable = scrollEnable;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    if (once) {
      mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
      LinearLayout wrapper = (LinearLayout) getChildAt(0);
      menu = (ViewGroup) wrapper.getChildAt(0);
      content = (ViewGroup) wrapper.getChildAt(1);
      menuWidth = getScreenWidth() * 2 / 3;
      menu.getLayoutParams().width = menuWidth;
      content.getLayoutParams().width = getScreenWidth();
      content.setOnClickListener(this);
      once = false;
    }

    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        downX = (int) ev.getRawX();
        downY = (int) ev.getRawY();
        break;
      case MotionEvent.ACTION_MOVE:
        int curX = (int) ev.getRawX();
        int curY = (int) ev.getRawY();
        // 确定是左右滑动
        if (Math.abs(curX - downX) > mTouchSlop
            && Math.abs(curY - downY) < mTouchSlop
            && moveType == 0) {
          moveType = 2;
        }

        if (Math.abs(curX - downX) < mTouchSlop
            && Math.abs(curY - downY) > mTouchSlop
            && moveType == 0) {
          moveType = 1;
        }

        break;

      case MotionEvent.ACTION_UP:
        moveType = 0;
        break;
      default:
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {

    switch (ev.getAction()) {
      case MotionEvent.ACTION_MOVE:
        if (moveType == 2) {
          return true;
        }

        if (moveType == 1) {
          return false;
        }

        break;

      default:
        break;
    }

    return super.onInterceptTouchEvent(ev);
  }

  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    if (changed) {
      toggle2Content();
    }
  }

  @Override public boolean onTouchEvent(MotionEvent ev) {

    switch (ev.getAction()) {
      case MotionEvent.ACTION_UP:
        int x = getScrollX();
        if (x > (menuWidth / 2)) {
          toggle2Content();
        } else {
          toggle2Menu();
        }
        return true;
      default:
        break;
    }

    return scrollEnable && super.onTouchEvent(ev);
  }

  @Override protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    float scale = l * 1.0f / menuWidth;
    float leftScale = 1 - 0.3f * scale;
    float rightScale = 0.8f + 0.2f * scale;

    AnimationHelper.setTranslateX(menu, scale * menuWidth * 0.6f);
    AnimationHelper.setScaleX(menu, leftScale);
    AnimationHelper.setScaleY(menu, leftScale);
    AnimationHelper.setAlpha(menu, leftScale);

    AnimationHelper.setScaleX(content, rightScale);
    AnimationHelper.setScaleY(content, rightScale);

    boolean toLeft = l == 0;

    if (isMenuOpen != toLeft) {
      isMenuOpen = toLeft;
      if (content instanceof InterceptTouchLinearLayout) {
        ((InterceptTouchLinearLayout) content).setIsInterceptTouch(isMenuOpen);
      }
    }
  }

  private int getScreenWidth() {
    return getContext().getResources().getDisplayMetrics().widthPixels;
  }

  public void toggle2Content() {
    smoothScrollTo(menuWidth, 0);
  }

  public void toggle2Menu() {
    smoothScrollTo(0, 0);
  }

  public boolean isMenuOpen() {
    return isMenuOpen;
  }

  @Override public void onClick(View v) {
    toggle2Content();
  }
}
