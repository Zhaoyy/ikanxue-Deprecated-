package com.mislead.ikanxue.app.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mislead.ikanxue.app.model.KanxueResponse;
import java.util.Map;
import org.json.JSONObject;

/**
 * VolleyHelper
 *
 * @author Zhaoyy
 *         2015/6/29
 *         DESC:
 **/
public class VolleyHelper {

  private static String TAG = "VolleyHelper";

  private static RequestQueue queue = null;

  public static void init(Context context) {
    queue = Volley.newRequestQueue(context, new MHurlStack());
  }

  /**
   * requestStringGet
   *
   * @param url request url
   * @param listener handle request listener
   */
  public static void requestStringGet(String url, ResponseListener<String> listener) {
    StringRequest request = new StringRequest(Request.Method.GET, url, listener, listener);
    request.setShouldCache(true);
    request.setTag(url);
    queue.add(request);
  }

  public static void requestStringWithEncodingParams(int method, String url,
      ResponseListener<String> listener, final Map<String, String> params) {
    StringRequest request = new StringRequest(method, url, listener, listener) {
      @Override protected Map<String, String> getParams() throws AuthFailureError {
        if (params == null) {
          return super.getParams();
        } else {
          return params;
        }
      }

      @Override protected String getParamsEncoding() {
        return super.getParamsEncoding();
      }
    };
    request.setShouldCache(true);
    request.setTag(url);
    queue.add(request);
  }

  public static void requestJSONObjectWithEncodingParams(int method, String url,
      ResponseListener<JSONObject> listener, final Map<String, String> params) {
    JsonObjectRequest request = new JsonObjectRequest(method, url, null, listener, listener) {
      @Override protected Map<String, String> getParams() throws AuthFailureError {
        if (params == null) {
          return super.getParams();
        } else {
          return params;
        }
      }

      @Override protected String getParamsEncoding() {
        return super.getParamsEncoding();
      }
    };
    request.setShouldCache(true);
    request.setTag(url);
    queue.add(request);
  }

  public static void requestJSONObject(int method, String url, JSONObject requestJson,
      ResponseListener<JSONObject> listener) {

    requestJSONObjectWithHeader(method, url, requestJson, listener, null);
  }

  public static void requestJSONObjectWithHeader(int method, String url, JSONObject requestJson,
      ResponseListener<JSONObject> listener, final Map<String, String> header) {
    requestJSONObjectWithHeaderAndParams(method, url, requestJson, listener, header, null);
  }

  public static void requestJSONObjectWithParams(int method, String url, JSONObject requestJson,
      ResponseListener<JSONObject> listener, final Map<String, String> params) {
    requestJSONObjectWithHeaderAndParams(method, url, requestJson, listener, null, params);
  }

  public static void requestJSONObjectWithHeaderAndParams(int method, String url,
      JSONObject requestJson, ResponseListener<JSONObject> listener,
      final Map<String, String> header, final Map<String, String> params) {
    JsonObjectRequest request;
    request = new JsonObjectRequest(method, url, requestJson, listener, listener) {
      @Override public Map<String, String> getHeaders() throws AuthFailureError {
        if (header == null) {
          return super.getHeaders();
        } else {
          return header;
        }
      }

      @Override protected Map<String, String> getParams() throws AuthFailureError {
        if (params == null) {
          return super.getParams();
        } else {
          return params;
        }
      }
    };
    //request.setRetryPolicy(new DefaultRetryPolicy(500, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
    //    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    request.setTag(url);
    queue.add(request);
  }

  public static void requestKanxuePost(String url, ResponseListener<KanxueResponse> listener,
      Map<String, String> params) {
    KanxuePostJSONRequest request = new KanxuePostJSONRequest(url, listener);
    request.setParams(params);
    //request.setRetryPolicy(new DefaultRetryPolicy(500, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
    //    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    request.setTag(url);
    queue.add(request);
  }

