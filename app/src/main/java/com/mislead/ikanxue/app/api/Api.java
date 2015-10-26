package com.mislead.ikanxue.app.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.model.CookieStorage;
import com.mislead.ikanxue.app.model.KanxueResponse;
import com.mislead.ikanxue.app.model.ObjStorage;
import com.mislead.ikanxue.app.net.HttpClientUtil;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ��ѩ��ȫ��̳����api��
 *
 * @author fanhexin
 */
public class Api {
  public static final String DOMAIN = "http://bbs.pediy.com";
  public static final String PATH = "/";
  /**
   * ��ѩ��׿�ͻ���ר�õ�ģ��id����ͨ���ڿ�ѩ��һurl�����ģ������鿴���ӿڷ�������
   */
  public static final String STYLE = "styleid=12";

  public static final String PC_STYLE = "styleid=1";

  // ��¼����״̬��
  public static final int LOGIN_SUCCESS = 0;
  public static final int LOGIN_FAIL_LESS_THAN_FIVE = 1; // �û��������������5������
  public static final int LOGIN_FAIL_MORE_THAN_FIVE = 2; // ��¼ʧ�ܴ�������5��,15���Ӻ�ſɼ�����¼

  // ����������״̬��
  public static final int NEW_POST_SUCCESS = 0;
  public static final int NEW_POST_FAIL_WITHIN_THIRTY_SECONDS = 1; // ��ʮ���ڷ�������
  public static final int NEW_POST_FAIL_WITHIN_FIVE_MINUTES = 2; // 5�����ڷ���ͬ����
  public static final int NEW_POST_FAIL_NOT_ENOUGH_KX = 3; // ��ѩ�Ҳ���

  // һЩ����id��
  public static final int TEMPORARY_FORUM_ID = 131; // ��ѩ��ʱ��Ա���id
  public static final int HELP_FORUM_ID = 20; // ��ѩ�����ʴ����id
  // public static final int SOFTWARE_DEBUG_FORUM_ID = 4; //������԰���id
  public static final int GET_JOB_FORUM_ID = 47; // ��Ƹ���id
  public static final int POST_CONTENT_SIZE_MIN = 6; // �������������С����
  public static final int NEW_FORUM_ID = 153; // �������ϰ��id
  public static final int LIFE_FORUM_ID = 45; // �����������id
  public static final int SECURITY_FORUM_ID = 61; // �����������id

  // �����ö�����
  public static final int GLOBAL_TOP_FORUM = -1;
  public static final int AREA_TOP_FORUM = 116;
  public static final int TOP_FORUM = 1;

  public static final int ALLOW_LOGIN_USERNAME_OR_PASSWD_ERROR_NUM = 5;

  private static Api mInstance = null;
  private String mToken = "guest";
  // TODO ʹ����cookieʱ����̰߳�ȫ
  private CookieStorage mCookieStorage = null;
  private SharedPreferences mPreferences = null;

  public static Api getInstance() {
    if (mInstance == null) {
      mInstance = new Api();
    }
    return mInstance;
  }

  /**
   * ����context����ʼ����������ص�����
   *
   * @param con Ҫ���õ� context
   */
  public void setmCon(Context con) {
    if (con == null) return;
    this.mPreferences = PreferenceManager.getDefaultSharedPreferences(con);
    this.mCookieStorage = new CookieStorage(new ObjStorage(this.mPreferences));
    this.getForumToken();
  }

  /**
   * @return ��ѩ��ȫ��̳��cookie�洢������
   */
  public CookieStorage getCookieStorage() {
    return this.mCookieStorage;
  }

  public Map<String, String> getCookieHeader() {
    return getCookieHeader(false);
  }

  public Map<String, String> getCookieHeader(boolean pcStyle) {
    if (!isLogin()) return null;
    Map<String, String> header = new HashMap<>();
    header.put("Cookie", pcStyle ? getPcStyleCookieString() : getPcStyleCookieString());
    return header;
  }

  public String getCookieString() {
    return isLogin() ? mCookieStorage.getCookies() : "";
  }

