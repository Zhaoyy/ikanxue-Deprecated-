package com.mislead.ikanxue.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.view.SwipeBackLayout;

public class SwipeBackActivity extends ToolbarActivity {
  protected SwipeBackLayout layout;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
    layout.attachToActivity(this);
  }

  @Override public void startActivity(Intent intent) {
    super.startActivity(intent);
    overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
  }

  // Press the back button in mobile phone
  @Override public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(0, R.anim.base_slide_right_out);
  }
}
