package com.mislead.ikanxue.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mislead.circleimageview.lib.CircleImageView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.base.SwipeBackActivity;
import com.mislead.ikanxue.app.db.FavorDao;
import com.mislead.ikanxue.app.model.ForumThreadObject;
import com.mislead.ikanxue.app.model.ForumThreadTitleObject;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.DownloadHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.RemoveNullInList;
import com.mislead.ikanxue.app.util.ShPreUtil;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.view.ImageClickableTextView;
import com.mislead.ikanxue.app.view.LoadMoreRecyclerView;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ThreadDisplayActivity
 *
 * @author Mislead
 *         DATE: 2015/9/25
 *         DESC:
 **/
public class ThreadDisplayActivity extends SwipeBackActivity {

  private static String TAG = "ThreadDisplayActivity";
  private int currPage = 1;
  private ForumThreadTitleObject.ThreadListEntity entity;
  private int lastFirstPostId = 0;
  private String title;

  private long lastRefreshTime;

  private LoadMoreRecyclerView list;
  private SwipeRefreshLayout swipe_refresh;
  private LinearLayout ll_reply;

  private EditText et_reply;

  private ImageButton btn_reply;
  private FavorDao favorDao;

  private ForumThreadAdapter adapter;

  private int lastVisibleIndex = 0;

  private int footState = 0; // 0 -can load more,1-loading, 2-no more

  private List<ForumThreadObject.PostbitsEntity> threads =
      new ArrayList<ForumThreadObject.PostbitsEntity>();

  private Runnable runnable = new Runnable() {
    @Override public void run() {
      swipe_refresh.setRefreshing(true);
      refresh();
    }
  };

  private ItemClickListener listener = new ItemClickListener() {
    @Override public void itemClick(int pos) {

      if (api.isLogin()) {
        // head clicked, show user info
        Intent intent = new Intent(ThreadDisplayActivity.this, UserInfoActivity.class);
        intent.putExtra("userId", threads.get(pos).getUserid());
        startActivity(intent);
      } else {
        ToastHelper.toastLong(ThreadDisplayActivity.this, "登录之后才能查看用户信息！");
      }
    }

    @Override public void replyClick(int pos) {
      if (Api.getInstance().isLogin()) {
        // reply click
        Intent intent = new Intent(ThreadDisplayActivity.this, ReplyActivity.class);
        intent.putExtra("data", threads.get(pos));
        startActivityForResult(intent, 201);
      } else {
        ThreadDisplayActivity.this.startActivity(
            new Intent(ThreadDisplayActivity.this, LoginActivity.class));
      }
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int theme_id = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);

    setTheme(theme_id);
    setContentView(R.layout.activity_threads_display);

    list = (LoadMoreRecyclerView) findViewById(R.id.list);
    swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
    ll_reply = (LinearLayout) findViewById(R.id.ll_reply);
    et_reply = (EditText) findViewById(R.id.et_reply);
    btn_reply = (ImageButton) findViewById(R.id.btn_reply);

    Intent intent = getIntent();
    entity = (ForumThreadTitleObject.ThreadListEntity) intent.getSerializableExtra("entity");
    title = intent.getStringExtra("title");
    setTitle(title);

    setIbtnRightImage(R.mipmap.ic_favor);
    ibtnRight.setVisibility(View.VISIBLE);
    initView();
    // load first
    list.post(runnable);
  }

  @Override protected void ibtnLeftClicked() {
    onBackPressed();
  }

  @Override protected void ibtnRightClicked() {
    if (favorDao == null) {
      favorDao = new FavorDao(this);
    }
    favorDao.addFavor(entity);
    ToastHelper.toastShort(this, "帖子收藏成功!");
  }

