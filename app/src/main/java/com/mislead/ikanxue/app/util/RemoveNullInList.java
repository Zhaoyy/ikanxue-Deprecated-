package com.mislead.ikanxue.app.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * RemoveNullInList
 *
 * @author Mislead
 *         DATE: 2015/9/15
 *         DESC:
 **/
public class RemoveNullInList<T> {

  private static String TAG = "RemoveNullInList";

  public void removeNull(List<T> list) {

    if (list == null) {
      list = new ArrayList<T>();
    }

    Iterator<T> iterator = list.iterator();
    while (iterator.hasNext()) {
      T t = iterator.next();

      if (t == null) iterator.remove();
    }
  }
}