  public String getPcStyleCookieString() {
    return getCookieString().replace(STYLE, PC_STYLE);
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

  public void requestStringByGet(String url, VolleyHelper.ResponseListener<String> listener) {
    if (isLogin()) {

      VolleyHelper.requestStringWithHeadersAndParams(Request.Method.GET, url, listener,
          getCookieHeader(), null);
    } else {
      VolleyHelper.requestStringGet(url, listener);
    }
  }

  public void requestJSONObjectByPost(String url,
      VolleyHelper.ResponseListener<JSONObject> listener, Map<String, String> param) {
    VolleyHelper.requestJSONObject(Request.Method.POST, url, null, listener);
  }

  /**
   * ��ȡsecuritytoken��securitytoken����post����ʱ��Ҫ�ύ�����ڵ�¼״̬�õ�����token��
   * �ǵ�¼״̬�õ��ַ���guest
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
   * securitytoken��setter�ӿ�
   */
  public void setToken(String token) {
    if (token == null) return;
    this.mToken = token;
  }

  /**
   * @return ���ڵ�¼״̬����true����֮����false
   */
  public boolean isLogin() {
    return this.mCookieStorage.hasCookie("bbsessionhash");
  }

  /**
   * ��ȡ��ѩ�İ���б�
   */
  public void getForumHomePage(final VolleyHelper.ResponseListener<JSONObject> responseListener) {
    String url = DOMAIN + PATH + "index.php?" + STYLE;
    VolleyHelper.requestJSONObjectWithHeader(Request.Method.GET, url, null, responseListener,
        getCookieHeader());
  }

  /**
   * ��ȡָ�����������б�
   *
   * @param id ���id
   * @param page ��������б��ҳ��
   */
  public void getForumDisplayPage(int id, int page,
      final VolleyHelper.ResponseListener<JSONObject> responseListener) {
    String url =
        DOMAIN + PATH + "forumdisplay.php?" + STYLE + "&f=" + id + "&page=" + page + "&order=desc";
    requestJSONObjectByGet(url, responseListener);
  }

  /**
   * ��ȡָ�������е������б�
   *
   * @param id ����id
   * @param page ���������б�ҳ��
   */
  public void getForumShowthreadPage(int id, int page,
      final VolleyHelper.ResponseListener<String> responseListener) {
    String url = DOMAIN + PATH + "showthread.php?" + STYLE + "&t=" + id + "&page=" + page;
    requestStringByGet(url, responseListener);
  }

  public void getForumPCHtml(int id, VolleyHelper.ResponseListener<String> responseListener) {
    String url = DOMAIN + PATH + "showthread.php?" + "t=" + id;
    VolleyHelper.requestStringWithHeadersAndParams(Request.Method.GET, url, responseListener,
        getCookieHeader(true), null);
  }

  /**
   * ��ȡ�������������ݡ��������ݽϳ�ʱ����ѩĬ��ֻ�����������ݣ���ͨ���ýӿڻ�ȡ�������ݡ�
   *
   * @param id ����id
   */
  public void getForumFullThread(int id,
      final VolleyHelper.ResponseListener<String> responseListener) {
    String url = DOMAIN + PATH + "showpost.php?" + STYLE + "&p=" + id;
    VolleyHelper.requestStringWithHeadersAndParams(Request.Method.GET, url, responseListener,
        getCookieHeader(), null);
  }

  /**
   * ��¼��ѩ
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
   * 用户登录
   */
  public void login(String uname, String passwd,
      VolleyHelper.ResponseListener<KanxueResponse> listener) {
    String url = DOMAIN + PATH + "login.php?do=login" + "&" + STYLE;
    Map<String, String> params = new HashMap<>();
    params.put("vb_login_username", uname);
    params.put("do", "login");
    params.put("cookieuser", "1");
    params.put("securitytoken", "guest");
    params.put("vb_login_md5password", SimpleHASH.md5(this.strToEnt(passwd.trim())));
    params.put("vb_login_md5password_utf", SimpleHASH.md5(passwd.trim()));
    VolleyHelper.requestKanxuePost(url, listener, params);
  }

  /**
   * ���õ�¼�û��ĸ�����Ϣ
   *
   * @param username ��¼�û��û���
   * @param id ��¼�û�id
   * @param isavatar ��¼�û��Ƿ���ͷ��
   * @param email ��¼�û�email��ַ
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
   * @return �û���
   */
  public String getLoginUserName() {
    return this.mPreferences.getString("username", "");
  }

  /**
   * @return �û�id
   */
  public int getLoginUserId() {
    return this.mPreferences.getInt("userid", -1);
  }

  /**
   * @return �û���ͷ�񷵻�true
   */
  public int getIsAvatar() {
    return this.mPreferences.getInt("isavatar", 0);
  }

  /**
   * @return �û�email��ַ
   */
  public String getEmail() {
    return this.mPreferences.getString("email", "");
  }

  /**
   * ���浱ǰ�û�����
   */
  public void setLoginUserType(String type) {
    SharedPreferences.Editor editor = this.mPreferences.edit();
    editor.putString("usertype", type);
    editor.commit();
  }

  public String getLoginUserType() {
    return mPreferences.getString("usertype", "");
  }

  /**
   * �����¼�û�������Ϣ
   */
  public void clearLoginData() {
    this.mCookieStorage.clearAll();
    SharedPreferences.Editor editor = this.mPreferences.edit();
    editor.remove("username");
    editor.remove("userid");
    editor.remove("isavatar");
    editor.remove("usertype");
    editor.apply();
  }

  /**
   * �ǳ�
   */
  public void logout(final VolleyHelper.ResponseListener<JSONObject> responseListener) {
    String url = DOMAIN + PATH + "login.php?do=logout&logouthash=" + mToken + "&" + STYLE;
    VolleyHelper.requestJSONObject(Request.Method.GET, url, null, responseListener);
  }

  /**
   * �ظ�����
   *
   * @param id ����id
   * @param msg �ظ�����
   */
  public void quickReply(int id, String msg, VolleyHelper.ResponseListener<String> listener) {
    String url = DOMAIN + PATH + "newreply.php?" + STYLE;
    Map<String, String> params = new HashMap<>();
    params.put("message", msg);
    params.put("t", id + "");
    params.put("fromquickreply", "1");
    params.put("do", "postreply");
    params.put("securitytoken", mToken);
    VolleyHelper.requestStringWithHeadersAndParams(Request.Method.POST, url, listener,
        getCookieHeader(), params);
  }

  /**
   * ������������kx�ҵ�������
   */
  public void newThreadWithoutReward(int id, String subject, String msg,
      VolleyHelper.ResponseListener<String> responseListener) {
    newThread(id, subject, msg, null, responseListener);
  }

  public void newThread(int id, String subject, String msg, String kxReward,
      VolleyHelper.ResponseListener<String> responseListener) {
    String url = DOMAIN + PATH + "newthread.php?do=postthread" + "&f=" + id + "&" + STYLE;
    Map<String, String> params = new HashMap<>();
    params.put("subject", subject);
    params.put("message", msg);
    params.put("securitytoken", mToken);
    params.put("f", id + "");

    if (kxReward != null) {
      params.put("offer_Price", kxReward);
    }

    params.put("do", "postthread");
    VolleyHelper.requestStringWithHeadersAndParams(Request.Method.POST, url, responseListener,
        getCookieHeader(), params);
  }

  /**
   * ��ѩ����������ӿ�
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
   * ���ָ������µ������б��Ƿ��и���
   *
   * @param id ���id
   * @param time �ϴ�ˢ�µ�ʱ���
   */
  public void checkNewPostInForumDisplayPage(int id, long time,
      VolleyHelper.ResponseListener<JSONObject> listener) {
    String url = DOMAIN + PATH + "forumdisplay.php?f=" + id + "&getnewpost=" + time + "&" + STYLE;
    VolleyHelper.requestJSONObjectWithHeaderAndParams(Request.Method.GET, url, null, listener,
        getCookieHeader(), null);
  }

  /**
   * ���ָ�������µ������б��Ƿ��и���
   *
   * @param id ����id
   * @param time �ϴ�ˢ�µ�ʱ���
   */
  public void checkNewPostInShowThreadPage(int id, long time,
      VolleyHelper.ResponseListener<String> listener) {
    String url = DOMAIN + PATH + "showthread.php?t=" + id + "&getnewpost=" + time + "&" + STYLE;
    VolleyHelper.requestStringWithHeadersAndParams(Request.Method.GET, url, listener,
        getCookieHeader(), null);
  }

  /**
   * ��ȡ��ѩ�û�ͷ���url
   *
   * @param userId �û�id
   */
  public String getUserHeadImageUrl(int userId) {
    return DOMAIN + PATH + "image.php?u=" + userId;
  }

  /**
   * ��ȡ��ѩ�����и���ͼƬ��url
   *
   * @param id ����id
   */
  public String getAttachmentImgUrl(int id) {
    return DOMAIN + PATH + "attachment.php?attachmentid=" + id + "&thumb=1&" + STYLE;
  }

  public String getImageAttachmentPCUrl(int id) {
    return DOMAIN + PATH + "attachment.php?attachmentid=" + id + "&thumb=1&" + PC_STYLE;
  }

  /**
   * ��¼ǰ�û�����Ԥ����
   *
   * @param input ȥ����λ�ո���û�����
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
   * ���ָ���û�������Ϣ�б�
   *
   * @param id �û�id
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
