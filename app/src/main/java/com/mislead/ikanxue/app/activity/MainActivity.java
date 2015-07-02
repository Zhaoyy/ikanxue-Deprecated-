package com.mislead.ikanxue.app.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.DailyEnglishUtil;
import com.mislead.ikanxue.app.util.ShPreUtil;
import com.mislead.ikanxue.app.util.VolleyHelper;

public class MainActivity extends AppCompatActivity {

  private TextView tvHello;
  private ImageView iv;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    tvHello = (TextView) findViewById(R.id.tv_hello);
    iv = (ImageView) findViewById(R.id.iv);

    tvHello.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String string = ShPreUtil.getString(DailyEnglishUtil.SH_LAST_DAILY_PIC_URL);
        VolleyHelper.requestImageWithCacheSimple(string, AndroidHelper.getImageDiskCache(), null);
      }
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
