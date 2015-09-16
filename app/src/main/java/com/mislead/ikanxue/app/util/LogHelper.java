package com.mislead.ikanxue.app.util;

import android.text.TextUtils;
import android.util.Log;
import com.mislead.ikanxue.app.BuildConfig;

/**
 * LogHelper
 * AUTHOR:Zhaoyy  2015/6/27
 * DESC:
 **/
public class LogHelper {

  private static String TAG = "Mislead";

  static String tagPrefix = "cp:";
  static String className;
  static String methodName;
  static int lineNumber;

  private LogHelper() {
        /* Protect from instantiations */
  }

  public static boolean isDebuggable() {
    return BuildConfig.DEBUG;
  }

  private static String createLog(String log) {

    StringBuffer buffer = new StringBuffer();
    buffer.append("[");
    buffer.append(methodName);
    buffer.append(":");
    buffer.append(lineNumber);
    buffer.append("]");
    buffer.append(log);

    return buffer.toString();
  }

  private static void getMethodNames(StackTraceElement[] sElements) {
    className = sElements[1].getFileName();
    if (!TextUtils.isEmpty(tagPrefix)) {
      className = tagPrefix + className;
    }
    methodName = sElements[1].getMethodName();
    lineNumber = sElements[1].getLineNumber();
  }

  public static void e(String message) {
    if (!isDebuggable()) return;

    // Throwable instance must be created before any methods
    getMethodNames(new Throwable().getStackTrace());
    Log.e(className, createLog(message));
  }

  public static void i(String message) {
    if (!isDebuggable()) return;

    getMethodNames(new Throwable().getStackTrace());
    Log.i(className, createLog(message));
  }

  public static void d(String message) {
    if (!isDebuggable()) return;

    getMethodNames(new Throwable().getStackTrace());
    Log.d(className, createLog(message));
  }

  public static void v(String message) {
    if (!isDebuggable()) return;

    getMethodNames(new Throwable().getStackTrace());
    Log.v(className, createLog(message));
  }

  public static void w(String message) {
    if (!isDebuggable()) return;

    getMethodNames(new Throwable().getStackTrace());
    Log.w(className, createLog(message));
  }

  public static void wtf(String message) {
    if (!isDebuggable()) return;

    getMethodNames(new Throwable().getStackTrace());
    Log.wtf(className, createLog(message));
  }
}
