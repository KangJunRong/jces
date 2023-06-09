package com.ecp.jces.server.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ecp.jces.server.dc.service.applet.impl.AppletServiceImpl;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String DATETIME_PATTERNS = "MM/dd/yyyy HH:mm:ss";

    private static final String DATETIME_PATTERN_NOT_MIN = "yyyy-MM-dd HH:mm";

    private static final String DATETIME_PATTERN_New = "yyyy-MM-dd";

    private static final String DATETIME_PATTERN_WATER = "yyyy-MM";

    private static final String DATETIME_PATTERN_YEAR = "yyyy";

    private static final String DATETIME_PATTERN_WATER_Table = "yyyyMM";

    private static final String DATETIME_PATTERN_WECHAT_NOTIFY_TIME = "yyyyMMddHHmmss";

    private static final Calendar cal = Calendar.getInstance();

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    public static String format(Date date) {
        return DateFormatUtils.format(date, DATETIME_PATTERN);
    }

    // KJR
    public static String formats(Date date) {
        return DateFormatUtils.format(date, DATETIME_PATTERNS);
    }

    public static String formatsSort(Date date) {
        return DateFormatUtils.format(date, DATETIME_PATTERN_New);
    }

    public static String formatsNotMinutes(Date date) {
        return DateFormatUtils.format(date, DATETIME_PATTERN_NOT_MIN);
    }

    public static String formatsWater(Date date) {
        return DateFormatUtils.format(date, DATETIME_PATTERN_WATER);
    }

    public static String formatTableyyyyMM(Date date) {
        return DateFormatUtils.format(date, DATETIME_PATTERN_WATER_Table);
    }

    public static String formatsYear(Date date) {
        return DateFormatUtils.format(date, DATETIME_PATTERN_YEAR);
    }

    public static String reformat(String dateString, String oldPattern,
                                  String newPattern) throws ParseException {
        return DateFormatUtils.format(
                DateUtils.parseDate(dateString, oldPattern), newPattern);
    }

    // /去掉毫秒
    public static String formatForString(String date, String charparm) {
        return date.substring(0, date.indexOf(charparm));
    }

    public static String getDateString(Date date, int days) {
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return formatsSort(cal.getTime());
    }

    //时间相减
    public static String subtractDateString(Date date, int days) {
        cal.setTime(date);
        cal.set(Calendar.DATE, 0 - days);
        return format(cal.getTime());
    }

    //当前日期前几天或后几天
    public static Date getDate(Date date, int days) {
        cal.setTime(date);
        cal.set(Calendar.DATE, days);
        return cal.getTime();
    }

    //当前日期后1小时
    public static Date getNextHour(Date date,int i) {
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, i);
        return cal.getTime();
    }

    public static String subtractDate(Date date, int days) {
        cal.setTime(date);
        cal.set(Calendar.DATE, 0 - days);
        return formatsSort(cal.getTime());
    }


    // String to date
    public static Date StringToDate(String str) {
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format1.parse(str);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }
        return date;
    }

    public static Date StringToDateTime(String str) {
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format1.parse(str);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }
        return date;
    }

    public static int getYear() {
        return cal.get(Calendar.YEAR);
    }

    public static int getMonth() {
        return cal.get(Calendar.MONTH) + 1;
    }

    public static int getHour() {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static int daysBetween(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days)) + 1;
    }

    public static Date StringToDateTime2(String str) {
        DateFormat format1 = new SimpleDateFormat(
                DATETIME_PATTERN_WECHAT_NOTIFY_TIME);
        Date date = null;
        try {
            date = format1.parse(str);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }
        return date;
    }


    /**
     * 时间的月
     */
    public static final Date month(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate();
    }

    /**
     * 时间的月操作
     */
    public static final Date month(Date date, int month) {
        DateTime dateTime = new DateTime(date);
        return dateTime.withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).plusMonths(month)
                .toDate();
    }

    /**
     * 时间月数差
     */
    public static final int monthsBetween(Date date0, Date date1) {
        DateTime dateTime0 = new DateTime(date0);
        DateTime dateTime1 = new DateTime(date1);
        return Months.monthsBetween(dateTime0, dateTime1).getMonths();
    }

    /**
     * 时间的天
     */
    public static final Date day(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate();
    }

    /**
     * 时间的天操作
     */
    public static final Date day(Date date, int day) {
        DateTime dateTime = new DateTime(date);
        return dateTime.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).plusDays(day).toDate();
    }

    /**
     * 时间的秒
     */
    public static final Date second(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.toDate();
    }

    /**
     * 时间的秒操作
     */
    public static final Date second(Date date, int second) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusSeconds(second).toDate();
    }


}
