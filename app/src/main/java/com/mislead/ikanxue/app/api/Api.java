package com.mislead.ikanxue.app.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.model.CookieStorage;
import com.mislead.ikanxue.app.model.ObjStorage;
import com.mislead.ikanxue.app.net.HttpClientUtil;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 看雪看全论坛开放api类
 *
 * @author fanhexin
 */
public class Api {
  public static final String DOMAIN = "http://bbs.pediy.com";
  public static final String PATH = "/";
  /**
   * 看雪安卓客户端专用的模板id，可通过在看雪任一url后加入模板参数查看各接口返回数据
   */
  public static final String STYLE = "styleid=12";

  // 登录返回状态码
  public static final int LOGIN_SUCCESS = 0;
  public static final int LOGIN_FAIL_LESS_THAN_FIVE = 1; // 用户名或密码错误尝试5次以内
  public static final int LOGIN_FAIL_MORE_THAN_FIVE = 2; // 登录失败次数超过5次,15分钟后才可继续登录

  // 发新贴返回状态码
  public static final int NEW_POST_SUCCESS = 0;
  public static final int NEW_POST_FAIL_WITHIN_THIRTY_SECONDS = 1; // 三十秒内发两个贴
  public static final int NEW_POST_FAIL_WITHIN_FIVE_MINUTES = 2; // 5分钟内发相同内容
  public static final int NEW_POST_FAIL_NOT_ENOUGH_KX = 3; // 看雪币不足

  // 一些板块的id号
  public static final int HELP_FORUM_ID = 20; // 看雪求助问答版块的id
  // public static final int SOFTWARE_DEBUG_FORUM_ID = 4; //软件调试版块的id
  public static final int GET_JOB_FORUM_ID = 47; // 考聘版块id
  public static final int POST_CONTENT_SIZE_MIN = 6; // 发帖或回帖的最小长度
  public static final int NEW_FORUM_ID = 153; // 新贴集合版块id
  public static final int LIFE_FORUM_ID = 45; // 生活放心情版块id
  public static final int SECURITY_FORUM_ID = 61; // 生活放心情版块id

  // 几种置顶类型
  public static final int GLOBAL_TOP_FORUM = -1;
  public static final int AREA_TOP_FORUM = 116;
  public static final int TOP_FORUM = 1;

  public static final int ALLOW_LOGIN_USERNAME_OR_PASSWD_ERROR_NUM = 5;

  private static Api mInstance = null;
  private String mToken = "guest";
  // TODO 使访问cookie时变得线程安全
  private CookieStorage mCookieStorage = null;
  private SharedPreferences mPreferences = null;

  public static Api getInstance() {
    if (mInstance == null) {
      mInstance = new Api();
    }
    return mInstance;
  }

  /**
   * 设置context，初始化上下文相关的属性
   *
   * @param con 要设置的 context
   */
  public void setmCon(Context con) {
    if (con == null) return;
    this.mPreferences = PreferenceManager.getDefaultSharedPreferences(con);
    this.mCookieStorage = new CookieStorage(new ObjStorage(this.mPreferences));
    this.getForumToken();
  }

  /**
   * @return 看雪安全论坛的cookie存储管理类
   */
  public CookieStorage getCookieStorage() {
    return this.mCookieStorage;
  }

  public Map<String, String> getCookieHeader() {
    if (!isLogin()) return null;
    Map<String, String> header = new HashMap<>();
    header.put("Cookie", mCookieStorage.getCookies());
    return header;
  }

  public void requestJSONObjectByGet(String url,
      VolleyHelper.ResponseListener<JSONObject> listener) {
    if (isLogin()) {

      VolleyHelper.requestJSONObjectWithHeader(Request.Method.GET, url, null, listener,
          getCookieHeader());
    } else {
      VolleyHelper.requestJSONObject(Request.Method.GET, url, null, listener);
    }
  }

