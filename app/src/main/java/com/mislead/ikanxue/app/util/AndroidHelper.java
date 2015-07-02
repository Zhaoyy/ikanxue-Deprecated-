package com.mislead.ikanxue.app.util;

import android.content.Context;
import android.os.Environment;
import com.mislead.ikanxue.app.model.ImageDiskCache;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * AndroidHelper
 * AUTHOR:Zhaoyy  2015/6/27
 * DESC:
 **/
public class AndroidHelper {

  private static String TAG = "AndroidHelper";

  private static Context mContext;
  private static File cacheDirPath;

  private static ImageDiskCache imageDiskCache;
  private static final int CACHE_SIZE = 10 * 1024 * 1024;// max cache size by byte

  public static void init(Context context) {
    mContext = context;
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      cacheDirPath = mContext.getExternalCacheDir();
    } else {
      cacheDirPath = mContext.getCacheDir();
    }
    LogHelper.e(cacheDirPath.getAbsolutePath());
    imageDiskCache = new ImageDiskCache(cacheDirPath, CACHE_SIZE);
    imageDiskCache.initialize();
  }

  public static ImageDiskCache getImageDiskCache() {
    return imageDiskCache;
  }


}
