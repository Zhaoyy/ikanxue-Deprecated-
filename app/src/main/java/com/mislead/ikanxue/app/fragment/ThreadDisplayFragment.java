package com.mislead.ikanxue.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mislead.circleimageview.lib.CircleImageView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.activity.LoginActivity;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.base.BaseFragment;
import com.mislead.ikanxue.app.model.ForumThreadObject;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.view.LoadMoreRecyclerView;
import com.mislead.ikanxue.app.view.MaterialProgressDrawable;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import com.mislead.ikanxue.app.volley.VolleyImageGetter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ThreadDisplayFragment
 *
 * @author Mislead
 *         DATE: 2015/7/11
 *         DESC:
 **/
public class ThreadDisplayFragment extends BaseFragment {

  private static String TAG = "ThreadDisplayFragment";

  private int threadId;
  private int open;
  private int replyCount;
  private int currPage = 1;

  private long lastRefreshTime;
  private int pageCount;

  private LoadMoreRecyclerView list;
  private SwipeRefreshLayout swipe_refresh;
  private LinearLayout ll_reply;

  private EditText et_reply;

  private ImageButton btn_reply;

  private View footView;

  private ForumThreadAdapter adapter;

  private int lastVisibleIndex = 0;

  private int footState = 0; // 0 -can load more,1-loading, 2-no more

  private List<ForumThreadObject.PostbitsEntity> threads = new ArrayList<>();

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

