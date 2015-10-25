package com.mislead.ikanxue.app.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.mislead.ikanxue.app.R;
import com.mislead.ikanxue.app.activity.LoginActivity;
import com.mislead.ikanxue.app.api.Api;
import com.mislead.ikanxue.app.base.Constants;
import com.mislead.ikanxue.app.model.ForumThreadObject;
import com.mislead.ikanxue.app.view.ConfirmDialog;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.http.HttpVersion;

/**
 * DownloadHelper
 * Created by zhaoyy
 * on 15-10-18.
 */
public class DownloadHelper {

  private Context context;
  private DownloadManager downloadManager;

  private DownloadHelper(Context context) {
    this.context = context;
    context = context.getApplicationContext();
    downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
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

  public void addDownloadTask(final Context context,
      final ForumThreadObject.ThumbnailattachmentsEntity entity) {

    final String content = context.getString(R.string.download_note, entity.getFilename());
    String needLogin = context.getString(R.string.need_login);
    final boolean isLogin = Api.getInstance().isLogin();
    int theme_id = ShPreUtil.getInt(Constants.THEME_ID, R.style.Theme_Dark);
    ConfirmDialog dialog = new ConfirmDialog(context, theme_id, isLogin ? content : needLogin,
        new ConfirmDialog.OnConfirmListener() {
          @Override public void onConfirm() {
            if (isLogin) {
              downloadAttachment(entity);
            } else {
              context.startActivity(new Intent(context, LoginActivity.class));
            }
          }
        });
    dialog.show();
  }

  private void downloadAttachment(ForumThreadObject.ThumbnailattachmentsEntity entity) {
    String url = Api.getInstance().getAttachmentImgUrl(entity.getAttachmentid());
    //开始下载
    Uri resource = Uri.parse(encodeGB(url));
    DownloadManager.Request request = new DownloadManager.Request(resource);
    request.addRequestHeader("Cookie", Api.getInstance().getCookieString());
    request.addRequestHeader("http.protocol.version", HttpVersion.HTTP_1_1.toString());
    request.addRequestHeader("http.useragent",
        "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
    request.addRequestHeader("http.protocol.expect-continue", Boolean.FALSE.toString());
    request.addRequestHeader("http.protocol.content-charset", "UTF-8");
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
    request.setDestinationInExternalPublicDir("ikanxue", entity.getFilename());
    request.setTitle(entity.getFilename());
    long id = downloadManager.enqueue(request);
  }
}
