package com.mislead.ikanxue.app.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.fragment.NavigationDrawerFragment;
import com.mislead.ikanxue.app.net.HttpClientUtil;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.util.List;
import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private TextView tvHello;
  private ImageView iv;

  private DrawerLayout drawerLayout;

  private NavigationDrawerFragment navigationDrawerFragment;

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
  }

  private void loginClick(View v) {
    Api.getInstance().login("winz", "kanxue153729", new HttpClientUtil.NetClientCallback() {
      @Override public void execute(int status, String response, List<Cookie> cookies) {
        if (status == HttpClientUtil.NET_SUCCESS) {
          final JSONObject retObj;
          LogHelper.e(response);
          try {
            retObj = new JSONObject(response);
            final int ret = retObj.getInt("result");
            if (ret != Api.LOGIN_SUCCESS) {
              switch (ret) {
                case Api.LOGIN_FAIL_LESS_THAN_FIVE:
                  String alertText = "用户名或者密码错误,还有" + (Api.ALLOW_LOGIN_USERNAME_OR_PASSWD_ERROR_NUM
                      - retObj.getInt("strikes")) + "尝试机会";
                  Toast.makeText(MainActivity.this, alertText, Toast.LENGTH_SHORT).show();
                  break;
                case Api.LOGIN_FAIL_MORE_THAN_FIVE:
                  Toast.makeText(MainActivity.this, R.string.login_fail_more_than_five,
                      Toast.LENGTH_SHORT).show();
                  break;
              }
              return;
            }
            String token = retObj.getString("securitytoken");
            Api.getInstance().setToken(token);
            Api.getInstance()
                .setLoginUserInfo(retObj.getString("username"), retObj.getInt("userid"),
                    retObj.getInt("isavatar"), retObj.getString("email"));

            for (int i = 0; i < cookies.size(); i++) {
              Cookie cookie = cookies.get(i);
              Api.getInstance().getCookieStorage().addCookie(cookie.getName(), cookie.getValue());
            }
            //MainActivity.this.sendBroadcast(new Intent(
            //    App.LOGIN_STATE_CHANGE_ACTION));
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      }
    });
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
}
