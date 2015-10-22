package com.mislead.ikanxue.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.RequestFuture;
import com.mislead.ikanxue.app.util.AndroidHelper;
import com.mislead.ikanxue.app.util.LogHelper;
import com.mislead.ikanxue.app.volley.ImageRequest;
import com.mislead.ikanxue.app.volley.VolleyHelper;
import com.mislead.ikanxue.app.volley.VolleyImageGetter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * ImageClickableTextView
 *
 * @author Mislead
 *         DATE: 2015/10/22
 *         DESC:
 *
 *         http://stackoverflow.com/questions/32701331/rxjava-and-volley-requests
 *         https://github.com/zzhoujay/RichText/blob/master/app/src/main/java/zhou/widget/RichText.java
 **/
public class ImageClickableTextView extends TextView {

  private static final String TAG = "ImageClickableTextView";

  private OnImageClickListener listener;
  private CompositeSubscription compositeSubscription;
  private String htmlString;

  public void setListener(OnImageClickListener listener) {

    if (this.listener == null) {
      this.listener = listener;
    }
  }

  public ImageClickableTextView(Context context) {
    super(context);
  }

  public ImageClickableTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ImageClickableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setHtmlString(String htmlString) {
    this.htmlString = htmlString;
    Spanned spanned = Html.fromHtml(htmlString, VolleyImageGetter.from(this), null);
    // get img src list
    SpannableStringBuilder spannableStringBuilder;

    if (spanned instanceof SpannableStringBuilder) {
      spannableStringBuilder = (SpannableStringBuilder) spanned;
    } else {
      spannableStringBuilder = new SpannableStringBuilder(spanned);
    }
    setText(spannableStringBuilder);
    ImageSpan[] imageSpans =
        spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ImageSpan.class);

    if (imageSpans != null && imageSpans.length > 0) {
      List<String> sources = new ArrayList<>();
      for (ImageSpan span : imageSpans) {
        final String url = span.getSource();
        if (!AndroidHelper.getImageDiskCache().hasBitMap(VolleyHelper.getCacheKey(url))) {
          LogHelper.e("add key:" + url);
          sources.add(url);
        }
      }

      if (sources.size() > 0) {
        getImageInRxWay(sources);
      } else {
        resetTextAndClickable();
      }
    }
    setMovementMethod(LinkMovementMethod.getInstance());
  }

  // get image from net in rxjava way
  private void getImageInRxWay(List<String> sources) {

    if (compositeSubscription == null) {
      compositeSubscription = new CompositeSubscription();
    }

    Subscription subscription =
        Observable.from(sources)
            .flatMap(new Func1<String, Observable<BitmapEntry>>() {
              @Override public Observable<BitmapEntry> call(String s) {
                try {
                  return Observable.just(getImageBitmap(s));
                } catch (ExecutionException | InterruptedException e) {
                  LogHelper.e(e.getMessage());
                  return Observable.error(e);
                }
              }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<BitmapEntry>() {
              @Override public void onCompleted() {
                LogHelper.e("onCompleted");
                resetTextAndClickable();
              }

              @Override public void onError(Throwable e) {
                LogHelper.e(e.getMessage());
              }

              @Override public void onNext(BitmapEntry entry) {
                // add bitmap to cache
                if (entry.getBitmap() != null) {
                  AndroidHelper.getImageDiskCache().putBitmap(entry.getKey(), entry.getBitmap());
                }
              }
            });

    compositeSubscription.add(subscription);
  }

  private void resetTextAndClickable() {
    Spanned spanned =
        Html.fromHtml(htmlString, VolleyImageGetter.from(ImageClickableTextView.this), null);
    SpannableStringBuilder spannableStringBuilder;

    if (spanned instanceof SpannableStringBuilder) {
      spannableStringBuilder = (SpannableStringBuilder) spanned;
    } else {
      spannableStringBuilder = new SpannableStringBuilder(spanned);
    }

    ImageSpan[] imageSpans =
        spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ImageSpan.class);

    if (imageSpans != null && imageSpans.length > 0) {
      for (ImageSpan span : imageSpans) {
        final String url = span.getSource();

        int start = spannableStringBuilder.getSpanStart(span);
        int end = spannableStringBuilder.getSpanEnd(span);

        ClickableSpan clickableSpan = new ClickableSpan() {
          @Override public void onClick(View widget) {
            if (listener != null) {
              listener.imageClicked(url);
            }
          }
        };

        // remove other clickable span
        ClickableSpan[] clickableSpans =
            spannableStringBuilder.getSpans(start, end, ClickableSpan.class);

        if (clickableSpans != null && clickableSpans.length > 0) {
          for (ClickableSpan span1 : clickableSpans) {
            spannableStringBuilder.removeSpan(span1);
          }
        }
        // add image clickable span
        spannableStringBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
    }
    setText(spanned);
  }

  private BitmapEntry getImageBitmap(String url) throws ExecutionException, InterruptedException {
    RequestFuture<Bitmap> future = RequestFuture.newFuture();
    ImageRequest request = new ImageRequest(url, future, 0, 0, Bitmap.Config.RGB_565, future);
    VolleyHelper.addRequest2Queue(request);
    String key = VolleyHelper.getCacheKey(url);
    Bitmap bitmap = future.get();

    return new BitmapEntry(key, bitmap);
  }

  public interface OnImageClickListener {
    public void imageClicked(String imageUrl);
  }

  private class BitmapEntry {
    private String key;
    private Bitmap bitmap;

    public BitmapEntry(String key, Bitmap bitmap) {
      this.key = key;
      this.bitmap = bitmap;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public Bitmap getBitmap() {
      return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
      this.bitmap = bitmap;
    }
  }
}
