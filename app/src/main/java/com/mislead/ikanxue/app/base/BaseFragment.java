package com.mislead.ikanxue.app.base;

import android.support.v4.app.Fragment;

/**
 * BaseFragment
 *
 * @author Mislead
 *         DATE: 2015/7/9
 *         DESC:
 **/
public class BaseFragment extends Fragment {

  private static String TAG = "BaseFragment";

  private String title = "";

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
