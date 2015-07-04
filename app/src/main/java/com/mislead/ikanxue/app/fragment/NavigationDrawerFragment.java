package com.mislead.ikanxue.app.fragment;

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
import com.mislead.ikanxue.app.R;

/**
 * NavigationDrawerFragment
 *
 * @author Mislead
 *         DATE: 2015/7/4
 *         DESC:
 **/
public class NavigationDrawerFragment extends Fragment {

  private static String TAG = "NavigationDrawerFragment";

  private ActionBarDrawerToggle mDrawerToggle;

  private DrawerLayout mDrawerLayout;

  private View mFragmentContainerView;

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
}
