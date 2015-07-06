package com.mislead.ikanxue.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.fragment.NavigationDrawerFragment;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private TextView tvHello;
  private ImageView iv;

  private DrawerLayout drawerLayout;

  private NavigationDrawerFragment navigationDrawerFragment;

  private BroadcastReceiver logReciever = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (navigationDrawerFragment.isDrawerOpen()) {
        drawerLayout.closeDrawers();
      }
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    tvHello = (TextView) findViewById(R.id.tv_hello);
    iv = (ImageView) findViewById(R.id.iv);
    drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
    navigationDrawerFragment =
        (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(
            R.id.navigation_drawer);

    navigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);

    findViewById(R.id.btn_login).setOnClickListener(this);
    findViewById(R.id.btn_logout).setOnClickListener(this);

    IntentFilter filter = new IntentFilter(MyApplication.LOGIN_STATE_CHANGE_ACTION);
    registerReceiver(logReciever, filter);
  }

  private void loginClick(View v) {

  }

  private void logout() {
    Api.getInstance().logout(new VolleyHelper.ResponseListener<JSONObject>() {
      @Override public void onErrorResponse(VolleyError volleyError) {

      }

      @Override public void onResponse(JSONObject jsonObject) {
        try {

          Api.getInstance().clearLoginData();
          LogHelper.e(jsonObject.getString("result"));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override protected void onDestroy() {
    unregisterReceiver(logReciever);
    super.onDestroy();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    //getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_login:
        loginClick(v);
        break;
      case R.id.btn_logout:
        logout();
        break;
      default:
        break;
    }
  }

  @Override public void onBackPressed() {
    exitApp();
  }

  private long exitTime = 0;

  private void exitApp() {
    if ((System.currentTimeMillis() - exitTime) > 800) {
      ToastHelper.toastShort(this, "再按一次退出i看雪");
      exitTime = System.currentTimeMillis();
    } else {
      finish();
    }
  }
}
