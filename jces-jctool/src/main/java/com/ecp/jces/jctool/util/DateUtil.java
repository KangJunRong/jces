package com.ecp.jces.jctool.util;

 import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * User: MiniJava
 * Date: 2009-06-19
 * Time: 16:53:06
 */
public class DateUtil {
	
	/**
	 * 如果晚的时间大，返回�?; 否则返回�?
	 * @param late 晚的时间
	 * @param early 早的时间
	 * @return
	 */
	public static boolean compareDate(String late, String early) {
		Date lateDate = stringToDate(late);
		Date earlyDate = stringToDate(early);
		
		if (lateDate.compareTo(earlyDate) > 0) {
			return true;
		}
 
		return false;
	}

   /**
	 * 将字符串转化为日期类�?
	 *
	 * @param date
	 *
	 */
	public static Date stringToDate(String date) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date time = formater.parse(date);
			return time;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return Calendar.getInstance().getTime();
	}

	public static Date stringToDate_1(String date) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
		try {
			Date time = formater.parse(date);
			return time;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return Calendar.getInstance().getTime();
	}
    public static Date getNow(){
        return  Calendar.getInstance().getTime();
    }

    /**
	 * 格式化时�?
	 *
	 * @param date
	 * @return String
	 */
	public static String dateToString_1(long date) {
		try {
			String dateString = String.valueOf(date);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
			Date formateDate = formatter.parse(dateString);
			formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			return formatter.format(formateDate);
		} catch (Exception e) {
			//e.printStackTrace();
			return String.valueOf(date);
		}
	}

	public static String dateToString_1(Date date) {
		if (date == null) return "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
			return formatter.format(date);
		} catch (Exception e) {
			//e.printStackTrace();
			return String.valueOf(date);
		}
	}
	
	/**
	 * 格式化日�?
	 *
	 * @param date
	 *
	 */
	public static String dateToString_2(Date date) {
		if (date == null) return "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd hh:mm:ss");
			return formatter.format(date);
		} catch (Exception e) {
			//e.printStackTrace();
			return String.valueOf(date);
		}
	}

	/**
	 * 格式化日�?
	 *
	 * @param date
	 * 
	 */
	public static String dateToString_3(Date date) {
		if (date == null) return "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.format(date);
		} catch (Exception e) {
			//e.printStackTrace();
			return String.valueOf(date);
		}
	}
	
	public static String dateToString_7(Date date) {
		if (date == null) return "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("(dd.MM.yyyy)");
			return formatter.format(date);
		} catch (Exception e) {
			//e.printStackTrace();
			return String.valueOf(date);
		}
	}
	
	public static String timeToString(Date date) {
		if (date == null) return "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			return formatter.format(date);
		} catch (Exception e) {
			//e.printStackTrace();
			return String.valueOf(date);
		}
	}
	
	public static String timeToString_1(Date date) {
		if (date == null) return "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
			return formatter.format(date);
		} catch (Exception e) {
			//e.printStackTrace();
			return String.valueOf(date);
		}
	}

	/**
	 * 格式化日�?
	 *
	 * @param date
	 *
	 */
	public static String dateToString_4(Date date) {
		if (date == null) return "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.format(date);
		} catch (Exception e) {
			//e.printStackTrace();
			return String.valueOf(date);
		}
	}

    	/**
	 * 格式化日�?
	 *
	 * @param date
	 *
	 */
	public static String dateToString_5(Date date) {
		if (date == null) return "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			return formatter.format(date);
		} catch (Exception e) {
			//e.printStackTrace();
			return String.valueOf(date);
		}
	}
	
	/**
	 * �?查字符串是否为日�?
	 * @param strDate
	 * @return
	 */
	public static boolean checkDate(String strDate){
		boolean flag = false;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.parse(strDate);
			flag = true;
		} catch (Exception e) {
			System.out.println("字符�?:"+strDate+" 不是有效日期");
		}
		
		return flag;
	}
	
	public static boolean checkDate1(String strDate){
		boolean flag = false;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			formatter.parse(strDate);
			flag = true;
		} catch (Exception e) {
			System.out.println("字符�?:"+strDate+" 不是有效日期");
		}
		
		return flag;
	}
	
	/**
	 * 格式化日�?
	 *
	 * @param date
	 * 
	 */
	public static String dateToString_6(Date date) {
		if (date == null) return "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			return formatter.format(date);
		} catch (Exception e) {
			//e.printStackTrace();
			return String.valueOf(date);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(dateToString_1(DateUtil.getNow()));
	}
}
