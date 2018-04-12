package com.baidu.ueditor.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 系统名称：济南服务外包公共服务平台
 * 概要:日期處理共通
 * 类名称:DateUtil
 * @author 860115025
 */
public class DateUtil {
	// 年
	private final static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
	// 年月日
	private final static SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
	// 年月日
	private final static SimpleDateFormat sdfDays = new SimpleDateFormat("yyyyMMdd");
	// 年月日时分秒
	private final static SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final static SimpleDateFormat sdfTimeNoSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * 获取YYYY格式
	 * @return
	 */
	public static String getYear() {
		return sdfYear.format(new Date());
	}

	/**
	 * 获取YYYY-MM-DD格式
	 * @return
	 */
	public static String getDay() {
		return sdfDay.format(new Date());
	}

	/**
	 * 获取YYYY-MM-DD格式
	 * @param time
	 * @return
	 */
	public static String getDay(Timestamp time) {
		return sdfDay.format(time);
	}

	/**
	 * 获取YYYYMMDD格式
	 * @return
	 */
	public static String getDays() {
		return sdfDays.format(new Date());
	}

	/**
	 * 获取YYYY-MM-DD HH:mm:ss格式
	 * @return
	 */
	public static String getTime() {
		return sdfTime.format(new Date());
	}
	
	/**
	 * 获取YYYY-MM-DD HH:mm格式
	 * @return
	 */
	public static String getTimeNoSecond() {
		return sdfTimeNoSecond.format(new Date());
	}

	/**
	 * @Title: compareDate
	 * @Description: TODO(日期比较，如果s>=e 返回true 否则返回false)
	 * @param s
	 * @param e
	 * @return boolean
	 * @throws @author
	 */
	public static boolean compareDate(String s, String e) {
		if (fomatDate(s) == null || fomatDate(e) == null) {
			return false;
		}
		return fomatDate(s).getTime() >= fomatDate(e).getTime();
	}

	/**
	 * 格式化日期
	 * @return
	 */
	public static Date fomatDate(String date) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return fmt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 校验日期是否合法
	 * @return
	 */
	public static boolean isValidDate(String s) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			fmt.parse(s);
			return true;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return false;
		}
	}

	/**
	 * 获取两个日期之间的差
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int getDiffYear(String startTime, String endTime) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			int years = (int) (((fmt.parse(endTime).getTime() - fmt.parse(startTime).getTime()) / (1000 * 60 * 60 * 24))
					/ 365);
			return years;
		} catch (Exception e) {
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return 0;
		}
	}

	/**
	 * <li>功能描述：时间相减得到天数
	 * @param beginDateStr
	 * @param endDateStr
	 * @return long
	 * @author Administrator
	 */
	public static long getDaySub(String beginDateStr, String endDateStr) {
		long day = 0;
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
		java.util.Date beginDate = null;
		java.util.Date endDate = null;

		try {
			beginDate = format.parse(beginDateStr);
			endDate = format.parse(endDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);

		return day;
	}

	/**
	 * 得到n天之后的日期
	 * @param days
	 * @return
	 */
	public static String getAfterDayDate(String days) {
		int daysInt = Integer.parseInt(days);
		// java.util包
		Calendar canlendar = Calendar.getInstance(); 
		// 日期减 如果不够减会将月变动
		canlendar.add(Calendar.DATE, daysInt); 
		Date date = canlendar.getTime();
		SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdfd.format(date);

		return dateStr;
	}

	/**
	 * 得到n天之后是周几
	 * @param days
	 * @return
	 */
	public static String getAfterDayWeek(String days) {
		int daysInt = Integer.parseInt(days);
		// java.util包
		Calendar canlendar = Calendar.getInstance(); 
		// 日期减 如果不够减会将月变动
		canlendar.add(Calendar.DATE, daysInt); 
		Date date = canlendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("E");
		String dateStr = sdf.format(date);
		return dateStr;
	}
	
    /***
     * 比较相差多少时间
     * @param startTime
     * @param endTime
     * @param format
     * @param str
     * @return
     * @throws Exception
     */
    public static Long dateDiff(String startTime, String endTime,     
            String format, String str) throws Exception {     
        // 按照传入的格式生成一个simpledateformate对象     
        SimpleDateFormat sd = new SimpleDateFormat(format);     
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数     
        long nh = 1000 * 60 * 60;// 一小时的毫秒数     
        long nm = 1000 * 60;// 一分钟的毫秒数     
        long ns = 1000;// 一秒钟的毫秒数     
        long diff;     
        long day = 0;     
        long hour = 0;     
        long min = 0;     
        long sec = 0;     
        // 获得两个时间的毫秒时间差异     
        diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime(); 
        if(str.equalsIgnoreCase("d")){
            day = diff / nd;// 计算差多少天     
            return day;
        }
        if(str.equalsIgnoreCase("h")){
            hour = diff % nd / nh + day * 24;// 计算差多少小时     
            return hour;
        }
        if(str.equalsIgnoreCase("m")){
            min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟     
            return min;
        }
        if(str.equalsIgnoreCase("s")){
            sec = diff % nd % nh % nm / ns;// 计算差多少秒    
            return sec;
        }
        return 0l;
    }
    
    public static Long dateDiffer(String startTime, String endTime,     
            String format, String str){     
        // 按照传入的格式生成一个simpledateformate对象     
        SimpleDateFormat sd = new SimpleDateFormat(format);     
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数     
        long nh = 1000 * 60 * 60;// 一小时的毫秒数     
        long nm = 1000 * 60;// 一分钟的毫秒数     
        long ns = 1000;// 一秒钟的毫秒数     
        long diff = 0;     
        long day = 0;     
        long hour = 0;     
        long min = 0;     
        long sec = 0;     
        // 获得两个时间的毫秒时间差异     
        try {
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		} 
        if(str.equalsIgnoreCase("d")){
            day = diff / nd;// 计算差多少天     
            return day;
        }
        if(str.equalsIgnoreCase("h")){
            hour = diff % nd / nh + day * 24;// 计算差多少小时     
            return hour;
        }
        if(str.equalsIgnoreCase("m")){
            min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟     
            return min;
        }
        if(str.equalsIgnoreCase("s")){
            sec = diff /ns;// 计算差多少秒    
            return sec;
        }
        return 0l;
    }
    
    public static long getCurrentTime(){
    	return System.currentTimeMillis();
    }
    
    
    public static void main(String[] ags){
    	try {
			System.out.println(dateDiffer("2017-11-2 10:42:30",getTime(),"yyyy-MM-dd HH:mm:ss","s"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
