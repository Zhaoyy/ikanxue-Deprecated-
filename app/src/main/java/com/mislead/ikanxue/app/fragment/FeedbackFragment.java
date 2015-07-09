package com.mislead.ikanxue.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.base.BaseFragment;

/**
 * FeedbackFragment
 *
 * @author Mislead
 *         DATE: 2015/7/8
 *         DESC:
 **/
public class FeedbackFragment extends BaseFragment {

  private static String TAG = "FeedbackFragment";

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    title = getString(R.string.feed_back);
    return inflater.inflate(R.layout.fragment_feedback, null);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }
}
