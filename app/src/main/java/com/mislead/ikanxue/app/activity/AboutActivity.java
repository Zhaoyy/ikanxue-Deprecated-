package com.mislead.ikanxue.app.activity;

import android.os.Bundle;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.base.SwipeBackActivity;
import com.mislead.ikanxue.app.util.ShPreUtil;

/**
 * AboutActivity
 *
 * @author Mislead
 *         DATE: 2015/9/25
 *         DESC:
 **/
public class AboutActivity extends SwipeBackActivity {

  private static String TAG = "AboutActivity";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int theme_id = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);

    setTheme(theme_id);
    setContentView(R.layout.activity_about);
    setTitle(R.string.about);
  }

  @Override protected void ibtnLeftClicked() {
    onBackPressed();
  }
}
