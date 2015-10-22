package com.mislead.ikanxue.app.volley;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;
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

  public static VolleyImageGetter from(TextView textView) {
    return new VolleyImageGetter(textView);
  }

  private VolleyImageGetter(TextView textView) {
    minHeight = getFountHeight(textView.getTextSize());
  }

  private int getFountHeight(float textSize) {
    Paint paint = new Paint();
    paint.setTextSize(textSize);
    Paint.FontMetrics fm = paint.getFontMetrics();
    return (int) Math.ceil(fm.descent - fm.ascent);
  }

  @Override public Drawable getDrawable(final String source) {
    // get the cache key, find bitmap in cache.
    Drawable drawable = null;
    final String key = VolleyHelper.getCacheKey(source);

    Bitmap bitmap = AndroidHelper.getImageDiskCache().getBitmap(key);
    if (bitmap != null) {
      drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
      drawable.setBounds(0, 0, bitmap.getWidth() < minHeight ? minHeight : bitmap.getWidth(),
          bitmap.getHeight() < minHeight ? minHeight : bitmap.getHeight());
    }

    return drawable;
  }
}
