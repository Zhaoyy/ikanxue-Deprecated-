package com.mislead.ikanxue.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.activity.LoginActivity;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;

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

  private Api api = Api.getInstance();

  private BroadcastReceiver logReciever = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      showUserInfo();
    }
  };

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
    ActionBar actionBar = activity.getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setHomeButtonEnabled(true);

    mDrawerToggle =
        new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.string.empty, R.string.empty) {
          @Override public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            if (!isAdded()) {
              return;
            }
            invalidateOptionsMenu();
          }

          @Override public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);

            if (!isAdded()) {
              return;
            }

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
      default:
        break;
    }
  }

  private void loginOrLogout() {
    if (api.isLogin()) {
      ToastHelper.toastShort(getActivity(), "当前登录用户为：" + api.getLoginUserName());
    } else {
      getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
    }

  }
}
