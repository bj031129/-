package com.example.server.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
  public static Timestamp getCurrentTime() {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time = sdf.format(date.getTime());
    return Timestamp.valueOf(time);
  }
}
