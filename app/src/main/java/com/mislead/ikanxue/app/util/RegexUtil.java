package com.mislead.ikanxue.app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RegxUtil
 *
 * @author Mislead
 *         DATE: 2015/7/9
 *         DESC:
 **/
public class RegexUtil {

  private static String TAG = "RegxUtil";

  private static final String REX_EMAIL =
      "([0-9A-Za-z\\-_\\.]+)@([0-9a-z]+\\.[a-z]{2,3}(\\.[a-z]{2})?)";

  public static boolean checkEmail(String email) {
    Pattern pattern = Pattern.compile(REX_EMAIL);

    Matcher matcher = pattern.matcher(email);

    return matcher.matches();
  }
}
