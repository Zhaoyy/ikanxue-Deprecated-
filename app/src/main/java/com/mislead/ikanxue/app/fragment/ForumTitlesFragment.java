package com.mislead.ikanxue.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.model.ForumTitleObject;
import com.mislead.ikanxue.app.util.DateHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.ShPreUtil;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.view.RecyclerLinearItemDecoration;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TitlesFragment
 *
 * @author Mislead
 *         DATE: 2015/7/8
 *         DESC:
 **/
public class ForumTitlesFragment extends BaseFragment {

  private static String TAG = "FeedbackFragment";

  private RecyclerView recyclerView;

  private List<ForumTitleObject> titles = new ArrayList<>();

  private RecyclerViewAdapter adapter;

  private long now;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    title = getString(R.string.titles);
    return inflater.inflate(R.layout.fragment_forum_titles, null);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

    initView();

    now = System.currentTimeMillis();

    long last = ShPreUtil.getLong(Constants.LAST_REFRESH_FORTUM_TITLE, 0);

    if (DateHelper.getDiffDays(now, last) > 2) {
      requestFortumTitles();
    } else {
      String jsonStr = ShPreUtil.getString(Constants.CACHE_FORTUM_TITLE);

      try {
        parseResponse(new JSONObject(jsonStr));
      } catch (JSONException e) {
        requestFortumTitles();
      }
    }
  }

  @Override protected void onLoginOrLogout() {
    ToastHelper.toastShort(mainActivity, "login:" + Api.getInstance().isLogin());
    requestFortumTitles();
  }

  private void requestFortumTitles() {
    Api.getInstance().getForumHomePage(new VolleyHelper.ResponseListener<JSONObject>() {
      @Override public void onErrorResponse(VolleyError volleyError) {
        LogHelper.e(volleyError.toString());
      }

      @Override public void onResponse(JSONObject object) {
        try {
          parseResponse(object);

          ShPreUtil.setLong(Constants.LAST_REFRESH_FORTUM_TITLE, now);
          ShPreUtil.setString(Constants.CACHE_FORTUM_TITLE, object.toString());
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void initView() {
    // set vertical listView
    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(manager);

    // to improve performance
    recyclerView.setHasFixedSize(true);

    recyclerView.addItemDecoration(
        new RecyclerLinearItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

    adapter = new RecyclerViewAdapter();
    adapter.setItemListener(new RecyclerViewAdapter.ItemListener() {
      @Override public void onItemClick(View view, int position) {
        ToastHelper.toastShort(mainActivity, titles.get(position).getName());

        Api.getInstance().getForumDisplayPage(titles.get(position).getId(), 2,
                new VolleyHelper.ResponseListener<JSONObject>() {
                  @Override public void onErrorResponse(VolleyError volleyError) {
                    LogHelper.e(volleyError.toString());
                  }

                  @Override public void onResponse(JSONObject object) {
                    LogHelper.e(object.toString());
                  }
                });
      }
    });
    recyclerView.setAdapter(adapter);
  }

  private void parseResponse(JSONObject object) throws JSONException {

    JSONArray ret = object.getJSONArray("forumbits");
    for (int i = 0; i < ret.length(); i++) {

      JSONObject innerObj = ret.getJSONObject(i);

      // add group title
      ForumTitleObject group = new ForumTitleObject();
      group.setName(innerObj.getString("forumTitle"));
      group.setType(0);
      titles.add(group);

      JSONArray forumArray = innerObj.getJSONArray("forumSubTitle");

      for (int j = 0; j < forumArray.length(); j++) {
        JSONObject forumObj = forumArray.getJSONObject(j);
        // add chile title
        ForumTitleObject child = new ForumTitleObject();
        child.setName(forumObj.getString("name"));
        child.setId(forumObj.getInt("id"));
        child.setImageId(forumObj.getInt("imgId"));
        child.setType(1);
        titles.add(child);
      }
    }

    adapter.setData(titles);
    adapter.notifyDataSetChanged();
  }

  public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ForumTitleObject> data = new ArrayList<>();

    private ItemListener itemListener;

    public void setItemListener(ItemListener itemListener) {
      this.itemListener = itemListener;
    }

    public void setData(List<ForumTitleObject> data) {
      this.data = data;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

      RecyclerView.ViewHolder holder;

      if (viewType == 0) {
        // group
        View groupView =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        holder = new GroupHolder(groupView);
      } else {
        View childView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_fortum_title, parent, false);

        holder = new ChildHolder(childView);
      }

      return holder;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

      if (getItemViewType(position) == 0) {
        GroupHolder groupHolder = (GroupHolder) holder;
        groupHolder.textView.setText(data.get(position).getName());
      } else {
        ChildHolder childHolder = (ChildHolder) holder;
        childHolder.textView.setHint(data.get(position).getName());
        childHolder.ll_item.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            if (itemListener != null) {
              itemListener.onItemClick(v, position);
            }
          }
        });
      }
    }

    @Override public int getItemCount() {
      return data.size();
    }

    @Override public int getItemViewType(int position) {
      return data.get(position).getType();
    }

    class GroupHolder extends RecyclerView.ViewHolder {

      public TextView textView;

      public GroupHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.textview);
      }
    }

    class ChildHolder extends RecyclerView.ViewHolder {

      public TextView textView;
      public LinearLayout ll_item;

      public ChildHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.textview);
        ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
      }
    }

    public interface ItemListener {
      void onItemClick(View view, int position);
    }
  }
}
