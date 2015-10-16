package com.mislead.ikanxue.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.mislead.circleimageview.lib.CircleImageView;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.application.MyApplication;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.base.ToolbarActivity;
import com.mislead.ikanxue.app.model.ForumTitleObject;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.ChangeThemeUtil;
import com.mislead.ikanxue.app.util.DateHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.util.RemoveNullInList;
import com.mislead.ikanxue.app.util.ShPreUtil;
import com.mislead.ikanxue.app.util.ToastHelper;
import com.mislead.ikanxue.app.view.LoadMoreRecyclerView;
import com.mislead.ikanxue.app.view.MySlidingLayout;
import com.mislead.ikanxue.app.view.RecyclerLinearItemDecoration;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import com.umeng.update.UmengUpdateAgent;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ToolbarActivity implements View.OnClickListener {

  private LoadMoreRecyclerView recyclerView;

  private List<ForumTitleObject> titles = new ArrayList<ForumTitleObject>();

  private RecyclerViewAdapter adapter;
  private LinearLayout ll_root;

  private MySlidingLayout sl_root;
  private LinearLayout userInfo;
  private CircleImageView circleIcon;
  private TextView name;
  private LinearLayout ll_daily;
  private LinearLayout ll_about;
  private LinearLayout ll_change_theme;
  private LinearLayout ll_exit;
  private TextView tv_type;
  private TextView tv_money;
  private ImageView iv_theme;
  private TextView tv_theme;

  private long now;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int theme_id = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);

    setTheme(theme_id);
    setContentView(R.layout.activity_main);

    sl_root = (MySlidingLayout) findViewById(R.id.sl_root);
    sl_root.toggle2Content();

    recyclerView = (LoadMoreRecyclerView) findViewById(R.id.recyclerView);
    ll_root = (LinearLayout) findViewById(R.id.ll_root);
    userInfo = (LinearLayout) findViewById(R.id.userInfo);
    circleIcon = (com.mislead.circleimageview.lib.CircleImageView) findViewById(R.id.circleIcon);
    name = (TextView) findViewById(R.id.name);
    ll_daily = (LinearLayout) findViewById(R.id.ll_daily);
    ll_about = (LinearLayout) findViewById(R.id.ll_about);
    ll_change_theme = (LinearLayout) findViewById(R.id.ll_change_theme);
    ll_exit = (LinearLayout) findViewById(R.id.ll_exit);

    tv_type = (TextView) findViewById(R.id.tv_type);
    tv_money = (TextView) findViewById(R.id.tv_money);
    iv_theme = (ImageView) findViewById(R.id.iv_theme);
    tv_theme = (TextView) findViewById(R.id.tv_theme);

    ll_daily.setOnClickListener(this);
    ll_about.setOnClickListener(this);
    ll_change_theme.setOnClickListener(this);
    ll_exit.setOnClickListener(this);
    userInfo.setOnClickListener(this);

    ibtnRight.setVisibility(View.VISIBLE);

    initView();
    setThemeIcon(theme_id);

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

    loginOrCustomer();
    UmengUpdateAgent.setUpdateOnlyWifi(false);
    UmengUpdateAgent.update(this);
  }

  private void requestFortumTitles() {
    api.getForumHomePage(new VolleyHelper.ResponseListener<JSONObject>() {
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
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(manager);

    // to improve performance
    recyclerView.setHasFixedSize(true);

    recyclerView.addItemDecoration(
        new RecyclerLinearItemDecoration(this, LinearLayoutManager.VERTICAL));

    adapter = new RecyclerViewAdapter();
    adapter.setItemListener(new RecyclerViewAdapter.ItemListener() {
      @Override public void onItemClick(View view, int position) {

        String name = titles.get(position).getName();
        int id = titles.get(position).getId();

        Intent intent = new Intent(MainActivity.this, ForumDisplayActivity.class);
        intent.putExtra("title", name);
        intent.putExtra("id", id);
        startActivity(intent);
      }
    });
    recyclerView.setAdapter(adapter);
  }

  private void parseResponse(JSONObject object) throws JSONException {
    addQuikeTitle();
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
    new RemoveNullInList<ForumTitleObject>().removeNull(titles);
    adapter.setData(titles);
    adapter.notifyDataSetChanged();
  }

  private void addQuikeTitle() {
    ForumTitleObject object = new ForumTitleObject();
    object.setType(0);
    object.setName("快捷入口");
    titles.add(object);
    ForumTitleObject child1 = new ForumTitleObject();
    child1.setName("查看新帖");
    child1.setId(Api.NEW_FORUM_ID);
    child1.setType(1);
    titles.add(child1);
    ForumTitleObject child2 = new ForumTitleObject();
    child2.setName("安全资讯");
    child2.setId(Api.SECURITY_FORUM_ID);
    child2.setType(1);
    titles.add(child2);
  }

  @Override protected void onLoginOrLogout() {

    loginOrCustomer();
    requestFortumTitles();
  }

  // 是否登录还是游客
  private void loginOrCustomer() {
    sl_root.setScrollEnable(api.isLogin());
    if (api.isLogin()) {
      if (api.getIsAvatar() > 0) {
        String headPic = api.getUserHeadImageUrl(api.getLoginUserId());

        VolleyHelper.requestImageWithCache(headPic, ibtnLeft, AndroidHelper.getImageDiskCache(),
            R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        VolleyHelper.requestImageWithCache(headPic, circleIcon, AndroidHelper.getImageDiskCache(),
            R.mipmap.ic_launcher, R.mipmap.ic_launcher);
      }
      name.setText(api.getLoginUserName());
      btnLeft.setVisibility(View.GONE);
      ibtnLeft.setVisibility(View.VISIBLE);
      api.getUserInfoPage(api.getLoginUserId(), new VolleyHelper.ResponseListener<JSONObject>() {
        @Override public void onErrorResponse(VolleyError volleyError) {
        }

        @Override public void onResponse(JSONObject jsonObject) {
          try {
            tv_type.setText(jsonObject.getString("usertitle"));
            tv_money.setText(String.format("%s Kx", jsonObject.getString("money")));
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    } else {
      sl_root.toggle2Content();
      btnLeft.setVisibility(View.VISIBLE);
      ibtnLeft.setVisibility(View.GONE);
    }
  }

  private void sendChangeTheme() {
    int themeId = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);
    ShPreUtil.setInt(Constants.THEME_ID,
        themeId == R.style.Theme_Light ? R.style.Theme_Dark : R.style.Theme_Light);
    themeId = themeId == R.style.Theme_Light ? R.style.Theme_Dark : R.style.Theme_Light;
    setThemeIcon(themeId);
    setTheme(themeId);
    sendBroadcast(new Intent(MyApplication.THEME_CHANGE_ACTION));
  }

  private void setThemeIcon(int themeID) {
    if (themeID == R.style.Theme_Dark) {
      setIbtnRightImage(R.mipmap.day);
      tv_theme.setText(R.string.light_theme);
      iv_theme.setImageResource(R.mipmap.day);
    } else {
      setIbtnRightImage(R.mipmap.night);
      tv_theme.setText(R.string.dark_theme);
      iv_theme.setImageResource(R.mipmap.night);
    }
  }

  @Override protected void changeTheme() {
    recyclerView.clear();
    recyclerView.getLayoutManager().removeAllViews();
    adapter.notifyDataSetChanged();

    int bgColor = ChangeThemeUtil.getAttrColorValue(getTheme(), R.attr.second_main_bg_color);

    if (bgColor != 0) {
      ll_root.setBackgroundColor(bgColor);
    }

    ibtnRight.setEnabled(true);
  }

  @Override protected void btnLeftClicked() {
    startActivity(new Intent(this, LoginActivity.class));
  }

  @Override protected void ibtnLeftClicked() {
    sl_root.toggle2Menu();
  }

  @Override protected void ibtnRightClicked() {
    ibtnRight.setEnabled(false);
    sendChangeTheme();
  }

  @Override public void onBackPressed() {
    exitApp();
  }

  private long exitTime = 0;

  private void exitApp() {
    if ((System.currentTimeMillis() - exitTime) > 2000) {
      ToastHelper.toastShort(this, "再按一次退出i看雪");
      exitTime = System.currentTimeMillis();
    } else {
      finish();
    }
  }

  private void gotoSplash() {
    Intent intent = new Intent(this, SplashActivity.class);
    intent.putExtra("auto", false);
    startActivity(intent);
  }

  private void gotoAbout() {
    startActivity(new Intent(this, AboutActivity.class));
  }

  private void gotoUerInfo() {
    startActivity(new Intent(this, UserInfoActivity.class));
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ll_daily:
        gotoSplash();
        break;
      case R.id.ll_about:
        gotoAbout();
        break;
      case R.id.ll_change_theme:
        sendChangeTheme();
        break;
      case R.id.ll_exit:
        finish();
        break;
      case R.id.userInfo:
        gotoUerInfo();
        break;
      default:
        break;
    }
  }

  public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ForumTitleObject> data = new ArrayList<ForumTitleObject>();

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
            .inflate(R.layout.item_forum_title, parent, false);

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
        childHolder.textView.setText(data.get(position).getName());
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
