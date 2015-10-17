package com.mislead.ikanxue.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mislead.circleimageview.lib.CircleImageView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.base.SwipeBackActivity;
import com.mislead.ikanxue.app.db.FavorDao;
import com.mislead.ikanxue.app.model.ForumThreadTitleObject;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.RemoveNullInList;
import com.mislead.ikanxue.app.util.ShPreUtil;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.view.LoadMoreRecyclerView;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ForumDisplayActivity
 *
 * @author Mislead
 *         DATE: 2015/9/25
 *         DESC:
 **/
public class ForumDisplayActivity extends SwipeBackActivity {

  private static String TAG = "ForumDisplayActivity";

  private int currPage = 1;

  private long lastRefreshTime;

  private LoadMoreRecyclerView list;
  private SwipeRefreshLayout swipe_refresh;

  private ForumThreadAdapter adapter;

  private int lastVisibleIndex = 0;

  private int footState = 0; // 0 -can load more,1-loading, 2-no more
  private int titleID;
  private String title = "";
  private boolean fromFavor = false;
  private FavorDao favorDao;

  private List<ForumThreadTitleObject.ThreadListEntity> threads =
      new ArrayList<ForumThreadTitleObject.ThreadListEntity>();

  private Runnable runnable = new Runnable() {
    @Override public void run() {
      swipe_refresh.setRefreshing(true);
      refresh();
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int theme_id = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);

    setTheme(theme_id);
    setContentView(R.layout.activity_forum_threads);
    list = (LoadMoreRecyclerView) findViewById(R.id.list);
    swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
    Intent intent = getIntent();
    titleID = intent.getIntExtra("id", 0);
    fromFavor = titleID == 0;
    title = intent.getStringExtra("title");

    setTitle(title);

    initView();
    ibtnRight.setVisibility(fromFavor ? View.GONE : View.VISIBLE);
    setIbtnRightImage(R.mipmap.ic_post);
    list.post(runnable);
  }

