package com.mislead.ikanxue.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.util.ChangeThemeUtil;

/**
 * AboutFragment
 *
 * @author Mislead
 *         DATE: 2015/7/8
 *         DESC:
 **/
public class AboutFragment extends BaseFragment {

  private static String TAG = "AboutFragment";

  private LinearLayout ll_root;
  private TextView tv_title1;
  private TextView tv_content1;
  private TextView tv_title2;
  private TextView tv_content2;
  private TextView tv_tail;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    title = getResources().getString(R.string.about);
    return inflater.inflate(R.layout.frament_about, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ll_root = (LinearLayout) view.findViewById(R.id.ll_root);
    tv_title1 = (TextView) view.findViewById(R.id.tv_title1);
    tv_content1 = (TextView) view.findViewById(R.id.tv_content1);
    tv_title2 = (TextView) view.findViewById(R.id.tv_title2);
    tv_content2 = (TextView) view.findViewById(R.id.tv_content2);
    tv_tail = (TextView) view.findViewById(R.id.tv_tail);
  }

  @Override protected void changeTheme() {
    int bgColor =
        ChangeThemeUtil.getAttrColorValue(getActivity().getTheme(), R.attr.second_main_bg_color);

    if (bgColor != 0) {
      ll_root.setBackgroundColor(bgColor);
    }

    int textColor1 =
        ChangeThemeUtil.getAttrColorValue(getActivity().getTheme(), R.attr.text_color_1);

    if (textColor1 != 0) {
      tv_title1.setTextColor(textColor1);
      tv_content1.setTextColor(textColor1);
      tv_title2.setTextColor(textColor1);
      tv_content2.setTextColor(textColor1);
    }

    int textColor2 =
        ChangeThemeUtil.getAttrColorValue(getActivity().getTheme(), R.attr.text_color_2);

    if (textColor2 != 0) {
      tv_tail.setTextColor(textColor2);
    }
  }
}
