package com.mislead.ikanxue.app.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import com.mislead.ikanxue.app.model.ImageDiskCache;
import java.io.File;

/**
 * AndroidHelper
 * AUTHOR:Zhaoyy  2015/6/27
 * DESC:
 **/
public class AndroidHelper {

  private static String TAG = "AndroidHelper";

  private static Context mContext;
  private static File cacheDirPath;

  private static File splashCacheDirPath;

  private static ProgressDialog progressDialog;

  private static ImageDiskCache imageDiskCache;
  private static ImageDiskCache splashImageCache;
  private static final int IMAGE_CACHE_SIZE = 30 * 1024 * 1024;// max cache size by byte
  private static final int CACHE_SIZE = 10 * 1024 * 1024;// max cache size by byte

  public static void init(Context context) {
    mContext = context;
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      cacheDirPath = mContext.getExternalCacheDir();
    } else {
      cacheDirPath = mContext.getCacheDir();
    }
    imageDiskCache = new ImageDiskCache(cacheDirPath, IMAGE_CACHE_SIZE);
    imageDiskCache.initialize();

    splashCacheDirPath = new File(cacheDirPath, "splash");
    splashCacheDirPath.mkdirs();
    splashImageCache = new ImageDiskCache(splashCacheDirPath, CACHE_SIZE);
    splashImageCache.initialize();
  }

  public static ImageDiskCache getImageDiskCache() {
    return imageDiskCache;
  }

  public static ImageDiskCache getSplashImageCache() {
    return splashImageCache;
  }

  public static String getVersionName(Context context) {
    PackageInfo info = getPackageInfo(context, context.getPackageName());

    return info == null ? null : info.versionName;
  }

  /**
   * get app version code
   */
  public static int getVersionCode(Context context) {
    PackageInfo info = getPackageInfo(context, context.getPackageName());

    return info == null ? 0 : info.versionCode;
  }

  public static PackageInfo getPackageInfo(Context context, String packageName) {
    PackageManager manager = context.getPackageManager();
    try {
      PackageInfo info =
          manager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
      return info;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void showProgressDialog(Context context, String msg) {
    progressDialog = new ProgressDialog(context);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setMessage(msg);
    progressDialog.show();
  }

  public static void showProgressDialog(Context context) {
    showProgressDialog(context, "");
  }

  public static void setProgressDialogMsg(String msg) {
    if (progressDialog == null) return;

    progressDialog.setMessage(msg);
  }

  public static void hideProgressDialog() {
    if (progressDialog == null) return;

    progressDialog.dismiss();
  }

  public static int dp2px(Activity activity, float dp) {
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

    return (int) (dp * metrics.density + 0.5f);
  }

  public static void sleep(int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
