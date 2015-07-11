package com.mislead.ikanxue.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.net.HttpClientUtil;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.ToastHelper;
import java.util.List;
import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * LoginActivity
 *
 * @author Mislead
 *         DATE: 2015/7/6
 *         DESC:
 **/
public class LoginActivity extends AppCompatActivity {

  private static String TAG = "LoginActivity";

  private EditText etName;
  private EditText etPwd;

  private Button btnLogin;

  private Handler mHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      AndroidHelper.hideProgressDialog();
      switch (msg.what) {
        case HttpClientUtil.NET_SUCCESS:
          ToastHelper.toastShort(LoginActivity.this, "登录成功！");
          LoginActivity.this.finish();
          break;
        case HttpClientUtil.NET_TIMEOUT:
          ToastHelper.toastShort(LoginActivity.this, "网络连接超时，请检查您的网络状态！");
          break;
        case HttpClientUtil.NET_FAILED:
          ToastHelper.toastShort(LoginActivity.this, "网络连接失败，请检查您的网络状态！");
          break;
      }
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);

    initActionbar(getSupportActionBar());

    etName = (EditText) findViewById(R.id.et_user_name);
    etPwd = (EditText) findViewById(R.id.et_user_pwd);

    btnLogin = (Button) findViewById(R.id.btn_login);
    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (check()) {
          AndroidHelper.showProgressDialog(LoginActivity.this);
          Api.getInstance()
              .login(etName.getText().toString(), etPwd.getText().toString(),
                  new HttpClientUtil.NetClientCallback() {
                    @Override
                    public void execute(int status, String response, List<Cookie> cookies) {
                      if (status == HttpClientUtil.NET_SUCCESS) {
                        final JSONObject retObj;
                        LogHelper.e(response);
                        try {
                          retObj = new JSONObject(response);
                          final int ret = retObj.getInt("result");
                          if (ret != Api.LOGIN_SUCCESS) {
                            switch (ret) {
                              case Api.LOGIN_FAIL_LESS_THAN_FIVE:
                                String alertText =
                                    "用户名或者密码错误,还有" + (Api.ALLOW_LOGIN_USERNAME_OR_PASSWD_ERROR_NUM
                                        - retObj.getInt("strikes")) + "尝试机会";
                                Toast.makeText(LoginActivity.this, alertText, Toast.LENGTH_SHORT)
                                    .show();
                                break;
                              case Api.LOGIN_FAIL_MORE_THAN_FIVE:
                                Toast.makeText(LoginActivity.this,
                                    R.string.login_fail_more_than_five, Toast.LENGTH_SHORT).show();
                                break;
                            }
                            return;
                          }
                          String token = retObj.getString("securitytoken");
                          Api.getInstance().setToken(token);
                          Api.getInstance()
                              .setLoginUserInfo(retObj.getString("username"),
                                  retObj.getInt("userid"), retObj.getInt("isavatar"),
                                  retObj.getString("email"));

                          for (int i = 0; i < cookies.size(); i++) {
                            Cookie cookie = cookies.get(i);
                            Api.getInstance()
                                .getCookieStorage()
                                .addCookie(cookie.getName(), cookie.getValue());
                          }
                          LoginActivity.this.sendBroadcast(
                              new Intent(MyApplication.LOGIN_STATE_CHANGE_ACTION));
                        } catch (JSONException e) {
                          e.printStackTrace();
                        }
                      }
                      mHandler.sendEmptyMessage(status);
                    }
                  });
        }
      }
    });
  }

  private void initActionbar(ActionBar actionBar) {
    if (actionBar == null) return;
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setHomeButtonEnabled(true);
  }

  private boolean check() {
    if (etName.getText().toString().isEmpty()) {
      ToastHelper.toastShort(this, "请输入用户名！");
      return false;
    }

    if (etPwd.getText().toString().isEmpty()) {
      ToastHelper.toastShort(this, "请输入用户密码！");
      return false;
    }
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
