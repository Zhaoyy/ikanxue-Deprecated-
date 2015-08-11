package com.mislead.ikanxue.app.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.mislead.circleimageview.lib.CircleImageView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.activity.LoginActivity;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.FragmentHelper;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * NavigationDrawerFragment
 *
 * @author Mislead
 *         DATE: 2015/7/4
 *         DESC:
 **/
public class NavigationDrawerFragment extends BaseFragment implements View.OnClickListener {

  private static String TAG = "NavigationDrawerFragment";

  private ActionBarDrawerToggle mDrawerToggle;

  private DrawerLayout mDrawerLayout;

  private View mFragmentContainerView;

  private CircleImageView ivHead;
  private TextView tvName;
  private LinearLayout userInfo;

  private RadioGroup rg;
  private RadioButton rbtn_new_topic;
  private RadioButton rbtn_titles;
  private RadioButton rbtn_news;
  private RadioButton rbtn_feed_back;
  private RadioButton rbtn_about;

  private Api api = Api.getInstance();

  private List<RadioButton> rbtns = new ArrayList<>();

  private BroadcastReceiver logReciever = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      showUserInfo();
    }
  };

  private DrawerMenuListener drawerMenuListener;

  private int n = 0;

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

    ivHead = (CircleImageView) view.findViewById(R.id.circleIcon);
    tvName = (TextView) view.findViewById(R.id.name);

    userInfo = (LinearLayout) view.findViewById(R.id.userInfo);
    userInfo.setOnClickListener(this);
    view.findViewById(R.id.ll_exit).setOnClickListener(this);

    rg = (RadioGroup) view.findViewById(R.id.rg);
    rbtn_new_topic = (RadioButton) view.findViewById(R.id.rbtn_new_topic);
    rbtn_titles = (RadioButton) view.findViewById(R.id.rbtn_titles);
    rbtn_news = (RadioButton) view.findViewById(R.id.rbtn_news);
    rbtn_feed_back = (RadioButton) view.findViewById(R.id.rbtn_feed_back);
    rbtn_about = (RadioButton) view.findViewById(R.id.rbtn_about);

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

    drawable = getResources().getDrawable(R.mipmap.ic_feed);
    drawable.setBounds(0, 0, px, px);
    rbtn_feed_back.setCompoundDrawables(drawable, null, null, null);

    drawable = getResources().getDrawable(R.mipmap.ic_about);
    drawable.setBounds(0, 0, px, px);
    rbtn_about.setCompoundDrawables(drawable, null, null, null);

    for (int i = 0; i < rg.getChildCount(); i++) {
      View v = rg.getChildAt(i);

      if (v instanceof RadioButton) {
        rbtns.add((RadioButton) v);
        v.setOnClickListener(this);
      }
    }
  }

  private void showUserInfo() {
    if (api.isLogin()) {

      tvName.setText(api.getLoginUserName());

      if (api.getIsAvatar() > 0) {
        String headPic = api.getUserHeadImageUrl(api.getLoginUserId());

        VolleyHelper.requestImageWithCache(headPic, ivHead, AndroidHelper.getImageDiskCache(),
            R.mipmap.ic_launcher, R.mipmap.ic_launcher);
      }
    } else {
      tvName.setText(getResources().getString(R.string.gust_user));
      ivHead.setImageResource(R.mipmap.ic_launcher);
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

            actionBar.setTitle(((BaseFragment) FragmentHelper.getCurrentFragment()).getTitle());

            invalidateOptionsMenu();
          }

          @Override public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);

            if (!isAdded()) {
              return;
            }
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
      case R.id.rbtn_new_topic:
        itemSelected(0);
        break;
      case R.id.rbtn_titles:
        itemSelected(1);
        break;
      case R.id.rbtn_news:
        itemSelected(2);
        break;
      case R.id.rbtn_feed_back:

        if (api.isLogin()) {
          itemSelected(3);
        } else {
          ToastHelper.toastLong(getActivity(), "请先登录再进行操作！");
          loginOrLogout();
        }

        break;
      case R.id.rbtn_about:
        itemSelected(4);
        break;
      default:
        break;
    }
  }

  private void loginOrLogout() {
    if (api.isLogin()) {

      if (drawerMenuListener != null) {
        n = 5;
        drawerMenuListener.selectedAt(n);
      }
    } else {

      mDrawerLayout.closeDrawers();

      getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
    }
  }

  private void itemSelected(int i) {
    n = i;
    if (drawerMenuListener != null) {
      drawerMenuListener.selectedAt(n);
    }
  }

  public interface DrawerMenuListener {
    void selectedAt(int pos);
  }
}