  private void initView() {
    // set vertical listView
    final LinearLayoutManager manager = new LinearLayoutManager(ThreadDisplayActivity.this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    list.setLayoutManager(manager);

    // to improve performance
    list.setHasFixedSize(true);

    list.initFootView();

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
    if (entity.getOpen() != 1) {
      ll_reply.setVisibility(View.GONE);
    }

    btn_reply.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        replyOrLogin(et_reply.getText().toString().trim());
      }
    });
  }

  private void refresh() {

    // check there is new post or not
    Api.getInstance()
        .checkNewPostInShowThreadPage(entity.getThreadid(), lastRefreshTime,
            new VolleyHelper.ResponseListener<String>() {
              @Override public void onErrorResponse(VolleyError volleyError) {
                LogHelper.e(volleyError.toString());
                swipe_refresh.setRefreshing(false);
              }

              @Override public void onResponse(String object) {
                if (object.contains("result:")) {
                  ToastHelper.toastShort(ThreadDisplayActivity.this, "没有新帖");
                  swipe_refresh.setRefreshing(false);
                } else {
                  currPage = 1;
                  loadData();
                }
              }
            });
  }

  private void loadMore() {
    if (threads == null || threads.size() == 0 || list.getFootState() == 2) {
      return;
    }
    list.changeFootState(1);
    removeYourReply();
    currPage++;
    loadData();
  }

  // request data from net
  private void loadData() {

    Api.getInstance()
        .getForumShowthreadPage(entity.getThreadid(), currPage,
            new VolleyHelper.ResponseListener<String>() {
              @Override public void onErrorResponse(VolleyError volleyError) {
                ToastHelper.toastShort(ThreadDisplayActivity.this, volleyError.toString());
                swipe_refresh.setRefreshing(false);
              }

              @Override public void onResponse(String object) {
                try {
                  parseJSON(object);
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            });
  }

  private void parseJSON(String json) throws JSONException {
    Gson gson = new Gson();
    ForumThreadObject object = null;
    try {
      object = gson.fromJson(json, ForumThreadObject.class);
    } catch (JsonSyntaxException e) {
      swipe_refresh.setRefreshing(false);
      e.printStackTrace();
      ToastHelper.toastShort(this, "解析数据出错，可在PC查看本帖！");
      return;
    }

    lastRefreshTime = object == null ? System.currentTimeMillis() : object.getTime();
    boolean isAll = false;

    if (currPage == 1) {
      threads = object.getPostbits();
      if (threads.size() > 0) {
        lastFirstPostId = threads.get(0).getPostid();
      }
    } else {
      // add new thread

      if (object.getPostbits().size() > 0) {
        int postId = object.getPostbits().get(0).getPostid();

        if (postId == lastFirstPostId) {
          isAll = true;
        } else {
          lastFirstPostId = postId;
          for (ForumThreadObject.PostbitsEntity entity : object.getPostbits()) {
            threads.add(entity);
          }
        }
      }
    }

    if (isAll) {
      list.changeFootState(2);
    } else {
      list.changeFootState(0);
    }
    new RemoveNullInList<ForumThreadObject.PostbitsEntity>().removeNull(threads);
    swipe_refresh.setRefreshing(false);
    adapter.setData(threads);
    adapter.notifyDataSetChanged();
  }

  private void replyOrLogin(final String reply) {
    if (Api.getInstance().isLogin()) {

      if (TextUtils.isEmpty(reply)) {
        ToastHelper.toastShort(ThreadDisplayActivity.this, "回复内容不能为空！");
        return;
      }

      if (reply.length() < Api.POST_CONTENT_SIZE_MIN) {
        ToastHelper.toastShort(ThreadDisplayActivity.this, "回复内容不能少于6个字！");
        return;
      }

      Api.getInstance()
          .quickReply(entity.getThreadid(), reply, new VolleyHelper.ResponseListener<String>() {
            @Override public void onErrorResponse(VolleyError volleyError) {
              LogHelper.e(volleyError.toString());
            }

            @Override public void onResponse(String object) {
              try {
                JSONObject jsonObject = new JSONObject(object);

                int result = jsonObject.getInt("result");

                switch (result) {
                  case Api.NEW_POST_SUCCESS:
                    ToastHelper.toastLong(ThreadDisplayActivity.this, R.string.new_post_success);
                    addNewReply(reply);
                    et_reply.setText("");
                    break;
                  case Api.NEW_POST_FAIL_WITHIN_THIRTY_SECONDS:
                    ToastHelper.toastLong(ThreadDisplayActivity.this,
                        R.string.new_post_fail_within_thirty_seconds);
                    return;
                  case Api.NEW_POST_FAIL_WITHIN_FIVE_MINUTES:
                    ToastHelper.toastLong(ThreadDisplayActivity.this,
                        R.string.new_post_fail_within_five_minutes);
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
      ThreadDisplayActivity.this.startActivity(
          new Intent(ThreadDisplayActivity.this, LoginActivity.class));
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (resultCode == RESULT_OK && data != null) {
      replyOrLogin(data.getStringExtra("msg"));
    }

    super.onActivityResult(requestCode, resultCode, data);
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

  public class ForumThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ForumThreadObject.PostbitsEntity> data =
        new ArrayList<ForumThreadObject.PostbitsEntity>();

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
        return new FootHolder(list.getFootView());
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

        forumThreadHolder.tv_time.setText(
            String.format("%1$s#  %2$s %3$s", position + 1, entity.getPostdate(),
                entity.getPosttime()));
        forumThreadHolder.tv_replay.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            if (listener != null) {
              listener.replyClick(position);
            }
          }
        });

        if (position == 0) {
          forumThreadHolder.tv_title.setVisibility(View.VISIBLE);
          forumThreadHolder.tv_title.setText(Html.fromHtml(entity.getTitle()));// 直接使用可能会出现乱码
        } else {
          forumThreadHolder.tv_title.setVisibility(View.GONE);
        }

        setHtmlText(forumThreadHolder.tv_msg, entity.getMessage());
        if (entity.getThumbnail() == 1) {
          Api.getInstance()
              .getForumFullThread(entity.getPostid(), new VolleyHelper.ResponseListener<String>() {
                @Override public void onErrorResponse(VolleyError volleyError) {
                  LogHelper.e(volleyError.toString());
                }

                @Override public void onResponse(String object) {
                  entity.setThumbnail(0);
                  entity.setMessage(object);
                  setHtmlText(forumThreadHolder.tv_msg, entity.getMessage());
                }
              });
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

            ImageView imageView = new ImageView(ThreadDisplayActivity.this);
            imageView.setLayoutParams(lp);

            imageView.setTag(attachment.getAttachmentid());

            imageView.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                gotoImageActivity(api.getImageAttachmentPCUrl((int) v.getTag()));
              }
            });

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

            TextView textView = new TextView(ThreadDisplayActivity.this);
            textView.setLayoutParams(lp);
            textView.setTextColor(getResources().getColor(R.color.ics_blue_dark));
            textView.setTextSize(14);
            textView.setTag(attachment);
            textView.setText(attachment.getFilename());

            textView.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View view) {
                ForumThreadObject.ThumbnailattachmentsEntity attachment =
                    (ForumThreadObject.ThumbnailattachmentsEntity) view.getTag();

                DownloadHelper downloadHelper =
                    DownloadHelper.getInstance(ThreadDisplayActivity.this);
                downloadHelper.addDownloadTask(ThreadDisplayActivity.this, attachment);
              }
            });

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

    private void gotoImageActivity(String url) {
      Intent intent = new Intent(ThreadDisplayActivity.this, ImageActivity.class);
      intent.putExtra("title", title);
      intent.putExtra("url", url);
      startActivity(intent);
    }

    private void setHtmlText(final ImageClickableTextView textView, final String htmlStr) {
      textView.setListener(new ImageClickableTextView.OnImageClickListener() {
        @Override public void imageClicked(String imageUrl) {
          // 一般以带gif扩展名的是表情
          if (!imageUrl.endsWith(".gif")) {
            gotoImageActivity(imageUrl);
          }
        }
      });
      textView.setHtmlString(htmlStr);
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

    void replyClick(int pos);
  }

  class ThreadHolder extends RecyclerView.ViewHolder {

    public LinearLayout ll_item;
    public CircleImageView iv_head;
    public TextView tv_name;
    public TextView tv_time;
    public TextView tv_replay;
    public TextView tv_title;
    public ImageClickableTextView tv_msg;
    public LinearLayout ll_attachment;
    public LinearLayout ll_image_attachment;
    public LinearLayout ll_other_attachment;

    public ThreadHolder(View itemView) {
      super(itemView);
      ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
      iv_head = (CircleImageView) itemView.findViewById(R.id.iv_head);
      tv_name = (TextView) itemView.findViewById(R.id.tv_name);
      tv_time = (TextView) itemView.findViewById(R.id.tv_time);
      tv_replay = (TextView) itemView.findViewById(R.id.tv_reply);
      tv_title = (TextView) itemView.findViewById(R.id.tv_title);
      tv_msg = (ImageClickableTextView) itemView.findViewById(R.id.tv_msg);
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
