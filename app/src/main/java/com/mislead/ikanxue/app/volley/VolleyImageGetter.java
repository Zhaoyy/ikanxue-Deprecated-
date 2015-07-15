package com.mislead.ikanxue.app.volley;

import android.content.res.Resources;
import android.graphics.Bitmap;
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
 * @author Mislead
 *         DATE: 2015/7/15
 *         DESC:
 **/
public class VolleyImageGetter implements Html.ImageGetter {

  private static String TAG = "VolleyImageGetter";

  private TextView textView;
  private Drawable drawable;

  public VolleyImageGetter(TextView textView) {
    this.textView = textView;
  }

  @Override public Drawable getDrawable(String source) {

    final String key = VolleyHelper.getCacheKey(source);

    Bitmap bitmap = AndroidHelper.getImageDiskCache().getBitmap(key);

    if (bitmap == null) {
      VolleyHelper.requestImageWithCacheAndHeader(source, Api.getInstance().getCookieHeader(),
          new VolleyHelper.ResponseListener<Bitmap>() {
            @Override public void onErrorResponse(VolleyError volleyError) {

            }

            @Override public void onResponse(Bitmap bitmap) {
              if (bitmap != null) {
                AndroidHelper.getImageDiskCache().putBitmap(key, bitmap);
                textView.requestLayout();
              }
            }
          });
    } else {
      drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
      drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    return drawable;
  }
}
