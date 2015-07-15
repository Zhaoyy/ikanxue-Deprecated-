package com.mislead.ikanxue.app.volley;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.util.AndroidHelper;

/**
 * VolleyImageGetter
 *
 * ImageGetter, to get the image in html string, by using Volley.
 *
 * @author Mislead
 *         DATE: 2015/7/15
 *         DESC:
 **/
public class VolleyImageGetter implements Html.ImageGetter {

  private static String TAG = "VolleyImageGetter";
  private int minHeight = 40;

  private TextView textView;
  private Drawable drawable;

  public VolleyImageGetter(TextView textView) {
    this.textView = textView;

    minHeight = getFountHeight(textView.getTextSize());
  }

  private int getFountHeight(float textSize) {
    Paint paint = new Paint();
    paint.setTextSize(textSize);
    Paint.FontMetrics fm = paint.getFontMetrics();
    return (int) Math.ceil(fm.descent - fm.ascent);
  }

  @Override public Drawable getDrawable(String source) {

    // get the cache key, find bitmap in cache.
    final String key = VolleyHelper.getCacheKey(source);

    Bitmap bitmap = AndroidHelper.getImageDiskCache().getBitmap(key);
    // if not found, request with url
    if (bitmap == null) {
      VolleyHelper.requestImageWithCacheAndHeader(source, Api.getInstance().getCookieHeader(),
          new VolleyHelper.ResponseListener<Bitmap>() {
            @Override public void onErrorResponse(VolleyError volleyError) {

            }

            @Override public void onResponse(Bitmap bitmap) {

              // request success, cache bitmap and relayout textView
              if (bitmap != null) {
                AndroidHelper.getImageDiskCache().putBitmap(key, bitmap);
                textView.requestLayout();
              }
            }
          });
    } else {
      drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
      drawable.setBounds(0, 0, bitmap.getWidth() < minHeight ? minHeight : bitmap.getWidth(),
          bitmap.getHeight() < minHeight ? minHeight : bitmap.getHeight());
    }

    return drawable;
  }
}
