package com.mislead.ikanxue.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.base.SwipeBackActivity;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.ShPreUtil;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.view.ThreadTypePopup;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * PostNewThreadActivity
 *
 * @author Mislead
 *         DATE: 2015/9/25
 *         DESC:
 **/
public class PostNewThreadActivity extends SwipeBackActivity {

  private static String TAG = "PostNewThreadActivity";
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
                Toast.makeText(PostNewThreadActivity.this, R.string.new_post_success,
                    Toast.LENGTH_SHORT).show();
                break;
              case Api.NEW_POST_FAIL_WITHIN_THIRTY_SECONDS:
                Toast.makeText(PostNewThreadActivity.this,
                    R.string.new_post_fail_within_thirty_seconds, Toast.LENGTH_SHORT).show();
                return;
              case Api.NEW_POST_FAIL_WITHIN_FIVE_MINUTES:
                Toast.makeText(PostNewThreadActivity.this,
                    R.string.new_post_fail_within_five_minutes, Toast.LENGTH_SHORT).show();
                return;
              case Api.NEW_POST_FAIL_NOT_ENOUGH_KX:
                Toast.makeText(PostNewThreadActivity.this, R.string.new_post_fail_not_enough_kx,
                    Toast.LENGTH_SHORT).show();
                return;
              default:
                break;
            }
            Intent intent = new Intent();
            intent.putExtra("subject", subject);
            int id = jsonObject.getInt("threadid");
            intent.putExtra("threadid", id);

            setResult(502, intent);
          } catch (JSONException e) {
            LogHelper.e(e.toString());
          }
        }
      };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int theme_id = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);

    setTheme(theme_id);
    setContentView(R.layout.activity_new_thread);
    setTitle(R.string.post_new_thread);
    setIbtnRightImage(R.mipmap.social_send_now);
    ibtnRight.setVisibility(View.VISIBLE);
    et_title = (EditText) findViewById(R.id.et_title);
    et_kx = (EditText) findViewById(R.id.et_kx);
    et_msg = (EditText) findViewById(R.id.et_msg);
    ch_type = (CheckBox) findViewById(R.id.ch_type);
    ll_kx = (LinearLayout) findViewById(R.id.ll_kx);

    popup = new ThreadTypePopup(this);
    popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    popup.setHeight(AndroidHelper.dp2px(this, 116));
    popup.setAnimationStyle(R.style.mypopwindow_anim_style);
    popup.setSelectedListener(new ThreadTypePopup.TypeSelectedListener() {
      @Override public void selected(String s) {
        ch_type.setText(s);
        type = s;
      }
    });
    popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override public void onDismiss() {
        ch_type.setChecked(false);
      }
    });
    ch_type.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        LogHelper.e("show:" + popup.isShowing() + " checked:" + ch_type.isChecked());
        if (!popup.isShowing()) {
          popup.showAsDropDown(ch_type, 0, 32);
        } else {
          popup.dismiss();
        }
      }
    });

    ch_type.setChecked(false);

    id = getIntent().getIntExtra("id", 0);

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
      ToastHelper.toastShort(this, "标题不能为空！");
      return false;
    }

    if (id == Api.HELP_FORUM_ID) {

      if (TextUtils.isEmpty(et_kx.getText().toString())) {
        ToastHelper.toastShort(this, "悬赏金额不能为空！");
        return false;
      }

      int kxNum = Integer.decode(et_kx.getText().toString());

      if (kxNum < 10 || kxNum > 100) {
        ToastHelper.toastShort(this, "悬赏金额超出限制（10-100）");
        return false;
      }
    }

    if (id != Api.GET_JOB_FORUM_ID && TextUtils.isEmpty(type)) {
      ToastHelper.toastShort(this, "请选择话题类型！");
      return false;
    }

    String message = et_msg.getText().toString();
    if (TextUtils.isEmpty(message)) {
      ToastHelper.toastShort(this, "内容不能为空！");
      return false;
    }

    if (message.length() < Api.POST_CONTENT_SIZE_MIN) {
      ToastHelper.toastShort(this, "内容长度不能少于6个字！");
      return false;
    }

    return true;
  }

  @Override public void onBackPressed() {

    if (popup.isShowing()) {
      popup.dismiss();
      return;
    }

    super.onBackPressed();
  }

  @Override protected void ibtnLeftClicked() {
    super.onBackPressed();
  }

  @Override protected void ibtnRightClicked() {
    postThread();
  }
}
