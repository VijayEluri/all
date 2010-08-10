package com.lets.filepro;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class test {

	private static String getDateTime(long t)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setCalendar(c);
		return sdf.format(new Date(t));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long st = System.currentTimeMillis();
		long et = System.currentTimeMillis();
		long sumLen = 10002302;
		long fcount = 12312;
		
		System.out.println(getDateTime(st));
		System.out.println(getDateTime(et));
		System.out.println("uploaded totally " + sumLen + " bytes");
		System.out.println("uploaded totally " + fcount + " files");
		
		DecimalFormat df = new DecimalFormat("000");
		System.out.println(df.format(23));
	}

}