  public void requestJSONObjectByPost(String url,
      VolleyHelper.ResponseListener<JSONObject> listener, Map<String, String> param) {
    VolleyHelper.requestJSONObject(Request.Method.POST, url, null, listener);
  }

  /**
   * 获取securitytoken，securitytoken在做post操作时需要提交。处于登录状态得到正常token，
   * 非登录状态得到字符串guest
   */
  public void getForumToken() {
    String url = DOMAIN + PATH + "getsecuritytoken.php?" + STYLE;
    VolleyHelper.ResponseListener<JSONObject> responseListener =
        new VolleyHelper.ResponseListener<JSONObject>() {
          @Override public void onErrorResponse(VolleyError volleyError) {

          }

          @Override public void onResponse(JSONObject jsonObject) {
            try {
              LogHelper.e(jsonObject.getString("securitytoken"));
              mToken = jsonObject.getString("securitytoken");
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        };
    requestJSONObjectByGet(url, responseListener);
  }

  /**
   * securitytoken的setter接口
   */
  public void setToken(String token) {
    if (token == null) return;
    this.mToken = token;
  }

  /**
   * @return 处于登录状态返回true，反之返回false
   */
  public boolean isLogin() {
    return this.mCookieStorage.hasCookie("bbsessionhash");
  }

  /**
   * 获取看雪的版块列表
   */
  public void getForumHomePage(final VolleyHelper.ResponseListener<JSONObject> responseListener) {
    String url = DOMAIN + PATH + "index.php?" + STYLE;
    VolleyHelper.requestJSONObjectWithHeader(Request.Method.GET, url, null, responseListener,
        getCookieHeader());
  }

  /**
   * 获取指定版块的主题列表
   *
   * @param id 版块id
   * @param page 版块主题列表的页码
   */
  public void getForumDisplayPage(int id, int page,
      final VolleyHelper.ResponseListener<JSONObject> responseListener) {
    String url =
        DOMAIN + PATH + "forumdisplay.php?" + STYLE + "&f=" + id + "&page=" + page + "&order=desc";
    requestJSONObjectByGet(url, responseListener);
  }

  /**
   * 获取指定主题中的帖子列表
   *
   * @param id 主题id
   * @param page 主题帖子列表页码
   */
  public void getForumShowthreadPage(int id, int page,
      final VolleyHelper.ResponseListener<JSONObject> responseListener) {
    String url = DOMAIN + PATH + "showthread.php?" + STYLE + "&t=" + id + "&page=" + page;
    requestJSONObjectByGet(url, responseListener);
  }

  /**
   * 获取完整的帖子内容。帖子内容较长时，看雪默认只传输缩略内容，可通过该接口获取完整内容。
   *
   * @param id 帖子id
   */
  public void getForumFullThread(int id,
      final VolleyHelper.ResponseListener<String> responseListener) {
    String url = DOMAIN + PATH + "showpost.php?" + STYLE + "&p=" + id;
    VolleyHelper.requestStringGet(url, responseListener);
  }

  /**
   * 登录看雪
   */
  public void login(String uname, String passwd, final HttpClientUtil.NetClientCallback callback) {
    String url = DOMAIN + PATH + "login.php?do=login" + "&" + STYLE;
    HttpClientUtil hcu = new HttpClientUtil(url, HttpClientUtil.METHOD_POST, callback);
    hcu.addParam("vb_login_username", uname);
    hcu.addParam("do", "login");
    hcu.addParam("cookieuser", "1");
    hcu.addParam("securitytoken", "guest");
    hcu.addParam("vb_login_md5password", SimpleHASH.md5(this.strToEnt(passwd.trim())));
    hcu.addParam("vb_login_md5password_utf", SimpleHASH.md5(passwd.trim()));
    hcu.asyncConnect();
  }

  /**
   * 设置登录用户的个人信息
   *
   * @param username 登录用户用户名
   * @param id 登录用户id
   * @param isavatar 登录用户是否有头像
   * @param email 登录用户email地址
   */
  public void setLoginUserInfo(String username, int id, int isavatar, String email) {
    if (username == null) return;
    String lastName = mPreferences.getString("username", "");
    SharedPreferences.Editor editor = this.mPreferences.edit();

    editor.putString("username", username);
    editor.putInt("userid", id);
    editor.putInt("isavatar", isavatar);

    if (!(lastName.equals(username) && TextUtils.isEmpty(email))) {
      editor.putString("email", email);
    }

    editor.apply();
  }

  /**
   * @return 用户名
   */
  public String getLoginUserName() {
    return this.mPreferences.getString("username", "");
  }

  /**
   * @return 用户id
   */
  public int getLoginUserId() {
    return this.mPreferences.getInt("userid", -1);
  }

  /**
   * @return 用户有头像返回true
   */
  public int getIsAvatar() {
    return this.mPreferences.getInt("isavatar", 0);
  }

  /**
   * @return 用户email地址
   */
  public String getEmail() {
    return this.mPreferences.getString("email", "");
  }

  /**
   * 清除登录用户个人信息
   */
  public void clearLoginData() {
    this.mCookieStorage.clearAll();
    SharedPreferences.Editor editor = this.mPreferences.edit();
    editor.remove("username");
    editor.remove("userid");
    editor.remove("isavatar");
    editor.apply();
  }

  /**
   * 登出
   */
  public void logout(final VolleyHelper.ResponseListener<JSONObject> responseListener) {
    String url = DOMAIN + PATH + "login.php?do=logout&logouthash=" + mToken + "&" + STYLE;
    VolleyHelper.requestJSONObject(Request.Method.GET, url, null, responseListener);
  }

  /**
   * 回复主题
   *
   * @param id 主题id
   * @param msg 回复内容
   */
  public void quickReply(int id, String msg, VolleyHelper.ResponseListener<JSONObject> listener) {
    String url = DOMAIN + PATH + "newreply.php?" + STYLE;
    Map<String, String> params = new HashMap<>();
    params.put("message", msg);
    params.put("t", id + "");
    params.put("fromquickreply", "1");
    params.put("do", "postreply");
    params.put("securitytoken", mToken);
    VolleyHelper.requestJSONObjectWithHeaderAndParams(Request.Method.POST, url, null, listener,
        getCookieHeader(), params);
  }
  //
  ///**
  // * 发布新主题
  // *
  // * @param id
  // *            版块id
  // * @param subject
  // *            主题标题
  // * @param msg
  // *            主题内容
  // * @param callback
  // */
  //public void newThread(int id, String subject, String msg,
  //		final NetClientCallback callback) {
  //	getNormalNewThread(id, subject, msg, callback).asyncConnect();
  //}
  //
  ///**
  // * 发布带有悬赏kx币的新主题
  // *
  // * @param id
  // * @param subject
  // * @param kxReward
  // *            悬赏kx币数值
  // * @param msg
  // * @param callback
  // */
  //public void newThread(int id, String subject, String kxReward, String msg,
  //		final NetClientCallback callback) {
  //	HttpClientUtil hcu = getNormalNewThread(id, subject, msg, callback);
  //	hcu.addParam("offer_Price", kxReward);
  //	hcu.asyncConnect();
  //}
  //
  //private HttpClientUtil getNormalNewThread(int id, String subject,
  //		String msg, final NetClientCallback callback) {
  //	String url = DOMAIN + PATH + "newthread.php?do=postthread" + "&f=" + id
  //			+ "&" + STYLE;
  //	HttpClientUtil hcu = new HttpClientUtil(url,
  //			HttpClientUtil.METHOD_POST, callback);
  //	hcu.addParam("subject", subject);
  //	hcu.addParam("message", msg);
  //	hcu.addParam("securitytoken", mToken);
  //	hcu.addParam("f", "" + id);
  //	hcu.addParam("do", "postthread");
  //	hcu.addCookie(this.mCookieStorage.getCookies());
  //	return hcu;
  //}
  //

  /**
   * 看雪的意见反馈接口
   */
  public void feedback(String name, String email, String msg,
      VolleyHelper.ResponseListener<String> listener) {
    String url = DOMAIN + PATH + "sendmessage.php?do=docontactus&" + STYLE;
    Map<String, String> params = new HashMap<>();
    params.put("name", name);
    params.put("email", email);
    params.put("message", msg);
    params.put("securitytoken", mToken);
    params.put("subject", "0");
    VolleyHelper.requestStringWithHeadersAndParams(Request.Method.POST, url, listener,
        getCookieHeader(), params);
  }

  /**
   * 检测指定版块下的主题列表是否有更新
   *
   * @param id 版块id
   * @param time 上次刷新的时间戳
   */
  public void checkNewPostInForumDisplayPage(int id, long time,
      VolleyHelper.ResponseListener<JSONObject> listener) {
    String url = DOMAIN + PATH + "forumdisplay.php?f=" + id + "&getnewpost=" + time + "&" + STYLE;
    VolleyHelper.requestJSONObjectWithHeaderAndParams(Request.Method.GET, url, null, listener,
        getCookieHeader(), null);
  }

  /**
   * 检测指定主题下的帖子列表是否有更新
   *
   * @param id 主题id
   * @param time 上次刷新的时间戳
   */
  public void checkNewPostInShowThreadPage(int id, long time,
      VolleyHelper.ResponseListener<JSONObject> listener) {
    String url = DOMAIN + PATH + "showthread.php?t=" + id + "&getnewpost=" + time + "&" + STYLE;
    VolleyHelper.requestJSONObjectWithHeaderAndParams(Request.Method.GET, url, null, listener,
        getCookieHeader(), null);
  }

  /**
   * 获取看雪用户头像的url
   *
   * @param userId 用户id
   */
  public String getUserHeadImageUrl(int userId) {
    return DOMAIN + PATH + "image.php?u=" + userId;
  }

  /**
   * 获取看雪帖子中附件图片的url
   *
   * @param id 附件id
   */
  public String getAttachmentImgUrl(int id) {
    return DOMAIN + PATH + "attachment.php?attachmentid=" + id + "&thumb=1&" + STYLE;
  }

  /**
   * 登录前用户密码预处理
   *
   * @param input 去掉首位空格的用户密码
   */
  private String strToEnt(String input) {
    String output = "";

    for (int i = 0; i < input.length(); i++) {
      int ucode = input.codePointAt(i);
      String tmp = "";

      if (ucode > 255) {
        while (ucode >= 1) {
          tmp = "0123456789".charAt(ucode % 10) + tmp;
          ucode /= 10;
        }

        if (tmp.equals("")) {
          tmp = "0";
        }

        tmp = "#" + tmp;
        tmp = "&" + tmp;
        tmp = tmp + ";";
        output += tmp;
      } else {
        output += input.charAt(i);
      }
    }
    return output;
  }

  /**
   * 检测指定用户个人信息列表
   *
   * @param id 用户id
   */
  public void getUserInfoPage(int id, final VolleyHelper.ResponseListener<JSONObject> listener) {
    String url = DOMAIN + PATH + "member.php?u=" + id + STYLE;
    VolleyHelper.requestStringWithHeadersAndParams(Request.Method.GET, url,
        new VolleyHelper.ResponseListener<String>() {
          @Override public void onErrorResponse(VolleyError volleyError) {
            listener.onErrorResponse(volleyError);
          }

          @Override public void onResponse(String s) {
            try {
              JSONObject object = new JSONObject(s.substring(1, s.length() - 1));
              listener.onResponse(object);
            } catch (JSONException e) {
              listener.onErrorResponse(new ParseError(e));
            }
          }
        }, getCookieHeader(), null);
  }
}
