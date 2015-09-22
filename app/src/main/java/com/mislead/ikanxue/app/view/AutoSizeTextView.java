package com.mislead.ikanxue.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.util.LogHelper;

/**
 * AutoSizeTextView
 *
 * @author Mislead
 * DATE: 2015/9/22
 * DESC:
 **/

/**
 * TextView that can fit lines limit by resizing text size.
 * Extra fields:
 * {@link #mMaxLines}: max lines limit({@link #getMaxLines()} only support
 * above API level 16, so we need a extra one.
 * {@link #mMinTextSize}: min textSize allowed.
 */
public class AutoSizeTextView extends TextView {

  private static String TAG = "AutoSizeTextView";
  private int mMaxLines = 0;
  private float mMinTextSize = 0;
  private float mMaxTextSize = 0;
  private Paint testPaint;

  public AutoSizeTextView(Context context) {
    super(context);
  }

  public AutoSizeTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoSizeTextView);
    mMaxLines = a.getInt(R.styleable.AutoSizeTextView_max_lines, 0);
    mMinTextSize = a.getDimensionPixelSize(R.styleable.AutoSizeTextView_min_text_size, 0);
    a.recycle();

    init();
  }

  public AutoSizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.AutoSizeTextView, defStyleAttr, 0);

    mMaxLines = a.getInt(R.styleable.AutoSizeTextView_max_lines, 0);
    mMinTextSize = a.getDimensionPixelSize(R.styleable.AutoSizeTextView_min_text_size, 0);
    a.recycle();

    init();
  }

  private void init() {
    testPaint = new Paint();
    mMaxTextSize = getTextSize();
    LogHelper.e(
        "max line:" + mMaxLines + " min size:" + mMinTextSize + " max size:" + mMaxTextSize);
  }

  @Override
  protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
    super.onTextChanged(text, start, lengthBefore, lengthAfter);
    //resize2FitLines(text.toString(), getWidth());
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    if (w != oldw) {
      resize2FitLines(getText().toString(), w);
    }
  }

  private void resize2FitLines(String text, int width) {
    if (mMaxLines <= 0) return;

    testPaint.setTextSize(mMaxTextSize);
    int avaliableWidth = width - getPaddingLeft() - getPaddingRight();

    int lineTextCount = text.length() / mMaxLines + (text.length() % mMaxLines > 0 ? 1 : 0);

    while (testPaint.measureText(text, 0, lineTextCount) > avaliableWidth
        && mMaxTextSize > mMinTextSize) {
      mMaxTextSize--;
      testPaint.setTextSize(mMaxTextSize);
      LogHelper.e("min:" + mMaxTextSize);
    }
    LogHelper.e(
        "max line:" + mMaxLines + " min size:" + mMinTextSize + " max size:" + mMaxTextSize);
    setTextSize(TypedValue.COMPLEX_UNIT_PX, mMaxTextSize);
  }
}
