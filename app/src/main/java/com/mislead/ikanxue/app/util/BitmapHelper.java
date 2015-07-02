package com.mislead.ikanxue.app.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * BitmapHelper
 * AUTHOR:Zhaoyy  2015/6/27
 * DESC:
 **/
public class BitmapHelper {

  private static String TAG = "BitmapHelper";

  public static Bitmap drawShadow(Bitmap map, int radius) {
    if (map == null)
      return null;

    BlurMaskFilter blurFilter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
    Paint shadowPaint = new Paint();
    shadowPaint.setMaskFilter(blurFilter);

    int[] offsetXY = new int[2];
    Bitmap shadowImage = map.extractAlpha(shadowPaint, offsetXY);
    shadowImage = shadowImage.copy(Bitmap.Config.ARGB_8888, true);
    Canvas c = new Canvas(shadowImage);
    c.drawBitmap(map, -offsetXY[0], -offsetXY[1], null);
    return shadowImage;
  }

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  public static byte[] convertBitmapToBytes(Bitmap bitmap) {
    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
    //  ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
    //  bitmap.copyPixelsToBuffer(buffer);
    //  return buffer.array();
    //} else {
    //  ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    //  byte[] data = baos.toByteArray();
    //  return data;
    //}
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      byte[] data = baos.toByteArray();
      return data;
  }
}
