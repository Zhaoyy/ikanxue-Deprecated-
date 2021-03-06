package com.mislead.ikanxue.app.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import com.android.volley.VolleyError;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.activity.LoginActivity;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.view.ConfirmDialog;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

/**
 * DownloadHelper
 * Created by zhaoyy
 * on 15-10-18.
 */
public class DownloadHelper {

  private static final Pattern pattern = Pattern.compile("a href=\"(.*?)\"");
  private String DirName;
  private static final String ikxDir = "ikanxue";

  private Context context;
  private DownloadManager downloadManager;

  private DownloadHelper(Context context) {
    this.context = context;
    context = context.getApplicationContext();
    downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      DirName =
          Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ikxDir;
    } else {
      DirName = Environment.getDataDirectory().getAbsolutePath() + File.separator + ikxDir;
    }
  }

  // single instance
  private static DownloadHelper instance;

  public static DownloadHelper getInstance(Context context) {
    if (instance == null) {
      synchronized (DownloadHelper.class) {
        if (instance == null) {
          instance = new DownloadHelper(context);
        }
      }
    }
    return instance;
  }

  /**
   * 如果服务器不支持中文路径的情况下需要转换url的编码。
   */
  public String encodeGB(String string) {
    //转换中文编码
    String split[] = string.split("/");
    for (int i = 1; i < split.length; i++) {
      try {
        split[i] = URLEncoder.encode(split[i], "GB2312");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      split[0] = split[0] + "/" + split[i];
    }
    split[0] = split[0].replaceAll("\\+", "%20");//处理空格
    return split[0];
  }

  public void addDownloadTask(final Context context, final int id, final String fileName) {

    String content = context.getString(R.string.download_note, fileName);
    final boolean isLogin = Api.getInstance().isLogin();

    LogHelper.e(DirName);
    if (!isLogin) {
      content = context.getString(R.string.need_login);
    } else {
      File file = new File(DirName, fileName);

      if (file.exists()) {
        content = context.getString(R.string.file_exists, fileName);
      }
    }

    int theme_id = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);
    ConfirmDialog dialog =
        new ConfirmDialog(context, theme_id, content, new ConfirmDialog.OnConfirmListener() {
          @Override public void onConfirm() {
            if (isLogin) {
              downloadAttachment(id, fileName);
            } else {
              context.startActivity(new Intent(context, LoginActivity.class));
            }
          }
        });
    dialog.show();
  }

  private void downloadAttachment(int id, final String fileName) {
    Api.getInstance().getForumPCHtml(id, new VolleyHelper.ResponseListener<String>() {
      @Override public void onErrorResponse(VolleyError error) {
        downloadUrl(null, null);
      }

      @Override public void onResponse(String response) {
        String url = null;
        try {
          Parser parser = new Parser(response);
          HtmlPage page = new HtmlPage(parser);
          parser.visitAllNodesWith(page);

          NodeList nodes = page.getBody();
          StringFilter filter = new StringFilter(fileName);
          nodes = nodes.extractAllNodesThatMatch(filter, true);

          for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.elementAt(i);
            Matcher matcher = pattern.matcher(node.getParent().getText());

            if (matcher.find()) {
              url = matcher.group(1);
              break;
            }
          }
        } catch (ParserException e) {
          e.printStackTrace();
        }
        downloadUrl(Api.DOMAIN + Api.PATH + url, fileName);
      }
    });
  }

  private boolean isNUllOrEmptyUrl(String url) {
    return url == null || url.trim().length() == 0 || url.trim().toLowerCase().equals("null");
  }

  private void downloadUrl(String url, String fileName) {

    if (isNUllOrEmptyUrl(url)) {
      ToastHelper.toastShort(context, "获取文件下载地址失败！");
      return;
    }

    LogHelper.e(url);
    //url = Api.DOMAIN + Api.PATH + "attachment.php?attachmentid=99762&amp;d=1441030101";
    //开始下载
    Uri resource = Uri.parse(url);
    DownloadManager.Request request = new DownloadManager.Request(resource);
    request.addRequestHeader("Cookie", Api.getInstance().getPcStyleCookieString());
    request.setAllowedNetworkTypes(
        DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
    request.setAllowedOverRoaming(false);
    //设置文件类型
    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    String mimeString =
        mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
    request.setMimeType(mimeString);
    //在通知栏中显示
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    request.setVisibleInDownloadsUi(true);
    //sdcard的目录下的ikanxue文件夹
    request.setDestinationInExternalPublicDir(ikxDir, fileName);
    request.setTitle(fileName);
    long id = downloadManager.enqueue(request);
  }
}