  public static void requestPostWithJSONParams(String url, ResponseListener<JSONObject> listener,
      Map<String, String> params) {
    JsonObjectRequest request =
        new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), listener, listener);
    //request.setRetryPolicy(new DefaultRetryPolicy(500, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
    //    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    request.setTag(url);
    queue.add(request);
  }

  /**
   * requestImageWithoutCache
   * if success, you can get the bitmap you requested.
   * if you want cache the bitmap, use {@link #requestImageWithCache(String, ImageView,
   * ImageLoader.ImageCache, int, int, int, int)} or {@link #requestImageWithCacheSimple(String,
   * ImageLoader.ImageCache, ImageLoader.ImageListener)}
   *
   * @param url request url
   * @param maxWidth bitmap maximum width
   * @param maxHeight bitmap maximum height
   * @param config bitmap code type ARGB_8888,RGB_565,ALPHA_8
   * @param listener handler request listener
   */
  public static void requestImageWithoutCache(String url, int maxWidth, int maxHeight,
      Bitmap.Config config, ResponseListener<Bitmap> listener) {
    ImageRequest request = new ImageRequest(url, listener, maxWidth, maxHeight, config, listener);
    request.setShouldCache(true);
    request.setTag(url);
    queue.add(request);
  }

  public static void requestImageViewWithoutCache(String url, ResponseListener<Bitmap> listener) {
    requestImageViewWithoutCache(url, Bitmap.Config.RGB_565, listener);
  }

  public static void requestImageViewWithoutCache(String url, Bitmap.Config config,
      ResponseListener<Bitmap> listener) {
    requestImageWithoutCache(url, 0, 0, config, listener);
  }

  /**
   * requestImageWithCache
   * you can set default image & error image on the view.
   *
   * if not needed set default image ,you can use {@link #requestImageWithCacheSimple(String,
   * ImageLoader.ImageCache, ImageLoader.ImageListener)}
   *
   * @param url request url
   * @param imageView imageView to display image
   * @param cache image cache
   * @param defaultImageResId default image
   * @param errorImageResId error image
   * @param maxWidth maximum image width
   * @param maxHeight maximum image height
   */
  public static void requestImageWithCache(String url, ImageView imageView,
      ImageLoader.ImageCache cache, int defaultImageResId, int errorImageResId, int maxWidth,
      int maxHeight) {
    ImageLoader imageLoader = new ImageLoader(queue, cache);
    imageLoader.get(url,
        ImageLoader.getImageListener(imageView, defaultImageResId, errorImageResId), maxWidth,
        maxHeight);
  }

  public static void requestImageWithCache(String url, ImageView imageView,
      ImageLoader.ImageCache cache, int defaultImageResId, int errorImageResId) {
    requestImageWithCache(url, imageView, cache, defaultImageResId, errorImageResId, 0, 0);
  }

  /**
   * requestImageWithCacheSimple
   * request a image, cache it,and deal it in listener.
   * if you don't need deal, use {@link #requestAndCacheImage(String, ImageLoader.ImageCache)}
   *
   * @param url request url
   * @param cache image cache
   * @param listener image request listener
   */
  public static void requestImageWithCacheSimple(String url, ImageLoader.ImageCache cache,
      ImageLoader.ImageListener listener) {
    ImageLoader imageLoader = new ImageLoader(queue, cache);
    imageLoader.get(url, listener);
  }

  /**
   * requestAndCacheImage
   * just cahce a image for the next use.
   * if you want to deal it immediately, use {@link #requestImageWithCacheSimple(String,
   * ImageLoader.ImageCache, ImageLoader.ImageListener)}
   */
  public static void requestAndCacheImage(String url, ImageLoader.ImageCache cache) {
    requestImageWithCacheSimple(url, cache, new SimpleImageListener());
  }

  /**
   * getCacheKey
   * get cache key about image url, used for getImage from cache;
   *
   * @param url image url
   * @param maxWidth cache maximum width
   * @param maxHeight cache maximum height
   */
  public static String getCacheKey(String url, int maxWidth, int maxHeight) {
    return (new StringBuilder(url.length() + 12)).append("#W")
        .append(maxWidth)
        .append("#H")
        .append(maxHeight)
        .append(url)
        .toString();
  }

  public static String getCacheKey(String url) {
    return getCacheKey(url, 0, 0);
  }

  public static interface ResponseListener<T> extends Response.Listener<T>, Response.ErrorListener {

  }

  static class SimpleImageListener implements ImageLoader.ImageListener {

    @Override public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {

    }

    @Override public void onErrorResponse(VolleyError volleyError) {

    }
  }
}
