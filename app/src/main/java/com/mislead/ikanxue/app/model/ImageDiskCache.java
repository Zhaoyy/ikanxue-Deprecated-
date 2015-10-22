package com.mislead.ikanxue.app.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.mislead.ikanxue.app.util.BitmapHelper;
import java.io.File;

/**
 * ImageDiskCache
 * AUTHOR:Zhaoyy  2015/7/2
 * DESC: image disk cache for volley
 **/
public class ImageDiskCache extends DiskBasedCache implements ImageLoader.ImageCache {

  private static String TAG = "ImageDiskCache";

  public ImageDiskCache(File rootDirectory, int maxCacheSizeInBytes) {
    super(rootDirectory, maxCacheSizeInBytes);
  }

  public ImageDiskCache(File rootDirectory) {
    super(rootDirectory);
  }

  @Override public Bitmap getBitmap(String s) {
    final Entry entry = get(s);
    if (entry == null) return null;
    return BitmapFactory.decodeByteArray(entry.data, 0, entry.data.length);
  }

  @Override public void putBitmap(String s, Bitmap bitmap) {
    Entry entry = new Entry();
    entry.data = BitmapHelper.convertBitmapToBytes(bitmap);
    put(s, entry);
  }

  public boolean hasBitMap(String key) {
    return get(key) != null;
  }
}
