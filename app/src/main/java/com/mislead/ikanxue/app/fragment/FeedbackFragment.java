package com.mislead.ikanxue.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.ChangeThemeUtil;
import com.mislead.ikanxue.app.util.RegexUtil;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;

/**
 * FeedbackFragment
 *
 * @author Mislead
 *         DATE: 2015/7/8
 *         DESC:
 **/
public class FeedbackFragment extends BaseFragment {

  private static String TAG = "FeedbackFragment";
  private LinearLayout ll_root;
  private LinearLayout ll_title;
  private EditText et_name;
  private EditText et_email;
  private EditText et_msg;

  private Api api;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    title = getString(R.string.feed_back);
    return inflater.inflate(R.layout.fragment_feed_back, container, false);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ll_root = (LinearLayout) view.findViewById(R.id.ll_root);
    ll_title = (LinearLayout) view.findViewById(R.id.ll_title);
    et_name = (EditText) view.findViewById(R.id.et_name);
    et_email = (EditText) view.findViewById(R.id.et_email);
    et_msg = (EditText) view.findViewById(R.id.et_msg);

    initInfo();
  }

  private void initInfo() {
    api = Api.getInstance();
    String name = api.getLoginUserName();

    if (!TextUtils.isEmpty(name)) {
      et_name.setHint(name);
    }
    String email = api.getEmail();
    if (!TextUtils.isEmpty(email)) {
      et_email.setHint(email);
    }
  }

  private String getTextOrHint(EditText editText) {
    String text = editText.getText().toString();

    if (TextUtils.isEmpty(text)) {
      text = editText.getHint().toString();
    }

    return text;
  }

  private void submit() {
    String msg = et_msg.getText().toString();
    if (TextUtils.isEmpty(msg)) {
      ToastHelper.toastShort(mainActivity, "请输入要提交的意见或者建议！");
      return;
    }
    String email = getTextOrHint(et_email);

    if (!RegexUtil.checkEmail(email)) {
      ToastHelper.toastShort(mainActivity, "请输入正确的邮件地址！");
      return;
    }

    AndroidHelper.showProgressDialog(mainActivity, "正在提交...");

    String name = getTextOrHint(et_name);

    api.feedback(name, email, msg, new VolleyHelper.ResponseListener<String>() {
      @Override public void onErrorResponse(VolleyError volleyError) {
        AndroidHelper.hideProgressDialog();
        ToastHelper.toastShort(mainActivity, "提交失败！");
      }

      @Override public void onResponse(String object) {
        AndroidHelper.hideProgressDialog();
        ToastHelper.toastShort(mainActivity, "提交成功！");
        //mainActivity.backToFragment(true);
      }
    });
  }

  @Override protected void changeTheme() {
    super.changeTheme();
    int bgColor =
        ChangeThemeUtil.getAttrColorValue(getActivity().getTheme(), R.attr.second_main_bg_color);

    if (bgColor != 0) {
      ll_root.setBackgroundColor(bgColor);
    }

    bgColor = ChangeThemeUtil.getAttrColorValue(getActivity().getTheme(), R.attr.main_bg_color);

    if (bgColor != 0) {
      ll_title.setBackgroundColor(bgColor);
      et_msg.setBackgroundColor(bgColor);
    }

    int textColor =
        ChangeThemeUtil.getAttrColorValue(getActivity().getTheme(), R.attr.text_color_1);

    if (textColor != 0) {
      ChangeThemeUtil.ChangeViewTextColor(ll_root, textColor);
    }

    int hintColor =
        ChangeThemeUtil.getAttrColorValue(getActivity().getTheme(), R.attr.text_color_2);

    if (hintColor != 0) {
      ChangeThemeUtil.ChangeETHintColor(ll_root, hintColor);
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_submit, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_submit:
        submit();
        break;
      default:
        break;
    }
    return true;
  }
}
