package com.mislead.ikanxue.app.test;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.util.AndroidHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * TestActivity
 *
 * @author Mislead
 *         DATE: 2015/7/14
 *         DESC:
 **/
public class TestActivity extends AppCompatActivity {

  private static String TAG = "TestActivity";

  private SwipeRefreshLayout swipelayout;
  private ListView list;

  private List<String> data = new ArrayList<>();
  private ArrayAdapter<String> arrayAdapter;

  private int n = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_test);

    swipelayout = (SwipeRefreshLayout) findViewById(R.id.swipelayout);
    list = (ListView) findViewById(R.id.list);
    swipelayout.setColorSchemeColors(getResources().getColor(R.color.ics_red_dark),
        getResources().getColor(R.color.ics_orange_dark),
        getResources().getColor(R.color.ics_blue_dark),
        getResources().getColor(R.color.ics_purple_dark));

    arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);

    list.setAdapter(arrayAdapter);

    refresh();

    swipelayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        refresh();
      }
    });
  }

  class RefreshThread extends Thread {
    @Override public void run() {
      AndroidHelper.sleep(2000);

      for (int i = 0; i < 10; i++) {
        n++;
        data.add("item:" + n);
      }

      TestActivity.this.runOnUiThread(new Runnable() {
        @Override public void run() {
          swipelayout.setRefreshing(false);
          arrayAdapter.notifyDataSetChanged();
        }
      });
    }
  }

  private void refresh() {
    new RefreshThread().start();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_post_refresh, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_refresh:
        swipelayout.setRefreshing(true);
        refresh();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
