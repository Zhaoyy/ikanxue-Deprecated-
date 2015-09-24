package com.mislead.ikanxue.app.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.util.LogHelper;

/**
 * BaseActivity
 *
 * @author Mislead
 *         DATE: 2015/9/24
 *         DESC:
 **/
public class ToolbarActivity extends AppCompatActivity {

  private static String TAG = "BaseActivity";
  private Toolbar toolbar;
  private Button btnLeft;
  private TextView tvTitle;
  private ImageButton ibtnLeft;
  private ImageButton ibtnRight;
  private BroadcastReceiver logReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      onLoginOrLogout();
    }
  };
  private BroadcastReceiver themeReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      changeTheme();
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    IntentFilter logFilter = new IntentFilter(MyApplication.LOGIN_STATE_CHANGE_ACTION);
    IntentFilter themeFilter = new IntentFilter(MyApplication.THEME_CHANGE_ACTION);

    registerReceiver(logReceiver, logFilter);
    registerReceiver(themeReceiver, themeFilter);
  }

  @Override public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);

    toolbar = (Toolbar) findViewById(R.id.toolbar);

    if (toolbar != null) {
      toolbar.setContentInsetsRelative(0, 0);
      getLayoutInflater().inflate(R.layout.layout_toolbar, toolbar);

      setSupportActionBar(toolbar);
      btnLeft = (Button) toolbar.findViewById(R.id.btn_left);
      ibtnLeft = (ImageButton) toolbar.findViewById(R.id.ibtn_left);
      tvTitle = (TextView) toolbar.findViewById(R.id.tv_title);
      ibtnRight = (ImageButton) toolbar.findViewById(R.id.ibtn_right);
    }
    LogHelper.e("tvTitle is null:" + (tvTitle == null));
  }

  @Override public void setTitle(CharSequence title) {
    if (tvTitle == null) {
      super.setTitle(title);
    } else {
      tvTitle.setText(title);
    }
  }

  @Override public void setTitle(int titleId) {
    if (tvTitle == null) {
      super.setTitle(titleId);
    } else {
      tvTitle.setText(titleId);
    }
  }

  @Override protected void onDestroy() {
    unregisterReceiver(logReceiver);
    unregisterReceiver(themeReceiver);
    super.onDestroy();
  }

  // do someting when user log state changed.
  protected void onLoginOrLogout() {
  }

  // change theme
  protected void changeTheme() {
  }
}
