package com.mislead.ikanxue.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 時間幫助類
 * 
 * @author Administrator
 *
 */
public class DateHelper {

	private static final String DATE_TIME_F = "yyyy-MM-dd HH:mm:ss";
	private static final String DATE_F = "yyyy-MM-dd";
	private static final String DAY_BEBIN_F = "yyyy-MM-dd 00:00:00";
	private static final String DAY_END_F = "yyyy-MM-dd 23:59:59";

  private static final long MILLIS_OF_HOUR = 60 * 60 * 1000;
  private static final long MILLIS_OF_DAY = 24 * MILLIS_OF_HOUR;

	/**
	 * 比較兩個日期
	 * 
	 * @param date1
	 * @param date2
	 * @return 0：date1==date2， <0:date1 < date2. >0:date1>date2
	 */
	public static int compareDate(Date date1, Date date2) {
		String dateStr1 = formateDateString(date1);
		String dateStr2 = formateDateString(date2);

		return dateStr1.compareTo(dateStr2);
	}

	/**
	 * 比較兩個日期
	 * 
	 * @param date1
	 * @param date2
	 * @return 0：date1==date2， <0:date1 < date2. >0:date1>date2
	 */
	public static int compareDateTime(Date date1, Date date2) {
		String dateStr1 = formateDateTimeString(date1);
		String dateStr2 = formateDateTimeString(date2);

		return dateStr1.compareTo(dateStr2);
	}

	/**
	 * 兩個日期是否是同一天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		return compareDate(date1, date2) == 0;
	}

	/**
	 * if date1 < date2
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isBefore(Date date1, Date date2) {
		return compareDateTime(date1, date2) < 0;
	}

	/**
	 * if date1 > date2
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isAfter(Date date1, Date date2) {
		return compareDateTime(date1, date2) > 0;
	}

	/**
	 * 獲得一天開始的時間字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDayBeginString(Date date) {
		return formateDate(date, DAY_BEBIN_F);
	}

	/**
	 * 獲得一天結束的時間字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDayEndString(Date date) {
		return formateDate(date, DAY_END_F);
	}

	/**
	 * 格式化 日期時間字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String formateDateTimeString(Date date) {
		return formateDate(date, DATE_TIME_F);
	}

	/**
	 * 格式化 日期字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String formateDateString(Date date) {
		return formateDate(date, DATE_F);
	}

	/**
	 * 格式化 日期字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String formateDate(Date date, String formate) {
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		return sdf.format(date);
	}

  /**
   * 获得两个时间相差的天数（舍尾）
   */
  public static int getDiffDays(long time1, long time2) {
    return (int) ((time1 - time2) / MILLIS_OF_DAY);
  }

  public static int getDiffDays(Date date1, Date date2) {
    return getDiffDays(date1.getTime(), date2.getTime());
  }
}
