package com.linktag.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class ClsDateTime {
	/**
	 * ISO 8691 포맷
	 */
	public static String FORMAT_ISO8691 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	/**
	 * 날짜 기본 포맷
	 */
	public static String FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	/**
	 * Default Simple Date Format 날짜 기본 포맷을 파싱하기 위해 사용한다.
	 */
	private static SimpleDateFormat sdfParseDefault = new SimpleDateFormat(FORMAT_DEFAULT);

	// ===========================================
	// 현재 시간에 관한 함수
	// ===========================================
	/**
	 * 현재 시간을 기본 포맷에 맞춰서 가져온다.
	 *
	 * @return yyyy-MM-dd HH:mm:ss 포맷의 시간
	 */
	public static String getNow() {
		Calendar cal = Calendar.getInstance();
		return sdfParseDefault.format(cal.getTime());
	}

	public static String getNow(String format) {
		Calendar cal = Calendar.getInstance();
		return new SimpleDateFormat(format).format(cal.getTime());
	}

	public static String ConvertDateFormat(String targetFormat, String requireFormat, String sourceDatetime){
		String strDatetime = sourceDatetime;

		SimpleDateFormat sdfFormat = new SimpleDateFormat(requireFormat);
		SimpleDateFormat sdfParse = new SimpleDateFormat(targetFormat);

		try{
			strDatetime = sdfFormat.format(sdfParse.parse(strDatetime));
		} catch (Exception e){
			e.printStackTrace();
		}

		return strDatetime;
	}

	/**
	 * 현재 날짜에 시분초를 설정한다.
	 *
	 * @return
	 */
	public static Calendar getNowCalendar(Calendar cal, int hour, int min, int sec) {
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, sec);
		return cal;
	}

	public static String ConvertStringBuffer(String source, String inputString, int index){
		String str = "";

		try {
			if(source != null && !source.equals("")) {
				StringBuffer sb = new StringBuffer(source);
				str = sb.insert(index, inputString).toString();
			} else {
				str = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}

	/**
	 * 현재 시간을 포맷에 맞춰서 가져온다.
	 * 
	 * @param format
	 * @return
	 */
	public static String getNow(String format, Locale locale) {
		Calendar cal = Calendar.getInstance();
		return new SimpleDateFormat(format, locale).format(cal.getTime());
	}

	// ===========================================
	// 일시를 String로 변환하여 전달한다.
	// ===========================================

	/**
	 * 기본 datetime 문자열을 포맷에 맞춰서 내보낸다.
	 * 
	 * @param targetFormat
	 * @param sourceDatetime
	 *            yyyy-MM-dd HH:mm:ss 기본형
	 * @param locale
	 * @return
	 */
	public static String ConvertDate(String targetFormat, String sourceDatetime, Locale locale) {
		String strDatetime = sourceDatetime;

		SimpleDateFormat sdfFormat = new SimpleDateFormat(targetFormat, locale);

		try {
			strDatetime = sdfFormat.format(ConvertDateToDate(strDatetime));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strDatetime;
	}

	/**
	 * 해당 포맷으로 String datetime을 전달한다.
	 * 
	 * @param source
	 * @param targetFormat
	 * @return
	 */
	public static String ConvertDateToFormat(String source, String targetFormat, Locale locale) {
		return ConvertDateToFormat(source, FORMAT_DEFAULT, targetFormat, locale);
	}

	/**
	 * 해당 포맷으로 String datetime을 전달한다.
	 * 
	 * @param sourceDatetime
	 * @param sourceFormat
	 * @param targetFormat
	 * @return
	 */
	public static String ConvertDateToFormat(String sourceDatetime, String sourceFormat, String targetFormat,
                                             Locale locale) {
		SimpleDateFormat sdfParse = new SimpleDateFormat(sourceFormat);
		SimpleDateFormat sdfFormat = new SimpleDateFormat(targetFormat, locale);

		String strTarget = sourceDatetime;

		try {
			strTarget = sdfFormat.format(sdfParse.parse(sourceDatetime));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strTarget;
	}

	/**
	 * ISO 8691날짜형태를 일반 날짜형태로 변경하여 반환한다.
	 * 
	 * @param targetFormat
	 * @param source
	 * @return
	 */
	public static String ConvertDateFromISO8691(String targetFormat, String source, Locale locale) {
		String strDatetime = source;

		SimpleDateFormat sdfFormat = new SimpleDateFormat(targetFormat, locale);

		try {
			strDatetime = sdfFormat.format(ConvertDateFromISO8691ToDate(strDatetime));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strDatetime;
	}

	// ===========================================
	// String Datetime을 Date형으로 변형한다.
	// ===========================================
	/**
	 * 기본 datetime 문자열을 date에 맞춰서 내보낸다.
	 * 
	 * @param source
	 * @return
	 */
	public static Date ConvertDateToDate(String source) {
		SimpleDateFormat sdfParse = new SimpleDateFormat(FORMAT_DEFAULT);

		Date date = null;
		try {
			date = sdfParse.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	/**
	 *
	 * @param source
	 * @return
	 */
	public static Date ConvertDateFromISO8691ToDate(String source) {
		SimpleDateFormat sdfParseISO = new SimpleDateFormat(FORMAT_ISO8691);
		sdfParseISO.setTimeZone(TimeZone.getTimeZone("GMT"));

		Date date = null;
		try {
			date = sdfParseISO.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	/**
	 * 폰에 설정된 날짜포맷을 가져온다.
	 *
	 * @return
	 */
	public static String getSytemDateFormat(Locale locale) {
		return DateFormat.getBestDateTimePattern(locale, "yyyy-MM-dd");
	}

	/**
	 * 시스템 시간으로 보내준다. 언어 설정 안됨
	 * 
	 * @param context
	 * @param date
	 * @return
	 */
	public static String setSystemDateFormat(Context context, Date date) {
		return DateFormat.getLongDateFormat(context).format(date);
	}
}
