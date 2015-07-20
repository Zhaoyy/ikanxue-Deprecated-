package com.mislead.ikanxue.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.activity.LoginActivity;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.model.ForumThreadTitleObject;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.view.LoadMoreRecyclerView;
import com.mislead.ikanxue.app.view.MaterialProgressDrawable;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
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
  private int currPage = 1;

  private long lastRefreshTime;
  private int pageCount;

  private LoadMoreRecyclerView list;
  private SwipeRefreshLayout swipe_refresh;

  private View footView;

  private ForumThreadAdapter adapter;

  private int lastVisibleIndex = 0;

  private int footState = 0; // 0 -can load more,1-loading, 2-no more

  private List<ForumThreadTitleObject.ThreadListEntity> threads = new ArrayList<>();

  private MaterialProgressDrawable progressDrawable;

  private Runnable runnable = new Runnable() {
    @Override public void run() {
      swipe_refresh.setRefreshing(true);
      refresh();
    }
  };

  private Runnable progressRunable = new Runnable() {
    @Override public void run() {
      if (footState == 1) {
        progressDrawable.start();
      } else {
        progressDrawable.stop();
      }
    }
  };

  private ItemClickListener listener = new ItemClickListener() {
    @Override public void itemClick(int pos) {
      int threadId = threads.get(pos).getThreadid();
      int open = threads.get(pos).getOpen();
      int replyCount = threads.get(pos).getReplycount();
      Bundle data = new Bundle();
      data.putInt("threadId", threadId);
      data.putInt("open", open);
      data.putInt("replyCount", replyCount);
      data.putString("title", title);

      ThreadDisplayFragment fragment = new ThreadDisplayFragment();
      fragment.setData(data);
      mainActivity.gotoFragment(fragment, false);
    }
  };

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    titleID = data.getInt("id");
    title = data.getString("title");
    return inflater.inflate(R.layout.fragment_forum_threads, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    list = (LoadMoreRecyclerView) view.findViewById(R.id.list);
    swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
    initView();
    list.post(runnable);
  }

  private void initView() {
    // set vertical listView
    final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    list.setLayoutManager(manager);

    // to improve performance
    list.setHasFixedSize(true);

    // add foot view
    footView = LayoutInflater.from(getActivity()).inflate(R.layout.view_load_more, null);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    footView.setLayoutParams(params);
    ImageView ivProgress = (ImageView) footView.findViewById(R.id.iv_progress);
    progressDrawable = new MaterialProgressDrawable(getActivity(), ivProgress);
    progressDrawable.setAlpha(255);
    ivProgress.setImageDrawable(progressDrawable);

    adapter = new ForumThreadAdapter();
    list.setAdapter(adapter);

    list.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE
            && lastVisibleIndex + 1 == adapter.getItemCount()
            && (footState == 0)) {
          loadMore();
        }
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        lastVisibleIndex = manager.findLastVisibleItemPosition();
      }
    });

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

  private void changeFootState(int state) {
    footState = state;

    footView.findViewById(R.id.tv_load_more).setVisibility(state == 0 ? View.VISIBLE : View.GONE);
    footView.findViewById(R.id.ll_loading).setVisibility(state == 1 ? View.VISIBLE : View.GONE);
    footView.findViewById(R.id.tv_no_more).setVisibility(state == 2 ? View.VISIBLE : View.GONE);

    footView.findViewById(R.id.iv_progress).post(progressRunable);
  }

  private void refresh() {
    // check there is new post or not
    Api.getInstance()
        .checkNewPostInForumDisplayPage(titleID, lastRefreshTime,
            new VolleyHelper.ResponseListener<JSONObject>() {
              @Override public void onErrorResponse(VolleyError volleyError) {
                LogHelper.e(volleyError.toString());
              }

              @Override public void onResponse(JSONObject object) {
                if (object.has("result")) {
                  ToastHelper.toastShort(getActivity(), "没有新帖");
                  swipe_refresh.setRefreshing(false);
                } else {
                  currPage = 1;
                  loadData();
                }
              }
            });
  }

  private void loadMore() {
    changeFootState(1);
    currPage++;
    loadData();
  }

  private void loadData() {

    Api.getInstance()
        .getForumDisplayPage(titleID, currPage, new VolleyHelper.ResponseListener<JSONObject>() {
          @Override public void onErrorResponse(VolleyError volleyError) {
            LogHelper.e(volleyError.toString());
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
    pageCount = object.getPagenav();

    if (currPage < pageCount || object.getThreadList().size() > 0) {
      if (currPage == 1) {
        threads = object.getThreadList();
      } else {
        // add new thread
        for (ForumThreadTitleObject.ThreadListEntity entity : object.getThreadList()) {
          threads.add(entity);
        }
      }
      changeFootState(0);
    } else {
      changeFootState(2);
    }
    swipe_refresh.setRefreshing(false);
    adapter.setData(threads);
    adapter.notifyDataSetChanged();
  }

  private void postNewThreadOrLogin() {
    if (Api.getInstance().isLogin()) {
      String type = Api.getInstance().getLoginUserType();
      if (type.equals("临时会员") && (titleID != Api.TEMPORARY_FORUM_ID
          && titleID != Api.HELP_FORUM_ID)) {
        ToastHelper.toastShort(getActivity(), R.string.temporary_limit);
        return;
      }

      PostNewThreadFragment fragment = new PostNewThreadFragment();
      Bundle data = new Bundle();
      data.putInt("id", titleID);
      fragment.setData(data);
      mainActivity.gotoFragment(fragment, false);
    } else {
      getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_post_refresh, menu);
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    // 新帖版块不能发帖
    if (titleID == Api.NEW_FORUM_ID) {
      MenuItem item = menu.findItem(R.id.action_post);

      if (item != null) item.setVisible(false);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_new_post:
        postNewThreadOrLogin();
        break;
      case R.id.action_refresh:
        swipe_refresh.setRefreshing(true);
        refresh();
        break;
      default:
        break;
    }
    return true;
  }

  @Override protected void onLoginOrLogout() {
    super.onLoginOrLogout();
    refresh();
  }

  @Override public void onRefresh() {
    super.onRefresh();
    if (data != null) {
      // post a new topic
      int index = getLastStickyIndex();

      int threadId = data.getInt("threadid");
      String subject = data.getString("subject");

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

    private List<ForumThreadTitleObject.ThreadListEntity> data = new ArrayList<>();

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
        return new FootHolder(footView);
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
      return data.size() + 1;
    }

    @Override public int getItemViewType(int position) {
      return position < data.size() ? 0 : 1; // 0-item view,1-footer
    }
  }

  interface ItemClickListener {
    void itemClick(int pos);
  }

  class ForumThreadHolder extends RecyclerView.ViewHolder {

    public ImageView iv_head;
    public TextView tv_title;
    public TextView tv_name;
    public TextView tv_info;
    public LinearLayout ll_item;

    public ForumThreadHolder(View itemView) {
      super(itemView);
      ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
      iv_head = (ImageView) itemView.findViewById(R.id.iv_head);
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
