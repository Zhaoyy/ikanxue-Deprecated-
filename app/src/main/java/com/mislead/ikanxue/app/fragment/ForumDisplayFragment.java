package com.mislead.ikanxue.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import org.json.JSONObject;

/**
 * ForumDisplayFragment
 *
 * @author Mislead
 *         DATE: 2015/7/11
 *         DESC:
 **/
public class ForumDisplayFragment extends BaseFragment {

  private static String TAG = "ForumDisplayFragment";

  private int titleID;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    titleID = data.getInt("id");
    title = data.getString("title");
    return inflater.inflate(R.layout.fragment_feedback, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Api.getInstance()
        .getForumDisplayPage(titleID, 2, new VolleyHelper.ResponseListener<JSONObject>() {
              @Override public void onErrorResponse(VolleyError volleyError) {
                LogHelper.e(volleyError.toString());
              }

              @Override public void onResponse(JSONObject object) {
                LogHelper.e(object.toString());
              }
            });
  }
}