  private void initView() {
    // set vertical listView
    final LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    list.setLayoutManager(manager);
    list.initFootView();
    // to improve performance
    list.setHasFixedSize(true);

    adapter = new ForumThreadAdapter();
    list.setAdapter(adapter);

    if (!fromFavor) {
      list.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
          super.onScrollStateChanged(recyclerView, newState);
          if (newState == RecyclerView.SCROLL_STATE_IDLE
              && lastVisibleIndex + 1 == adapter.getItemCount() && (footState == 0)) {
            loadMore();
          }
        }

        @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          lastVisibleIndex = manager.findLastVisibleItemPosition();
        }
      });
    }

    swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.ics_red_dark),
        getResources().getColor(R.color.ics_orange_dark),
        getResources().getColor(R.color.ics_blue_dark),
        getResources().getColor(R.color.ics_purple_dark));

    swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        refresh();
      }
    });
  }

  private void refresh() {
    if (fromFavor) {

      if (favorDao == null) {
        favorDao = new FavorDao(this);
      }

      swipe_refresh.setRefreshing(false);
      threads = favorDao.getFavors();
      adapter.setData(threads);
    } else {
      // check there is new post or not
      Api.getInstance()
          .checkNewPostInForumDisplayPage(titleID, lastRefreshTime,
              new VolleyHelper.ResponseListener<JSONObject>() {
                @Override public void onErrorResponse(VolleyError volleyError) {
                  LogHelper.e(volleyError.toString());
                  swipe_refresh.setRefreshing(false);
                }

                @Override public void onResponse(JSONObject object) {
                  if (object.has("result")) {
                    ToastHelper.toastShort(ForumDisplayActivity.this, "没有新帖");
                    swipe_refresh.setRefreshing(false);
                  } else {
                    currPage = 1;
                    loadData();
                  }
                }
              });
    }
  }

  private void loadMore() {
    if (list.getFootState() == 2) {
      return;
    }
    list.changeFootState(1);
    currPage++;
    loadData();
  }

  private void loadData() {

    Api.getInstance()
        .getForumDisplayPage(titleID, currPage, new VolleyHelper.ResponseListener<JSONObject>() {
          @Override public void onErrorResponse(VolleyError volleyError) {
            ToastHelper.toastShort(ForumDisplayActivity.this, volleyError.toString());
            swipe_refresh.setRefreshing(false);
          }

          @Override public void onResponse(JSONObject object) {
            try {
              parseJSON(object);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }

  private void parseJSON(JSONObject json) throws JSONException {
    Gson gson = new Gson();
    ForumThreadTitleObject object = gson.fromJson(json.toString(), ForumThreadTitleObject.class);

    lastRefreshTime = object.getTime();
    int pageCount = object.getPagenav();

    if (currPage < pageCount || object.getThreadList().size() > 0) {
      if (currPage == 1) {
        threads = object.getThreadList();
      } else {
        // add new thread
        for (ForumThreadTitleObject.ThreadListEntity entity : object.getThreadList()) {
          threads.add(entity);
        }
      }
      list.changeFootState(0);
    } else {
      list.changeFootState(2);
    }
    new RemoveNullInList<ForumThreadTitleObject.ThreadListEntity>().removeNull(threads);
    swipe_refresh.setRefreshing(false);
    adapter.setData(threads);
  }

  @Override protected void ibtnRightClicked() {
    postNewThreadOrLogin();
  }

  private void postNewThreadOrLogin() {
    if (Api.getInstance().isLogin()) {
      String type = Api.getInstance().getLoginUserType();
      if (type.equals("临时会员") && (titleID != Api.TEMPORARY_FORUM_ID
          && titleID != Api.HELP_FORUM_ID)) {
        ToastHelper.toastShort(this, R.string.temporary_limit);
        return;
      }

      Intent intent = new Intent(this, PostNewThreadActivity.class);
      intent.putExtra("id", titleID);
      startActivityForResult(intent, 502);
    } else {
      startActivity(new Intent(this, LoginActivity.class));
    }
  }

  private ItemClickListener listener = new ItemClickListener() {
    @Override public void itemClick(int pos) {
      ForumThreadTitleObject.ThreadListEntity entity = threads.get(pos);
      Intent intent = new Intent(ForumDisplayActivity.this, ThreadDisplayActivity.class);
      intent.putExtra("entity", entity);
      intent.putExtra("title", title);

      startActivity(intent);
    }
  };

  @Override protected void ibtnLeftClicked() {
    onBackPressed();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    onRefresh(data);
  }

  public void onRefresh(Intent data) {
    if (data != null) {
      // post a new topic
      int index = getLastStickyIndex();

      int threadId = data.getIntExtra("threadid", 0);
      String subject = data.getStringExtra("subject");

      ForumThreadTitleObject.ThreadListEntity entity =
          new ForumThreadTitleObject.ThreadListEntity();
      entity.setThreadid(threadId);
      entity.setThreadtitle(subject);
      entity.setPostuserid(Api.getInstance().getLoginUserId());
      entity.setPostusername(Api.getInstance().getLoginUserName());
      entity.setAvatar(Api.getInstance().getIsAvatar());
      entity.setReplycount(0);
      entity.setViews(1);

      threads.add(index, entity);

      adapter.setData(threads);
    }
  }

  private int getLastStickyIndex() {
    for (int i = 0; i < threads.size(); i++) {
      ForumThreadTitleObject.ThreadListEntity entity = threads.get(i);

      if (entity.getGlobalsticky() == -1 || entity.getSticky() == 1) {
        continue;
      }

      return i;
    }

    return -1;
  }

  public class ForumThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ForumThreadTitleObject.ThreadListEntity> data =
        new ArrayList<ForumThreadTitleObject.ThreadListEntity>();

    public void setData(List<ForumThreadTitleObject.ThreadListEntity> data) {
      this.data = data;

      notifyDataSetChanged();
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (viewType == 0) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_forum_thread, parent, false);
        return new ForumThreadHolder(view);
      } else {
        return new FootHolder(list.getFootView());
      }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

      if (holder instanceof ForumThreadHolder) {
        ForumThreadTitleObject.ThreadListEntity entity = data.get(position);
        ForumThreadHolder forumThreadHolder = (ForumThreadHolder) holder;
        if (entity.getAvatar() == 0) {
          forumThreadHolder.iv_head.setImageResource(R.mipmap.ic_launcher);
        } else {
          String headUrl = Api.getInstance().getUserHeadImageUrl(entity.getPostuserid());
          VolleyHelper.requestImageWithCache(headUrl, forumThreadHolder.iv_head,
              AndroidHelper.getImageDiskCache(), R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        }

        forumThreadHolder.tv_name.setText(Html.fromHtml(entity.getPostusername()));

        String tag = "";
        // 不是新帖版块
        if (titleID != Api.NEW_FORUM_ID) {
          if (entity.getGlobalsticky() == Api.GLOBAL_TOP_FORUM) {
            tag = "<font color=\"red\">[总顶] </font>";
          } else if (entity.getGlobalsticky() == Api.AREA_TOP_FORUM) {
            tag = "<font color=\"red\">[区顶] </font>";
          } else if (entity.getSticky() == Api.TOP_FORUM) {
            tag = "<font color=\"red\">[置顶] </font>";
          }
        }

        forumThreadHolder.tv_title.setText(
            Html.fromHtml(tag + entity.getThreadtitle()));// 直接使用可能会出现乱码
        forumThreadHolder.tv_info.setText(
            String.format("查看：%1$s  回复：%2$s", entity.getViews(), entity.getReplycount()));

        forumThreadHolder.ll_item.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            if (listener != null) {
              listener.itemClick(position);
            }
          }
        });
      }
    }

    @Override public int getItemCount() {
      return data.size() + (fromFavor ? 0 : 1);
    }

    @Override public int getItemViewType(int position) {
      return position < data.size() ? 0 : 1; // 0-item view,1-footer
    }
  }

  interface ItemClickListener {
    void itemClick(int pos);
  }

  class ForumThreadHolder extends RecyclerView.ViewHolder {

    public CircleImageView iv_head;
    public TextView tv_title;
    public TextView tv_name;
    public TextView tv_info;
    public LinearLayout ll_item;

    public ForumThreadHolder(View itemView) {
      super(itemView);
      ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
      iv_head = (CircleImageView) itemView.findViewById(R.id.iv_head);
      tv_title = (TextView) itemView.findViewById(R.id.tv_title);
      tv_name = (TextView) itemView.findViewById(R.id.tv_name);
      tv_info = (TextView) itemView.findViewById(R.id.tv_info);
    }
  }

  class FootHolder extends RecyclerView.ViewHolder {

    public FootHolder(View itemView) {
      super(itemView);
    }
  }
}
