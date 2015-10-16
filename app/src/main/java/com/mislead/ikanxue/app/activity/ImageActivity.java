package com.mislead.ikanxue.app.activity;

import android.content.Intent;
import android.os.Bundle;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.base.ToolbarActivity;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.ShPreUtil;
import com.mislead.ikanxue.app.view.MyImageView;
import com.mislead.ikanxue.app.volley.VolleyHelper;

/**
 * ImageActivity
 *
 * @author Mislead
 *         DATE: 2015/9/25
 *         DESC:
 **/
public class ImageActivity extends ToolbarActivity {

  private static String TAG = "ImageActivity";
  private MyImageView imageView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int theme_id = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);

    setTheme(theme_id);
    setContentView(R.layout.activity_image);
    imageView = (MyImageView) findViewById(R.id.imageView);

    Intent intent = getIntent();
    String titile = intent.getStringExtra("title");
    String url = intent.getStringExtra("url");
    setTitle(titile);
    VolleyHelper.requestImageWithCache(url, imageView, AndroidHelper.getImageDiskCache(),
        R.mipmap.image_404, R.mipmap.image_404);
  }

  @Override protected void ibtnLeftClicked() {
    onBackPressed();
  }
}
