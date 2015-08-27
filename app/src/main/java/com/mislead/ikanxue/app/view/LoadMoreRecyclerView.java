package com.mislead.ikanxue.app.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.util.ChangeThemeUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * LoadMoreRecyclerView
 *
 * @author Mislead
 *         DATE: 2015/7/13
 *         DESC:
 **/
public class LoadMoreRecyclerView extends RecyclerView {

  private static String TAG = "LoadMoreRecyclerView";

  private View footView;
  private int footState;
  private Context context;

  private MaterialProgressDrawable progressDrawable;

  private Runnable progressRunable = new Runnable() {
    @Override public void run() {
      if (footState == 1) {
        progressDrawable.start();
      } else {
        progressDrawable.stop();
      }
    }
  };

  public LoadMoreRecyclerView(Context context) {
    super(context);
    this.context = context;
  }

  public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }

  public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.context = context;
  }

  public void initFootView() {
    // add foot view
    footView = LayoutInflater.from(context).inflate(R.layout.view_load_more, null);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    footView.setLayoutParams(params);
    ImageView ivProgress = (ImageView) footView.findViewById(R.id.iv_progress);
    progressDrawable = new MaterialProgressDrawable(context, ivProgress);
    progressDrawable.setAlpha(255);
    ivProgress.setImageDrawable(progressDrawable);
  }

  public void changeFootState(int state) {
    footState = state;

    footView.findViewById(R.id.tv_load_more).setVisibility(state == 0 ? View.VISIBLE : View.GONE);
    footView.findViewById(R.id.ll_loading).setVisibility(state == 1 ? View.VISIBLE : View.GONE);
    footView.findViewById(R.id.tv_no_more).setVisibility(state == 2 ? View.VISIBLE : View.GONE);

    footView.findViewById(R.id.iv_progress).post(progressRunable);
  }

  public void changetFootTextColor(int textColor) {
    ChangeThemeUtil.ChangeViewTextColor(footView, textColor);
  }

  public View getFootView() {
    return footView;
  }

  /**
   * clear all item view to reload them
   */
  public void clear() {
    try {
      Field recycler = RecyclerView.class.getDeclaredField("mRecycler");
      recycler.setAccessible(true);
      Method localMethod = Class.forName("android.support.v7.widget.RecyclerView$Recycler")
          .getDeclaredMethod("clear");
      localMethod.setAccessible(true);
      localMethod.invoke(recycler.get(this));
    } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException
        | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }

    getRecycledViewPool().clear();
  }
}
