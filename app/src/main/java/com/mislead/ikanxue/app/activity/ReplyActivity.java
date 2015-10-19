package com.mislead.ikanxue.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.base.SwipeBackActivity;
import com.mislead.ikanxue.app.model.ForumThreadObject;
import com.mislead.ikanxue.app.util.ShPreUtil;
import com.mislead.ikanxue.app.util.ToastHelper;

/**
 * ReplyActivity
 *
 * @author Mislead
 *         DATE: 2015/9/25
 *         DESC:
 **/
public class ReplyActivity extends SwipeBackActivity {

  private static String TAG = "ReplyActivity";
  private TextView tvReference;
  private CheckBox chWith;
  private EditText etMsg;
  private ForumThreadObject.PostbitsEntity data;
  private String reference;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int theme_id = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);

    setTheme(theme_id);
    setContentView(R.layout.activity_reply);
    setTitle(R.string.reply);

    data = (ForumThreadObject.PostbitsEntity) getIntent().getSerializableExtra("data");

    StringBuilder msg = new StringBuilder(data.getMessage());

    while (msg.length() > 150) {
      int index = msg.lastIndexOf("<br \\/>");
      if (index < 150 && index > 0) {
        break;
      }
      msg.setLength(index < 0 ? 150 : index);
    }

    msg.append("...");

    reference = getString(R.string.ref_template, data.getUsername(), msg);

    tvReference = (TextView) findViewById(R.id.tv_reference);
    chWith = (CheckBox) findViewById(R.id.ch_with);
    etMsg = (EditText) findViewById(R.id.et_msg);
    setIbtnRightImage(R.mipmap.social_send_now);
    ibtnRight.setVisibility(View.VISIBLE);

    tvReference.setText(Html.fromHtml(reference));
  }

  @Override protected void ibtnLeftClicked() {
    onBackPressed();
  }

  @Override protected void ibtnRightClicked() {
    String msg = etMsg.getText().toString().trim();

    if (TextUtils.isEmpty(msg) || msg.length() < 6) {
      ToastHelper.toastShort(this, "回复内容不能少于6个字！");
    } else {

      msg = msg.replace("\n", "<br \\/>\n");

      if (chWith.isChecked()) {
        msg = reference + "<br \\/>" + msg;
      }

      setResult(RESULT_OK, new Intent().putExtra("msg", msg));
      finish();
    }
  }
}