      if (Api.getInstance().isLogin()) {
        // head clicked, show user info

        UserInfoFragment fragment = new UserInfoFragment();
        Bundle data = new Bundle();
        data.putInt("userId", threads.get(pos).getUserid());
        fragment.setData(data);

        mainActivity.gotoFragment(fragment, false);
      } else {
        ToastHelper.toastLong(getActivity(), "登录之后才能查看用户信息！");
      }
    }
  };

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    threadId = data.getInt("threadId");
    open = data.getInt("open");
    replyCount = data.getInt("replyCount");
    title = data.getString("title");
    return inflater.inflate(R.layout.fragment_threads_display, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    list = (LoadMoreRecyclerView) view.findViewById(R.id.list);
    swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
    ll_reply = (LinearLayout) view.findViewById(R.id.ll_reply);
    et_reply = (EditText) view.findViewById(R.id.et_reply);
    btn_reply = (ImageButton) view.findViewById(R.id.btn_reply);
    initView();

    // load first
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
    progressDrawable.setColorSchemeColors(getResources().getColor(R.color.ics_blue_dark));
    progressDrawable.updateSizes(MaterialProgressDrawable.DEFAULT);
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
    // thread has closed
    if (open != 1) {
      ll_reply.setVisibility(View.GONE);
    }

    et_reply.setHint(String.format("回复不少于6个字，已有%s个回复", replyCount));

    btn_reply.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        replyOrLogin();
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
        .checkNewPostInShowThreadPage(threadId, lastRefreshTime,
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
    removeYourReply();
    currPage++;
    loadData();
  }

  // request data from net
  private void loadData() {
    Api.getInstance()
        .getForumShowthreadPage(threadId, currPage,
            new VolleyHelper.ResponseListener<JSONObject>() {
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
    ForumThreadObject object = gson.fromJson(json.toString(), ForumThreadObject.class);

    lastRefreshTime = object.getTime();
    pageCount = object.getPagenav();

    if (currPage == 1) {
      threads = object.getPostbits();
    } else {
      // add new thread
      for (ForumThreadObject.PostbitsEntity entity : object.getPostbits()) {
        threads.add(entity);
      }
    }

    if (threads.size() > replyCount) {
      changeFootState(2);
    } else {
      changeFootState(0);
    }

    swipe_refresh.setRefreshing(false);
    adapter.setData(threads);
    adapter.notifyDataSetChanged();
  }

  private void replyOrLogin() {
    if (Api.getInstance().isLogin()) {
      String reply = et_reply.getText().toString();

      if (TextUtils.isEmpty(reply)) {
        ToastHelper.toastShort(getActivity(), "回复内容不能为空！");
        return;
      }

      if (reply.length() < Api.POST_CONTENT_SIZE_MIN) {
        ToastHelper.toastShort(getActivity(), "回复内容不能少于6个字！");
        return;
      }

      Api.getInstance().quickReply(threadId, reply, new VolleyHelper.ResponseListener<String>() {
        @Override public void onErrorResponse(VolleyError volleyError) {
          LogHelper.e(volleyError.toString());
        }

        @Override public void onResponse(String object) {
          try {
            JSONObject jsonObject = new JSONObject(object);

            int result = jsonObject.getInt("result");

            switch (result) {
              case Api.NEW_POST_SUCCESS:
                ToastHelper.toastLong(getActivity(), R.string.new_post_success);
                addNewReply(et_reply.getText().toString());
                et_reply.setHint(String.format("回复不少于6个字，已有%s个回复", replyCount));
                et_reply.setText("");
                break;
              case Api.NEW_POST_FAIL_WITHIN_THIRTY_SECONDS:
                ToastHelper.toastLong(getActivity(), R.string.new_post_fail_within_thirty_seconds);
                return;
              case Api.NEW_POST_FAIL_WITHIN_FIVE_MINUTES:
                ToastHelper.toastLong(getActivity(), R.string.new_post_fail_within_five_minutes);
                return;
              default:
                break;
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    } else {
      getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
    }
  }

  /**
   * add your reply to the last
   */
  private void addNewReply(String msg) {
    ForumThreadObject.PostbitsEntity entity = new ForumThreadObject.PostbitsEntity();
    // set post info
    entity.setMessage(msg);
    entity.setThumbnail(0);
    entity.setPostid(-1);
    // set user info
    entity.setUserid(Api.getInstance().getLoginUserId());
    entity.setUsername(Api.getInstance().getLoginUserName());
    entity.setAvatar(Api.getInstance().getIsAvatar());
    // set post time
    entity.setPostdate(getString(R.string.today));
    Calendar calendar = Calendar.getInstance();
    entity.setPosttime(
        calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(
            Calendar.SECOND));

    threads.add(entity);
    adapter.setData(threads);
    replyCount++;
  }

  /**
   * before load more, remove our new reply
   */
  private void removeYourReply() {
    if (threads.size() <= 0) return;
    ForumThreadObject.PostbitsEntity entity = threads.get(threads.size() - 1);

    if (entity.getPostid() < 0) {
      threads.remove(entity);
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_refresh, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
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

  public class ForumThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ForumThreadObject.PostbitsEntity> data = new ArrayList<>();

    public void setData(List<ForumThreadObject.PostbitsEntity> data) {
      this.data = data;

      notifyDataSetChanged();
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      if (viewType == 0) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_thread_display, parent, false);
        return new ThreadHolder(view);
      } else {
        return new FootHolder(footView);
      }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

      if (holder instanceof ThreadHolder) {
        final ForumThreadObject.PostbitsEntity entity = data.get(position);
        final ThreadHolder forumThreadHolder = (ThreadHolder) holder;
        if (entity.getAvatar() == 0) {
          forumThreadHolder.iv_head.setImageResource(R.mipmap.ic_launcher);
        } else {
          String headUrl = Api.getInstance().getUserHeadImageUrl(entity.getUserid());
          VolleyHelper.requestImageWithCache(headUrl, forumThreadHolder.iv_head,
              AndroidHelper.getImageDiskCache(), R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        }

        forumThreadHolder.tv_name.setText(Html.fromHtml(entity.getUsername()));

        forumThreadHolder.tv_time.setText(entity.getPostdate() + " " + entity.getPosttime());
        forumThreadHolder.tv_num.setText((position + 1) + "#");

        if (position == 0) {
          forumThreadHolder.tv_title.setVisibility(View.VISIBLE);
          forumThreadHolder.tv_title.setText(Html.fromHtml(entity.getTitle()));// 直接使用可能会出现乱码
        } else {
          forumThreadHolder.tv_title.setVisibility(View.GONE);
        }


        if (entity.getThumbnail() == 1) {
          Api.getInstance()
              .getForumFullThread(entity.getPostid(), new VolleyHelper.ResponseListener<String>() {
                @Override public void onErrorResponse(VolleyError volleyError) {
                  LogHelper.e(volleyError.toString());
                }

                @Override public void onResponse(String object) {
                  entity.setThumbnail(0);
                  entity.setMessage(object);
                  forumThreadHolder.tv_msg.setText(Html.fromHtml(entity.getMessage(),
                      new VolleyImageGetter(forumThreadHolder.tv_msg), null));
                }
              });
        } else {
          forumThreadHolder.tv_msg.setText(
              Html.fromHtml(entity.getMessage(), new VolleyImageGetter(forumThreadHolder.tv_msg),
                  null));
        }

        if ((entity.getThumbnailattachments() != null
            && entity.getThumbnailattachments().size() > 0) || (entity.getOtherattachments() != null
            && entity.getOtherattachments().size() > 0)) {
          forumThreadHolder.ll_attachment.setVisibility(View.VISIBLE);
        } else {
          forumThreadHolder.ll_attachment.setVisibility(View.GONE);
        }

        LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 8, 0, 0);
        // 处理图片附件
        if (entity.getThumbnailattachments() != null
            && entity.getThumbnailattachments().size() > 0) {
          forumThreadHolder.ll_image_attachment.setVisibility(View.VISIBLE);
          forumThreadHolder.ll_image_attachment.removeAllViews();
          for (ForumThreadObject.ThumbnailattachmentsEntity attachment : entity.getThumbnailattachments()) {

            if (attachment == null) continue;

            String url = Api.getInstance().getAttachmentImgUrl(attachment.getAttachmentid());

            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(lp);

            VolleyHelper.requestImageWithCache(url, imageView, AndroidHelper.getImageDiskCache(),
                R.mipmap.image_404, R.mipmap.image_404);

            forumThreadHolder.ll_image_attachment.addView(imageView);
          }
        } else {
          forumThreadHolder.ll_image_attachment.setVisibility(View.GONE);
        }

        //处理其他附件
        if (entity.getOtherattachments() != null && entity.getOtherattachments().size() > 0) {
          forumThreadHolder.ll_other_attachment.setVisibility(View.VISIBLE);
          forumThreadHolder.ll_other_attachment.removeAllViews();
          for (ForumThreadObject.ThumbnailattachmentsEntity attachment : entity.getOtherattachments()) {

            if (attachment == null) continue;

            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(lp);
            textView.setTextColor(getResources().getColor(R.color.ics_blue_dark));
            textView.setTextSize(14);
            textView.setText(attachment.getFilename());

            forumThreadHolder.ll_other_attachment.addView(textView);
          }
        } else {
          forumThreadHolder.ll_other_attachment.setVisibility(View.GONE);
        }

        forumThreadHolder.iv_head.setOnClickListener(new View.OnClickListener() {
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

  class ThreadHolder extends RecyclerView.ViewHolder {

    public LinearLayout ll_item;
    public CircleImageView iv_head;
    public TextView tv_name;
    public TextView tv_time;
    public TextView tv_num;
    public TextView tv_title;
    public TextView tv_msg;
    public LinearLayout ll_attachment;
    public LinearLayout ll_image_attachment;
    public LinearLayout ll_other_attachment;

    public ThreadHolder(View itemView) {
      super(itemView);
      ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
      iv_head = (CircleImageView) itemView.findViewById(R.id.iv_head);
      tv_name = (TextView) itemView.findViewById(R.id.tv_name);
      tv_time = (TextView) itemView.findViewById(R.id.tv_time);
      tv_num = (TextView) itemView.findViewById(R.id.tv_num);
      tv_title = (TextView) itemView.findViewById(R.id.tv_title);
      tv_msg = (TextView) itemView.findViewById(R.id.tv_msg);
      ll_attachment = (LinearLayout) itemView.findViewById(R.id.ll_attachment);
      ll_image_attachment = (LinearLayout) itemView.findViewById(R.id.ll_image_attachment);
      ll_other_attachment = (LinearLayout) itemView.findViewById(R.id.ll_other_attachment);
    }
  }

  class FootHolder extends RecyclerView.ViewHolder {

    public FootHolder(View itemView) {
      super(itemView);
    }
  }
}
