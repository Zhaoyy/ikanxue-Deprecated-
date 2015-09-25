package com.mislead.ikanxue.app.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.mislead.circleimageview.lib.CircleImageView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.application.MyApplication;

/**
 * BaseActivity
 *
 * @author Mislead
 *         DATE: 2015/9/24
 *         DESC:
 **/
public class ToolbarActivity extends AppCompatActivity {

  private static String TAG = "BaseActivity";
  protected Toolbar toolbar;
  protected Button btnLeft;
  protected TextView tvTitle;
  protected CircleImageView ibtnLeft;
  protected ImageButton ibtnRight;

  protected Api api = Api.getInstance();
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

  private View.OnClickListener listener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      if (toolbar == null) return;
      switch (v.getId()) {
        case R.id.btn_left:
          btnLeftClicked();
          break;
        case R.id.ibtn_left:
          ibtnLeftClicked();
          break;
        case R.id.ibtn_right:
          ibtnRightClicked();
          break;
        default:
          break;
      }
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
      ibtnLeft = (CircleImageView) toolbar.findViewById(R.id.ibtn_left);
      tvTitle = (TextView) toolbar.findViewById(R.id.tv_title);
      ibtnRight = (ImageButton) toolbar.findViewById(R.id.ibtn_right);
      btnLeft.setOnClickListener(listener);
      ibtnLeft.setOnClickListener(listener);
      ibtnRight.setOnClickListener(listener);
    }
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

  // tool bar btn
  public void setBtnLeftText(String text) {
    if (toolbar != null) {
      btnLeft.setText(text);
      btnLeft.setVisibility(View.VISIBLE);
      ibtnLeft.setVisibility(View.GONE);
    }
  }

  public void setIbtnLeftImage(int resID) {
    if (toolbar != null) {
      ibtnLeft.setImageResource(resID);
      btnLeft.setVisibility(View.GONE);
      ibtnLeft.setVisibility(View.VISIBLE);
    }
  }

  public void setIbtnRightImage(int resID) {
    if (toolbar != null) {
      ibtnRight.setImageResource(resID);
    }
  }

  protected void btnLeftClicked() {
  }

  protected void ibtnLeftClicked() {
  }

  protected void ibtnRightClicked() {
  }


}
