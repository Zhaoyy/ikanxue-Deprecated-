package com.mislead.ikanxue.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.view.ThreadTypePopup;

/**
 * PostNewThreadFragment
 *
 * @author Mislead
 *         DATE: 2015/7/17
 *         DESC:
 **/
public class PostNewThreadFragment extends BaseFragment {

  private static String TAG = "PostNewThreadFragment";
  private EditText et_title;
  private EditText et_kx;
  private EditText et_msg;

  private CheckBox ch_type;

  private ThreadTypePopup popup;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_new_thread, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    et_title = (EditText) view.findViewById(R.id.et_title);
    et_kx = (EditText) view.findViewById(R.id.et_kx);
    et_msg = (EditText) view.findViewById(R.id.et_msg);
    ch_type = (CheckBox) view.findViewById(R.id.ch_type);

    popup = new ThreadTypePopup(getActivity());
    popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    popup.setAnimationStyle(R.style.mypopwindow_anim_style);
    ch_type.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ch_type.setChecked(popup.isShowing());
        if (popup.isShowing()) {
          popup.dismiss();
        } else {
          popup.showAsDropDown(ch_type, 0, 32);
        }
      }
    });
  }

  @Override public boolean onBackPressed() {

    if (popup.isShowing()) {
      popup.dismiss();
      return true;
    }

    return super.onBackPressed();
  }
}
