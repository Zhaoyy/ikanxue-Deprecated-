package com.mislead.ikanxue.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.base.BaseFragment;

/**
 * TitlesFragment
 *
 * @author Mislead
 *         DATE: 2015/7/8
 *         DESC:
 **/
public class TitlesFragment extends BaseFragment {

  private static String TAG = "FeedbackFragment";

  private TextView tvContent;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    title = getString(R.string.titles);
    return inflater.inflate(R.layout.fragment_feedback, null);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    tvContent = (TextView) view.findViewById(R.id.tv_content);
    tvContent.setText(getResources().getString(R.string.titles));
  }
}
