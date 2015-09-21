package com.mislead.ikanxue.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.model.DailyEnglishObject;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.DailyEnglishUtil;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;

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

  private boolean hasPost = false;

  private Runnable runnable = new Runnable() {
    @Override public void run() {
      if (hasPost) return;
      hasPost = true;
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

    findViewById(R.id.rl_root).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        tvDailyEn.postDelayed(runnable, 1000);
      }
    });

    setDailyEnglish();

    tvDailyEn.postDelayed(runnable, 4 * 1000);
  }

  @Override public void onBackPressed() {
    tvDailyEn.post(runnable);
  }

  private void setDailyEnglish() {

    DailyEnglishObject dailyEnglishObject = new DailyEnglishUtil().GetDailyEnglish();

    if (dailyEnglishObject == null) {
      String content = getResources().getString(R.string.default_daily_en);
      tvDailyEn.setText(content);
      String note = getResources().getString(R.string.default_daily_zh);
      tvDailyZh.setText(note);
      ivDaily.setImageResource(R.mipmap.daily);
    } else {
      tvDailyZh.setText(dailyEnglishObject.getNote());
      tvDailyEn.setText(dailyEnglishObject.getContent());

      String key = VolleyHelper.getCacheKey(dailyEnglishObject.getPicture());
      Bitmap bitmap = AndroidHelper.getSplashImageCache().getBitmap(key);
      if (bitmap != null) {
        ivDaily.setImageBitmap(bitmap);
      } else {
        ivDaily.setImageResource(R.mipmap.daily);
      }

      //VolleyHelper.requestImageWithCacheSimple(dailyEnglish.getPicture(),
      //    AndroidHelper.getImageDiskCache(), new FadeInImageListener());
    }
  }

  class FadeInImageListener implements ImageLoader.ImageListener {

    @Override public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
      Bitmap bitmap = imageContainer.getBitmap();
      LogHelper.e("onResponse : " + b);
      if (bitmap != null) {
        ivDaily.setImageBitmap(bitmap);
      } else {
        ivDaily.setImageResource(R.mipmap.daily);
      }
    }

    @Override public void onErrorResponse(VolleyError volleyError) {
      LogHelper.e("onErrorResponse");
      ivDaily.setImageResource(R.mipmap.daily);
    }
  }
}
