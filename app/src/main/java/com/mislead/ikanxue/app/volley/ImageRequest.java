package com.mislead.ikanxue.app.volley;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * ImageRequest
 *
 * @author Mislead
 *         DATE: 2015/7/6
 *         DESC:
 **/
public class ImageRequest extends Request<Bitmap> {
  private static final int IMAGE_TIMEOUT_MS = 1000;
  private static final int IMAGE_MAX_RETRIES = 2;
  private static final float IMAGE_BACKOFF_MULT = 2.0F;
  private final Response.Listener<Bitmap> mListener;
  private final Bitmap.Config mDecodeConfig;
  private final int mMaxWidth;
  private final int mMaxHeight;
  private static final Object sDecodeLock = new Object();

  public ImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight,
      Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
    super(0, url, errorListener);
    this.setRetryPolicy(
        new DefaultRetryPolicy(IMAGE_TIMEOUT_MS, IMAGE_MAX_RETRIES, IMAGE_BACKOFF_MULT));
    this.mListener = listener;
    this.mDecodeConfig = decodeConfig;
    this.mMaxWidth = maxWidth;
    this.mMaxHeight = maxHeight;
  }

  public Priority getPriority() {
    return Priority.LOW;
  }

  private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
      int actualSecondary) {
    if (maxPrimary == 0 && maxSecondary == 0) {
      return actualPrimary;
    } else {
      double ratio;
      if (maxPrimary == 0) {
        ratio = (double) maxSecondary / (double) actualSecondary;
        return (int) ((double) actualPrimary * ratio);
      } else if (maxSecondary == 0) {
        return maxPrimary;
      } else {
        ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if ((double) maxPrimary * ratio > (double) maxSecondary) {
          resized = (int) ((double) maxSecondary / ratio);
        }

        return resized;
      }
    }
  }

  protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
    Object var2 = sDecodeLock;
    synchronized (sDecodeLock) {
      Response var10000;
      try {
        var10000 = this.doParse(response);
      } catch (OutOfMemoryError var4) {
        VolleyLog.e("Caught OOM for %d byte image, url=%s",
            new Object[] { Integer.valueOf(response.data.length), this.getUrl() });
        return Response.error(new ParseError(var4));
      }

      return var10000;
    }
  }

  private Response<Bitmap> doParse(NetworkResponse response) {
    byte[] data = response.data;
    BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
    Bitmap bitmap = null;
    if (this.mMaxWidth == 0 && this.mMaxHeight == 0) {
      decodeOptions.inPreferredConfig = this.mDecodeConfig;
      bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
    } else {
      decodeOptions.inJustDecodeBounds = true;
      BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
      int actualWidth = decodeOptions.outWidth;
      int actualHeight = decodeOptions.outHeight;
      int desiredWidth =
          getResizedDimension(this.mMaxWidth, this.mMaxHeight, actualWidth, actualHeight);
      int desiredHeight =
          getResizedDimension(this.mMaxHeight, this.mMaxWidth, actualHeight, actualWidth);
      decodeOptions.inJustDecodeBounds = false;
      decodeOptions.inSampleSize =
          findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
      Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
      if (tempBitmap == null
          || tempBitmap.getWidth() <= desiredWidth && tempBitmap.getHeight() <= desiredHeight) {
        bitmap = tempBitmap;
      } else {
        bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
        tempBitmap.recycle();
      }
    }

    if (bitmap == null) {
      return Response.error(new ParseError());
    } else {
      return Response.success(bitmap, HttpHeaderParser.parseCacheHeaders(response));
    }
  }

  protected void deliverResponse(Bitmap response) {
    this.mListener.onResponse(response);
  }

  static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth,
      int desiredHeight) {
    double wr = (double) actualWidth / (double) desiredWidth;
    double hr = (double) actualHeight / (double) desiredHeight;
    double ratio = Math.min(wr, hr);

    float n;
    for (n = 1.0F; (double) (n * 2.0F) <= ratio; n *= 2.0F) {
      ;
    }

    return (int) n;
  }
}
