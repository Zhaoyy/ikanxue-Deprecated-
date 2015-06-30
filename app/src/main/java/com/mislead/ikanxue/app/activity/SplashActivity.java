package com.mislead.ikanxue.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.model.DailyEnglish;
import com.mislead.ikanxue.app.util.DailyEnglishUtil;

/**
 * SplashActivity
 * AUTHOR:Zhaoyy  2015/6/27
 * DESC:
 **/
public class SplashActivity extends Activity {

  private static String TAG = "SplashActivity";
  private TextView tvDailyEn;
  private TextView tvDailyZh;
  private ImageView ivDaily;

  private Runnable runnable = new Runnable() {
    @Override public void run() {
      startActivity(new Intent(SplashActivity.this, MainActivity.class));
      SplashActivity.this.finish();
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    tvDailyEn = (TextView) findViewById(R.id.tv_daily_en);
    tvDailyZh = (TextView) findViewById(R.id.tv_daily_zh);
    ivDaily = (ImageView) findViewById(R.id.iv_daily);

    setDailyEnglish();

    tvDailyEn.postDelayed(runnable, 2000);
  }

  private void setDailyEnglish() {

    DailyEnglish dailyEnglish = new DailyEnglishUtil().prepareListener().GetDailyEnglish();

    if (dailyEnglish == null) {
      String content = getResources().getString(R.string.default_daily_en);
      tvDailyEn.setText(content);
      String note = getResources().getString(R.string.default_daily_zh);
      tvDailyZh.setText(note);
    } else {
      tvDailyZh.setText(dailyEnglish.getNote());
      tvDailyEn.setText(dailyEnglish.getContent());
    }

    ivDaily.setImageResource(R.drawable.daily);
  }
}
