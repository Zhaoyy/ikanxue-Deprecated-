package com.mislead.ikanxue.app.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.mislead.ikanxue.app.R;

/**
 * FragmentHelper
 *
 * @author Mislead
 *         DATE: 2015/7/8
 *         DESC:
 **/
public class FragmentHelper {

  private static String TAG = "FragmentHelper";

  private static FragmentManager mFragmentManager;

  public static void init(FragmentManager fragmentManager) {
    mFragmentManager = fragmentManager;
  }

  public static void addFragment(Fragment fragment, int containerID) {
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.add(containerID, fragment, getFragmentTag(fragment));
    transaction.commit();
  }

  public static void showFragment(Fragment fragment, int containerID) {
    String tag = getFragmentTag(fragment);

    Fragment fg = mFragmentManager.findFragmentByTag(tag);

    if (fg == null) {
      addFragment(fragment, containerID);
    }

    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.show(fragment).setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
    transaction.commit();
  }

  public static void hideFragment(Fragment fragment) {
    String tag = getFragmentTag(fragment);

    Fragment fg = mFragmentManager.findFragmentByTag(tag);

    if (fg == null) {
      return;
    }

    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.hide(fragment);
    transaction.commit();
  }

  public static void removeFragment(Fragment fragment) {
    FragmentTransaction transaction = mFragmentManager.beginTransaction();
    transaction.remove(fragment);
    transaction.commit();
  }

  public static void executePendingTransactions() {
    mFragmentManager.executePendingTransactions();
  }

  public static boolean hasAdded(Fragment fragment) {
    String tag = getFragmentTag(fragment);

    Fragment fg = mFragmentManager.findFragmentByTag(tag);

    return fg != null;
  }

  private static String getFragmentTag(Fragment fragment) {
    return TAG + fragment.getClass().getName();
  }
}
