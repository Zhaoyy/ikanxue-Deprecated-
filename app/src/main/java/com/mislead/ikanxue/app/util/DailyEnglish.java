package com.mislead.ikanxue.app.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import com.mislead.ikanxue.app.model.DailyEnglishObject;
import java.util.Date;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

/**
 * DailyEnglish
 *
 * @author Mislead
 *         DATE: 2015/9/21
 *         DESC:
 **/
public class DailyEnglish {

  private static String TAG = "DailyEnglish";

  public static final String ENG_API = "http://news.iciba.com/dailysentence";
  public static final String PIC_URL = "http://cdn.iciba.com/news/word/%s.jpg";

  private HandlerThread handlerThread =
      new HandlerThread("back_handler", Process.THREAD_PRIORITY_BACKGROUND);
  private Handler backHandle;

  public DailyEnglishObject getDailyEnglish(String s) {
    DailyEnglishObject object = new DailyEnglishObject();

    try {
      Parser parser = new Parser(s);
      HtmlPage page = new HtmlPage(parser);
      parser.visitAllNodesWith(page);

      NodeList nodes = page.getBody();
      NodeFilter filter =
          new LinkRegexFilter("http://news.iciba.com/dailysentence/detail-\\d+.html");
      nodes = nodes.extractAllNodesThatMatch(filter, true);

      for (int i = 0; i < nodes.size(); i++) {
        LinkTag linkTag = (LinkTag) nodes.elementAt(i);

        if (linkTag.getParent().getText().contains("class=\"en\"")) {
          object.setContent(linkTag.getChildrenHTML());
        }

        if (linkTag.getParent().getText().contains("class=\"cn\"")) {
          object.setNote(linkTag.getChildrenHTML());
        }
      }
      String today = DateHelper.formateDateString(new Date());
      object.setPicture(String.format(PIC_URL, today));
      object.setDateline(today);

      return object;
    } catch (ParserException e) {
      e.printStackTrace();
      return null;
    }
  }
}
