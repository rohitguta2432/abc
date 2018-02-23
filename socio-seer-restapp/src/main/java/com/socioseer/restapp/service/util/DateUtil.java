package com.socioseer.restapp.service.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtil {

  public static long getCurrentTimeInMilliseconds() {
    return System.currentTimeMillis();
  }

  public static Date getDate(String date, String pattern) {
    DateFormat format = new SimpleDateFormat(pattern);
    try {
      return format.parse(date);
    } catch (ParseException e) {
      log.debug("Invalid date format " + date);
      return null;
    }
  }

  public static boolean isValidDate(String dateString, String pattern) {
    Date date = getDate(dateString, pattern);
    if (date == null) {
      return false;
    }
    return true;
  }

  public static List<Date> splitDuration(Date startDate, Date endDate, long splitSize) {
    long startMillis = startDate.getTime();
    long endMillis = endDate.getTime();
    List<Date> list = new ArrayList<Date>();
    while (startMillis <= endMillis) {
      list.add(new Date(startMillis));
      startMillis += splitSize;
    }
    return list;
  }

  public static Date getStartOfDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  public static Date getEndOfDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime();
  }
}
