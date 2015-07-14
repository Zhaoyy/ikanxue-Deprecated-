package com.mislead.ikanxue.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * UserInfoFragment
 *
 * @author Mislead
 *         DATE: 2015/7/9
 *         DESC:
 **/
public class UserInfoFragment extends BaseFragment {

  private static String TAG = "UserInfoFragment";

  private ImageView iv_head;
  private TextView tv_name;
  private TextView tv_type;
  private TextView tv_money;
  private TextView tv_posts;
  private TextView tv_goodness;
  private TextView btn_logout;

  private Api api;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    title = getString(R.string.user_info);
    return inflater.inflate(R.layout.fragment_user_info, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    iv_head = (ImageView) view.findViewById(R.id.iv_head);
    tv_name = (TextView) view.findViewById(R.id.tv_name);
    tv_type = (TextView) view.findViewById(R.id.tv_type);
    tv_money = (TextView) view.findViewById(R.id.tv_money);
    tv_posts = (TextView) view.findViewById(R.id.tv_posts);
    tv_goodness = (TextView) view.findViewById(R.id.tv_goodness);
    btn_logout = (TextView) view.findViewById(R.id.btn_logout);
    btn_logout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        showDialog();
      }
    });
    api = Api.getInstance();
    api.getUserInfoPage(api.getLoginUserId(), new VolleyHelper.ResponseListener<JSONObject>() {
      @Override public void onErrorResponse(VolleyError volleyError) {
        ToastHelper.toastShort(UserInfoFragment.this.getActivity(), "获取用户信息失败！");
      }

      @Override public void onResponse(JSONObject jsonObject) {
        showUserInfo(jsonObject);
      }
    });
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  private void showUserInfo(JSONObject object) {
    // show user head pic
    if (api.getIsAvatar() > 0) {
      String headPic = api.getUserHeadImageUrl(api.getLoginUserId());

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

  private void showDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity).setTitle("提示")
        .setMessage("确定要退出当前登录账号？")
        .setPositiveButton("退出", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            logout();
          }
        })
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    builder.show();
  }

  private void logout() {
    api.logout(new VolleyHelper.ResponseListener<JSONObject>() {
      @Override public void onErrorResponse(VolleyError volleyError) {
        ToastHelper.toastShort(getActivity(), volleyError.toString());
      }

      @Override public void onResponse(JSONObject object) {
        try {
          if (object.getInt("result") == 0) {
            Api.getInstance().clearLoginData();
            getActivity().sendBroadcast(new Intent(MyApplication.LOGIN_STATE_CHANGE_ACTION));
            ToastHelper.toastShort(getActivity(), "退出登录成功！");
            mainActivity.backToFragment(true);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
