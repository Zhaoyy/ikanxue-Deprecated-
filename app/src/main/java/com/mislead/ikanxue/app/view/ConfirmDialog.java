package com.mislead.ikanxue.app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.mislead.ikanxue.app.R;

/**
 * ConfirmDialog
 * Created by zhaoyy
 * on 15-10-18.
 */
public class ConfirmDialog extends Dialog implements View.OnClickListener {

  private Context context;
  private String content;
  private TextView textView;
  private OnConfirmListener listener;
  private int themeId;

  public ConfirmDialog(Context context, int theme_id, String content, OnConfirmListener listener) {
    super(context);
    this.themeId = theme_id;
    this.content = content;
    this.context = context;
    this.listener = listener;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    setCanceledOnTouchOutside(true);
    setCancelable(true);

    setContentView(R.layout.dialog_confirm);
    textView = (TextView) findViewById(R.id.textView);
    textView.setTextColor(
        themeId == R.style.Theme_Dark ? context.getResources().getColor(R.color.text_black_1_dark)
            : context.getResources().getColor(R.color.text_black_1));
    textView.setText(content);

    findViewById(R.id.ll_root).setBackgroundResource(
        themeId == R.style.Theme_Dark ? R.drawable.dialog_bg_dark : R.drawable.dialog_bg_white);
    findViewById(R.id.btn_confirm).setOnClickListener(this);
    findViewById(R.id.btn_cancel).setOnClickListener(this);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_confirm:

        if (listener != null) {
          listener.onConfirm();
        }

        break;
      default:
        break;
    }
    dismiss();
  }

  public interface OnConfirmListener {
    public void onConfirm();
  }
}
