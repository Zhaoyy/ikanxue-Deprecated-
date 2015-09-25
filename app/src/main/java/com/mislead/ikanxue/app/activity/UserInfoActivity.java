package com.mislead.ikanxue.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.mislead.circleimageview.lib.CircleImageView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.base.SwipeBackActivity;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.ShPreUtil;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * UserInfoActivity
 *
 * @author Mislead
 *         DATE: 2015/9/25
 *         DESC:
 **/
public class UserInfoActivity extends SwipeBackActivity {

  private CircleImageView iv_head;
  private TextView tv_name;
  private TextView tv_type;
  private TextView tv_money;
  private TextView tv_posts;
  private TextView tv_goodness;
  private TextView btn_logout;

  private int userId;

  private static String TAG = "UserInfoActivity";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int theme_id = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);

    setTheme(theme_id);
    setContentView(R.layout.activity_user_info);
    setTitle(R.string.user_info);
    iv_head = (CircleImageView) findViewById(R.id.iv_head);
    tv_name = (TextView) findViewById(R.id.tv_name);
    tv_type = (TextView) findViewById(R.id.tv_type);
    tv_money = (TextView) findViewById(R.id.tv_money);
    tv_posts = (TextView) findViewById(R.id.tv_posts);
    tv_goodness = (TextView) findViewById(R.id.tv_goodness);
    btn_logout = (TextView) findViewById(R.id.btn_logout);
    btn_logout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        logout();
      }
    });
    userId = getIntent().getIntExtra("userId", 0);
    if (userId == 0) {
      btn_logout.setVisibility(View.VISIBLE);
      userId = api.getLoginUserId();
    } else {
      btn_logout.setVisibility(View.GONE);
    }

    api.getUserInfoPage(userId, new VolleyHelper.ResponseListener<JSONObject>() {
      @Override public void onErrorResponse(VolleyError volleyError) {
        ToastHelper.toastShort(UserInfoActivity.this, "获取用户信息失败！");
      }

      @Override public void onResponse(JSONObject jsonObject) {
        showUserInfo(jsonObject);
      }
    });
  }

  private void showUserInfo(JSONObject object) {
    // show user head pic
    if (api.getIsAvatar() > 0) {
      String headPic = api.getUserHeadImageUrl(userId);

      VolleyHelper.requestImageWithCache(headPic, iv_head, AndroidHelper.getImageDiskCache(),
          R.mipmap.ic_launcher, R.mipmap.ic_launcher);
    }

    try {
      tv_name.setText(object.getString("username"));
      tv_type.setText(object.getString("usertitle"));
      tv_money.setText(String.format("%s Kx", object.getString("money")));
      tv_posts.setText(object.getString("posts"));
      tv_goodness.setText(object.getString("goodnees"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void logout() {
    api.logout(new VolleyHelper.ResponseListener<JSONObject>() {
      @Override public void onErrorResponse(VolleyError volleyError) {
        ToastHelper.toastShort(UserInfoActivity.this, volleyError.toString());
      }

      @Override public void onResponse(JSONObject object) {
        try {
          if (object.getInt("result") == 0) {
            Api.getInstance().clearLoginData();
            sendBroadcast(new Intent(MyApplication.LOGIN_STATE_CHANGE_ACTION));
            ToastHelper.toastShort(UserInfoActivity.this, "退出登录成功！");
            onBackPressed();
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override protected void ibtnLeftClicked() {
    onBackPressed();
  }
}
