package com.mislead.ikanxue.app.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.mislead.ikanxue.app.activity.MainActivity;

/**
 * BaseFragment
 *
 * @author Mislead
 *         DATE: 2015/7/9
 *         DESC:
 **/
public class BaseFragment extends Fragment {

  private static String TAG = "BaseFragment";

  protected String title = "i看雪";

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  protected MainActivity mainActivity;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    mainActivity = (MainActivity) activity;
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (!hidden) {
      mainActivity.getSupportActionBar().setTitle(title);
    }
  }
}
