package com.mislead.ikanxue.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.fragment.AboutFragment;
import com.mislead.ikanxue.app.fragment.FeedbackFragment;
import com.mislead.ikanxue.app.fragment.ForumDisplayFragment;
import com.mislead.ikanxue.app.fragment.ForumTitlesFragment;
import com.mislead.ikanxue.app.fragment.NavigationDrawerFragment;
import com.mislead.ikanxue.app.fragment.UserInfoFragment;
import com.mislead.ikanxue.app.util.FragmentHelper;
import com.mislead.ikanxue.app.util.ToastHelper;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

  private DrawerLayout drawerLayout;

  private NavigationDrawerFragment navigationDrawerFragment;

  private BroadcastReceiver logReciever = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      closeDrawer();
    }
  };

  private Stack<Fragment> fragments = new Stack<>();

  private NavigationDrawerFragment.DrawerMenuListener listener =
      new NavigationDrawerFragment.DrawerMenuListener() {
        @Override public void selectedAt(int pos) {
          drawerLayout.closeDrawers();
          BaseFragment fragment = new AboutFragment();
          boolean dispose = true;
          Bundle bundle = new Bundle();
          switch (pos) {
            case 0:
              // 查看新帖
              fragment = new ForumDisplayFragment();
              bundle.putInt("id", Api.NEW_FORUM_ID);
              bundle.putString("title", getString(R.string.new_topic));
              fragment.setData(bundle);
              break;
            case 1:
              fragment = new ForumTitlesFragment();
              break;
            case 2:
              // 安全咨询
              fragment = new ForumDisplayFragment();
              bundle.putInt("id", Api.SECURITY_FORUM_ID);
              bundle.putString("title", getString(R.string.security_news));
              fragment.setData(bundle);
              break;
            case 3:
              fragment = new FeedbackFragment();
              dispose = false;
              break;
            case 4:
              fragment = new AboutFragment();
              break;
            case 5:
              fragment = new UserInfoFragment();
              dispose = false;
              break;
            default:
              break;
          }

          gotoFragment(fragment, dispose, dispose);
        }
      };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
    navigationDrawerFragment =
        (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(
            R.id.navigation_drawer);

    navigationDrawerFragment.setDrawerMenuListener(listener);

    navigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentHelper.init(fragmentManager);
    if (savedInstanceState == null) {
      // goto new content fragment firstly
      Bundle bundle = new Bundle();
      ForumDisplayFragment fragment = new ForumDisplayFragment();
      bundle.putInt("id", Api.NEW_FORUM_ID);
      bundle.putString("title", getString(R.string.new_topic));
      fragment.setData(bundle);
      gotoFragment(fragment, true);
    }

    IntentFilter filter = new IntentFilter(MyApplication.LOGIN_STATE_CHANGE_ACTION);
    registerReceiver(logReciever, filter);
  }

  @Override protected void onDestroy() {
    unregisterReceiver(logReciever);
    FragmentHelper.destroy();
    super.onDestroy();
  }

  @Override public void onBackPressed() {
    BaseFragment fragment = (BaseFragment) FragmentHelper.getCurrentFragment();
    if (fragment != null && fragment.onBackPressed()) {
      return;
    }
    if (!fragments.empty()) {
      backToFragment(true);
    } else {
      exitApp();
    }
  }

  public void closeDrawer() {
    if (navigationDrawerFragment.isDrawerOpen()) {
      drawerLayout.closeDrawers();
    }
  }

  private long exitTime = 0;

  private void exitApp() {
    if ((System.currentTimeMillis() - exitTime) > 2000) {
      ToastHelper.toastShort(this, "再按一次退出i看雪");
      exitTime = System.currentTimeMillis();
    } else {
      finish();
    }
  }

  public void gotoFragment(@NonNull BaseFragment fragment, boolean dispose) {
    gotoFragment(fragment, dispose, false);
  }

  public void gotoFragment(@NonNull BaseFragment fragment, boolean dispose, boolean clean) {
    Fragment fg = FragmentHelper.getCurrentFragment();

    if (fg != null) {
      String current = FragmentHelper.getFragmentTag(fg);
      String tag = FragmentHelper.getFragmentTag(fragment);

      if (current.equals(tag)) return;
      FragmentHelper.hideFragment(fg);

      if (dispose) {
        FragmentHelper.removeFragment(fg);
      } else {
        fragments.push(fg);
      }

      if (clean) {
        cleanFragments();
      }
    }
    FragmentHelper.showFragment(fragment, R.id.container);

    fragment.setTitle();
  }

  private void cleanFragments() {
    fragments.clear();
  }

  public void backToFragment(boolean dispose) {
    backToFragment(dispose, null);
  }

  public void backToFragment(boolean dispose, Bundle data) {
    Fragment current = FragmentHelper.getCurrentFragment();
    BaseFragment fragment = (BaseFragment) fragments.pop();
    FragmentHelper.hideFragment(current);

    if (dispose) {
      FragmentHelper.removeFragment(current);
    }

    FragmentHelper.showFragment(fragment, R.id.container);
    if (data != null) {
      fragment.setData(data);
      fragment.onRefresh();
    }
    fragment.setTitle();
  }
}
