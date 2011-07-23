package com.lanxum.dstor.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class Util {
	private static final Logger logger = Logger.getLogger(Util.class.getName());

	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	public static String getHexString(byte[] buf) {
		char[] chars = new char[2 * buf.length];
		for (int i = 0; i < buf.length; ++i) {
			chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}
		return new String(chars);
	}

	private static ThreadLocal<MessageDigest> digestCache = new ThreadLocal<MessageDigest>()
	{
        @Override
		protected synchronized MessageDigest initialValue()
		{
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				logger.fatal("cannot init MD5 digest", e);
				return null;
			}
		}
	};

	public static MessageDigest getDigest() {
		MessageDigest digest = digestCache.get();
		if (digest != null)
			digest.reset();
		return digest;
	}

	private static ThreadLocal<DateFormat> dateFormatCache = new ThreadLocal<DateFormat>()
	{
        @Override
		protected synchronized DateFormat initialValue()
		{
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		}
	};

	public static long dateStringToTimestamp(String date) throws ParseException {
		DateFormat dateFormat = dateFormatCache.get();
		return dateFormat.parse(date.replace('T', ' ')).getTime();
	}
	
	private static ThreadLocal<ObjectMapper> objectMapperCache = new ThreadLocal<ObjectMapper>()
	{
        @Override
		protected synchronized ObjectMapper initialValue()
		{
			return new ObjectMapper();
		}
	};
	
	public static ObjectMapper getObjectMapper() {
		return objectMapperCache.get();
	}

}
