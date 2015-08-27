package com.mislead.circleimageview.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * CircleImageView
 *
 * @author Mislead
 *         DATE: 2015/8/10
 *         DESC:
 **/
public class CircleImageView extends ImageView {

  private static String TAG = "CircleImageView";

  private static final int DEFAULT_BORDER_WIDTH = 0;
  private static final int DEFAULT_BORDER_COLOR = Color.WHITE;
  private static final int COLORDRAWABLE_DIMEN = 1;
  private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.RGB_565;

  private Paint bitmapPaint = new Paint();
  private Paint borderPaint = new Paint();
  private boolean already = false;
  private int borederWidth = 0;
  private int borderColor = Color.WHITE;

  private RectF bitmapRect = new RectF();
  private RectF borderRect = new RectF();

  private int bitmapRadius = 0;
  private int borderRadius = 0;
  private int bitmapWidth = 0;
  private int bitmapHeight = 0;

  private Bitmap bitmap;
  private BitmapShader bitmapShader;
  private Matrix shaderMatrix = new Matrix();

  public CircleImageView(Context context) {
    super(context);
  }

  public CircleImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setScaleType(ScaleType.CENTER_CROP);

    TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);
    borederWidth =
        a.getDimensionPixelSize(R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
    borderColor = a.getColor(R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR);
    a.recycle();
    //Log.e(TAG, "borderWidth:" + borederWidth);
    setUp();
  }

  public void setBorderWidth(int width) {
    borederWidth = width;
    setUp();
  }

  public void setBorderColor(int color) {
    borderColor = color;
    borderPaint.setColor(color);
    setUp();
  }

  @Override public void setImageBitmap(Bitmap bm) {
    super.setImageBitmap(bm);
    bitmap = bm;
    setUp();
  }

  @Override public void setImageResource(int resId) {
    super.setImageResource(resId);
    bitmap = getBitmapFromDrawable(getDrawable());
    setUp();
  }

  @Override public void setImageDrawable(Drawable drawable) {
    super.setImageDrawable(drawable);
    bitmap = getBitmapFromDrawable(getDrawable());
    setUp();
  }

  @Override protected void onDraw(Canvas canvas) {
    //super.onDraw(canvas);

    if (borederWidth > 0) {
      canvas.drawCircle(getWidth() / 2, getHeight() / 2, borderRadius, borderPaint);
    }
    if (getDrawable() == null) return;
    //Log.e(TAG, "onDraw");
    canvas.drawCircle(getWidth() / 2, getHeight() / 2, bitmapRadius, bitmapPaint);

  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    setUp();
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
  }

  private Bitmap getBitmapFromDrawable(Drawable drawable) {

    if (drawable == null) return null;

    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    }

    Bitmap bitmap;

    if (drawable instanceof ColorDrawable) {
      bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMEN, COLORDRAWABLE_DIMEN, DEFAULT_CONFIG);
    } else {
      bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
          DEFAULT_CONFIG);
    }
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);

    return bitmap;
  }

  private void setUp() {
    if (bitmap == null) return;
    //Log.e(TAG, "setup");
    //if (already) return;
    if (bitmapPaint == null) {
      bitmapPaint = new Paint();
    }

    if (borderPaint == null) {
      borderPaint = new Paint();
    }
    if (shaderMatrix == null) {
      shaderMatrix = new Matrix();
    }

    bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

    bitmapPaint.setAntiAlias(true);
    bitmapPaint.setShader(bitmapShader);

    borderPaint.setAntiAlias(true);
    borderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    borderPaint.setColor(borderColor);

    bitmapWidth = bitmap.getWidth();
    bitmapHeight = bitmap.getHeight();

    borderRect = new RectF(getPaddingLeft() + 1, getPaddingTop() + 1,
        getMeasuredWidth() - getPaddingRight() - 1, getMeasuredHeight() - getPaddingBottom() - 1);
    borderRadius = Math.min((int) borderRect.width() / 2, (int) borderRect.height() / 2);

    bitmapRect = new RectF(getPaddingLeft() + borederWidth, getPaddingTop() + borederWidth,
        borderRect.width() - borederWidth, borderRect.height() - borederWidth);
    bitmapRadius = Math.min((int) bitmapRect.width() / 2, (int) bitmapRect.height() / 2);

    getShaderMatrix();

    invalidate();
  }

  private void getShaderMatrix() {
    float scall = 0;

    shaderMatrix.set(null);

    // 尽可能多显示图片
    if (bitmapWidth * bitmapRect.height() > bitmapHeight * bitmapRect.width()) {
      scall = bitmapRect.height() / (float) bitmapHeight;
    } else {
      scall = bitmapRect.width() / (float) bitmapWidth;
    }

    shaderMatrix.setScale(scall, scall);
    shaderMatrix.postTranslate(borederWidth, borederWidth);

    bitmapShader.setLocalMatrix(shaderMatrix);
  }
}
