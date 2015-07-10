package com.mislead.ikanxue.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerItemDecoration
 *
 * @author Mislead
 *         DATE: 2015/7/10
 *         DESC:
 **/
public class RecyclerLinearItemDecoration extends RecyclerView.ItemDecoration {

  private static String TAG = "RecyclerItemDecoration";

  private int orientation = LinearLayoutManager.VERTICAL;

  private int attr[] = new int[] {
      android.R.attr.listDivider
  };

  private Context context;
  private Drawable divider;

  public RecyclerLinearItemDecoration(Context context, int orientation) {
    this.context = context;
    TypedArray array = context.obtainStyledAttributes(attr);
    divider = array.getDrawable(0);
    array.recycle();

    setOrientation(orientation);
  }

  private void setOrientation(int orientation) {
    if (orientation != LinearLayoutManager.HORIZONTAL
        && orientation != LinearLayoutManager.VERTICAL) {
      throw new RuntimeException("orientation type error!");
    }
    this.orientation = orientation;
  }

  @Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    super.onDraw(c, parent, state);
    if (orientation == LinearLayoutManager.HORIZONTAL) {
      drawHorzonLine(c, parent);
    } else {
      drawVerticalLine(c, parent);
    }
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);

    if (orientation == LinearLayoutManager.HORIZONTAL) {
      outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
    } else {
      outRect.set(0, 0, 0, divider.getIntrinsicHeight());
    }
  }

  private void drawHorzonLine(Canvas c, RecyclerView view) {

    int left = view.getPaddingLeft();
    int right = view.getRight() - view.getPaddingRight();

    for (int i = 0; i < view.getChildCount(); i++) {
      View v = view.getChildAt(i);

      RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) v.getLayoutParams();

      int top = v.getBottom() + params.bottomMargin;
      int bottom = top + divider.getIntrinsicHeight();

      divider.setBounds(left, top, right, bottom);

      divider.draw(c);
    }
  }

  private void drawVerticalLine(Canvas c, RecyclerView view) {
    int top = view.getPaddingTop();
    int bottom = view.getBottom() - view.getPaddingBottom();

    for (int i = 0; i < view.getChildCount(); i++) {
      View v = view.getChildAt(i);

      RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) v.getLayoutParams();

      int left = v.getRight() + params.rightMargin;
      int right = left + divider.getIntrinsicWidth();

      divider.setBounds(left, top, right, bottom);

      divider.draw(c);
    }
  }
}
