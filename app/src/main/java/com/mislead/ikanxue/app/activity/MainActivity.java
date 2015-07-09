package com.mislead.ikanxue.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.fragment.AboutFragment;
import com.mislead.ikanxue.app.fragment.FeedbackFragment;
import com.mislead.ikanxue.app.fragment.NavigationDrawerFragment;
import com.mislead.ikanxue.app.fragment.NewContentFragment;
import com.mislead.ikanxue.app.fragment.SecurityNewsFragment;
import com.mislead.ikanxue.app.fragment.TitlesFragment;
import com.mislead.ikanxue.app.util.FragmentHelper;
import com.mislead.ikanxue.app.util.ToastHelper;

public class MainActivity extends AppCompatActivity {

  private DrawerLayout drawerLayout;


  private NavigationDrawerFragment navigationDrawerFragment;

  private BroadcastReceiver logReciever = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (navigationDrawerFragment.isDrawerOpen()) {
        drawerLayout.closeDrawers();
      }
    }
  };

  //private Stack<Fragment> fragments = new Stack<>();

  private NavigationDrawerFragment.DrawerMenuListener listener =
      new NavigationDrawerFragment.DrawerMenuListener() {
        @Override public void selectedAt(int pos) {
          drawerLayout.closeDrawers();
          Fragment fragment = new AboutFragment();
          switch (pos) {
            case 0:
              fragment = new NewContentFragment();
              break;
            case 1:
              fragment = new TitlesFragment();
              break;
            case 2:
              fragment = new SecurityNewsFragment();
              break;
            case 3:
              fragment = new FeedbackFragment();
              break;
            case 4:
              fragment = new AboutFragment();
              break;
            default:
              break;
          }

          gotoFragment(fragment, getResources().getString(Constants.titleIDs[pos]));
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

    // goto new content fragment firstly
    gotoFragment(new NewContentFragment(), getResources().getString(Constants.titleIDs[0]));

    IntentFilter filter = new IntentFilter(MyApplication.LOGIN_STATE_CHANGE_ACTION);
    registerReceiver(logReciever, filter);
  }

  @Override protected void onDestroy() {
    unregisterReceiver(logReciever);
    super.onDestroy();
  }

  @Override public void onBackPressed() {
    //if (fragments.size() > 1) {
    //  backtoFragment();
    //} else {
    //  exitApp();
    //}
    exitApp();
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

  private void gotoFragment(Fragment fragment, String title) {
    Fragment fg = FragmentHelper.getCurrentFragment();

    if (fg != null) {
      String current = FragmentHelper.getFragmentTag(fg);
      String tag = FragmentHelper.getFragmentTag(fragment);

      if (current.equals(tag)) return;
      FragmentHelper.hideFragment(fg);
    }

    //fragments.push(fragment);
    FragmentHelper.showFragment(fragment, R.id.container);
    getSupportActionBar().setTitle(title);
  }

  //private void backtoFragment() {
  //  Fragment fragment = fragments.pop();
  //  FragmentHelper.hideFragment(fragment);
  //
  //  FragmentHelper.showFragment(fragments.peek(), R.id.container);
  //}
}
