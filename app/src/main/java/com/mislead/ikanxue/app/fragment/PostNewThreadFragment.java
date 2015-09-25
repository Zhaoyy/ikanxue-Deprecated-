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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.view.ThreadTypePopup;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import org.json.JSONException;
import org.json.JSONObject;

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
  private LinearLayout ll_kx;

  private String type = "";
  private String subject;

  private int id;

  private VolleyHelper.ResponseListener<String> listener =
      new VolleyHelper.ResponseListener<String>() {
        @Override public void onErrorResponse(VolleyError volleyError) {
          LogHelper.e(volleyError.toString());
        }

        @Override public void onResponse(String object) {
          try {
            LogHelper.e(object);
            JSONObject jsonObject = new JSONObject(object);

            int result = jsonObject.getInt("result");

            switch (result) {
              case Api.NEW_POST_SUCCESS:
                Toast.makeText(mainActivity, R.string.new_post_success, Toast.LENGTH_SHORT).show();
                break;
              case Api.NEW_POST_FAIL_WITHIN_THIRTY_SECONDS:
                Toast.makeText(mainActivity, R.string.new_post_fail_within_thirty_seconds,
                    Toast.LENGTH_SHORT).show();
                return;
              case Api.NEW_POST_FAIL_WITHIN_FIVE_MINUTES:
                Toast.makeText(mainActivity, R.string.new_post_fail_within_five_minutes,
                    Toast.LENGTH_SHORT).show();
                return;
              case Api.NEW_POST_FAIL_NOT_ENOUGH_KX:
                Toast.makeText(mainActivity, R.string.new_post_fail_not_enough_kx,
                    Toast.LENGTH_SHORT).show();
                return;
              default:
                break;
            }
            Bundle data = new Bundle();
            data.putString("subject", subject);
            int id = jsonObject.getInt("threadid");
            data.putInt("threadid", id);

            //mainActivity.backToFragment(true, data);
          } catch (JSONException e) {
            LogHelper.e(e.toString());
          }
        }
      };


  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    title = getString(R.string.post_new_thread);
    setHasOptionsMenu(true);
    return inflater.inflate(R.layout.fragment_new_thread, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    et_title = (EditText) view.findViewById(R.id.et_title);
    et_kx = (EditText) view.findViewById(R.id.et_kx);
    et_msg = (EditText) view.findViewById(R.id.et_msg);
    ch_type = (CheckBox) view.findViewById(R.id.ch_type);
    ll_kx = (LinearLayout) view.findViewById(R.id.ll_kx);

    popup = new ThreadTypePopup(getActivity());
    popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    popup.setHeight(AndroidHelper.dp2px(getActivity(), 116));
    popup.setAnimationStyle(R.style.mypopwindow_anim_style);
    popup.setSelectedListener(new ThreadTypePopup.TypeSelectedListener() {
      @Override public void selected(String s) {
        ch_type.setText(s);
        type = s;
      }
    });
    popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override public void onDismiss() {
        if (ch_type.isChecked()) {
          ch_type.setChecked(false);
        }
      }
    });
    ch_type.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (popup.isShowing()) {
          popup.dismiss();
        } else {
          popup.showAsDropDown(ch_type, 0, 32);
          if (!ch_type.isChecked()) ch_type.setChecked(true);
        }
      }
    });

    id = data.getInt("id");

    if (id == Api.HELP_FORUM_ID) {
      ll_kx.setVisibility(View.VISIBLE);
    } else {
      ll_kx.setVisibility(View.GONE);
    }

    if (id == Api.GET_JOB_FORUM_ID) {
      ch_type.setText("【招聘】");
      ch_type.setClickable(false);
    } else {
      ch_type.setClickable(true);
    }

  }

  private void postThread() {
    if (check()) {

      subject = ch_type.getText().toString() + et_title.getText().toString();
      String message = et_msg.getText().toString();
      String kxReward = et_kx.getText().toString();

      if (id == Api.HELP_FORUM_ID) {
        Api.getInstance().newThread(id, subject, message, kxReward, listener);
      } else {
        Api.getInstance().newThreadWithoutReward(id, subject, message, listener);
      }
    }
  }

  private boolean check() {

    if (TextUtils.isEmpty(et_title.getText().toString())) {
      ToastHelper.toastShort(mainActivity, "标题不能为空！");
      return false;
    }

    if (id == Api.HELP_FORUM_ID) {

      if (TextUtils.isEmpty(et_kx.getText().toString())) {
        ToastHelper.toastShort(mainActivity, "悬赏金额不能为空！");
        return false;
      }

      int kxNum = Integer.decode(et_kx.getText().toString());

      if (kxNum < 10 || kxNum > 100) {
        ToastHelper.toastShort(mainActivity, "悬赏金额超出限制（10-100）");
        return false;
      }
    }

    if (id != Api.GET_JOB_FORUM_ID && TextUtils.isEmpty(type)) {
      ToastHelper.toastShort(mainActivity, "请选择话题类型！");
      return false;
    }

    String message = et_msg.getText().toString();
    if (TextUtils.isEmpty(message)) {
      ToastHelper.toastShort(mainActivity, "内容不能为空！");
      return false;
    }

    if (message.length() < Api.POST_CONTENT_SIZE_MIN) {
      ToastHelper.toastShort(mainActivity, "内容长度不能少于6个字！");
      return false;
    }

    return true;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.menu_post, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_post:
        postThread();
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public boolean onBackPressed() {

    if (popup.isShowing()) {
      popup.dismiss();
      return true;
    }

    return super.onBackPressed();
  }
}
