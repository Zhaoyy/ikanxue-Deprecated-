package com.mislead.ikanxue.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.activity.LoginActivity;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * NavigationDrawerFragment
 *
 * @author Mislead
 *         DATE: 2015/7/4
 *         DESC:
 **/
public class NavigationDrawerFragment extends Fragment implements View.OnClickListener {

  private static String TAG = "NavigationDrawerFragment";

  private ActionBarDrawerToggle mDrawerToggle;

  private DrawerLayout mDrawerLayout;

  private View mFragmentContainerView;

  private ImageView ivHead;
  private TextView tvName;
  private LinearLayout userInfo;
  private LinearLayout llExit;

  private RadioGroup rg;
  private RadioButton rbtn_new_topic;
  private RadioButton rbtn_titles;
  private RadioButton rbtn_news;

  private Api api = Api.getInstance();

  private BroadcastReceiver logReciever = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      showUserInfo();
    }
  };

  private DrawerMenuListener drawerMenuListener;

  private String title;

  public void setDrawerMenuListener(DrawerMenuListener drawerMenuListener) {
    this.drawerMenuListener = drawerMenuListener;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_navigation_drawer, container);
  }

  // 4.1系统bug，setHasOptionsMenu(true) 如果放在 onCreate 中会报错
  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ivHead = (ImageView) view.findViewById(R.id.circleIcon);
    tvName = (TextView) view.findViewById(R.id.name);

    userInfo = (LinearLayout) view.findViewById(R.id.userInfo);
    userInfo.setOnClickListener(this);
    view.findViewById(R.id.ll_exit).setOnClickListener(this);
    view.findViewById(R.id.ll_about).setOnClickListener(this);
    view.findViewById(R.id.ll_feed_back).setOnClickListener(this);

    rg = (RadioGroup) view.findViewById(R.id.rg);
    rbtn_new_topic = (RadioButton) view.findViewById(R.id.rbtn_new_topic);
    rbtn_titles = (RadioButton) view.findViewById(R.id.rbtn_titles);
    rbtn_news = (RadioButton) view.findViewById(R.id.rbtn_news);

    initRbtn();

    showUserInfo();
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    IntentFilter filter = new IntentFilter(MyApplication.LOGIN_STATE_CHANGE_ACTION);

    getActivity().registerReceiver(logReciever, filter);
  }

  @Override public void onDetach() {
    super.onDetach();
    getActivity().unregisterReceiver(logReciever);
  }

  private void initRbtn() {
    int px = AndroidHelper.dp2px(getActivity(), 24);

    Drawable drawable = getResources().getDrawable(R.mipmap.ic_content);
    drawable.setBounds(0, 0, px, px);
    rbtn_new_topic.setCompoundDrawables(drawable, null, null, null);

    drawable = getResources().getDrawable(R.mipmap.ic_menu);
    drawable.setBounds(0, 0, px, px);
    rbtn_titles.setCompoundDrawables(drawable, null, null, null);

    drawable = getResources().getDrawable(R.mipmap.ic_news);
    drawable.setBounds(0, 0, px, px);
    rbtn_news.setCompoundDrawables(drawable, null, null, null);
  }

  private void showUserInfo() {
    if (api.isLogin()) {

      tvName.setText(api.getLoginUserName());

      if (api.getIsAvatar() > 0) {
        String headPic = api.getUserHeadImageUrl(api.getLoginUserId());

        VolleyHelper.requestImageWithCache(headPic, ivHead, AndroidHelper.getImageDiskCache(),
            R.mipmap.ic_lancher, R.mipmap.ic_lancher);
      }
    } else {
      tvName.setText(getResources().getString(R.string.gust_user));
      ivHead.setImageResource(R.mipmap.ic_lancher);
    }
  }

  public void setUp(int fragmentId, DrawerLayout drawerLayout) {
    mFragmentContainerView = getActivity().findViewById(fragmentId);
    mDrawerLayout = drawerLayout;

    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

    AppCompatActivity activity = (AppCompatActivity) getActivity();
    final ActionBar actionBar = activity.getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setHomeButtonEnabled(true);

    mDrawerToggle =
        new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.string.empty, R.string.empty) {
          @Override public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            if (!isAdded()) {
              return;
            }

            if (!TextUtils.isEmpty(title)) {
              actionBar.setTitle(title);
            }

            invalidateOptionsMenu();
          }

          @Override public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);

            if (!isAdded()) {
              return;
            }
            title = actionBar.getTitle().toString();
            actionBar.setTitle(getResources().getString(R.string.app_name));
            invalidateOptionsMenu();
          }
        };

    mDrawerLayout.post(new Runnable() {
      @Override public void run() {
        mDrawerToggle.syncState();
      }
    });

    mDrawerLayout.setDrawerListener(mDrawerToggle);
  }

  private void invalidateOptionsMenu() {
    getActivity().invalidateOptionsMenu();
  }

  public boolean isDrawerOpen() {
    return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
  }

  private void showGlobalContextActionBar() {
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    ActionBar actionBar = activity.getSupportActionBar();
    actionBar.setDisplayShowTitleEnabled(true);
    actionBar.setDisplayShowCustomEnabled(false);
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    if (mDrawerLayout != null && isDrawerOpen()) {
      showGlobalContextActionBar();
    }
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.userInfo:
        loginOrLogout();
        break;
      case R.id.ll_exit:
        getActivity().finish();
        break;
      case R.id.ll_about:

        if (drawerMenuListener != null) {
          drawerMenuListener.selectedAt(4);
        }

        break;
      case R.id.ll_feed_back:
        if (drawerMenuListener != null) {
          drawerMenuListener.selectedAt(3);
        }
        break;
      default:
        break;
    }
  }

  private void loginOrLogout() {
    if (api.isLogin()) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle("退出登录")
          .setMessage("确认要退出当前登录？")
          .setCancelable(true)
          .setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
              Api.getInstance().logout(new VolleyHelper.ResponseListener<JSONObject>() {
                @Override public void onErrorResponse(VolleyError volleyError) {
                  ToastHelper.toastShort(getActivity(), volleyError.toString());
                }

                @Override public void onResponse(JSONObject jsonObject) {
                  LogHelper.e(jsonObject.toString());
                  try {
                    if (jsonObject.getInt("result") == 0) {
                      Api.getInstance().clearLoginData();
                      getActivity().sendBroadcast(
                          new Intent(MyApplication.LOGIN_STATE_CHANGE_ACTION));
                      ToastHelper.toastShort(getActivity(), "退出登录成功！");
                    }
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                }
              });
            }
          })
          .setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          });

      builder.show();
    } else {
      getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
    }
  }

  public interface DrawerMenuListener {
    public void selectedAt(int pos);
  }
}
