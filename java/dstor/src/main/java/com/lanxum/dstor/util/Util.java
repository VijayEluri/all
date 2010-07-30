package com.lanxum.dstor.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class Util
{
	private static final Logger logger = Logger.getLogger(Util.class.getName());

	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	public static String getHexString(byte[] buf)
	{
		char[] chars = new char[2 * buf.length];
		for (int i = 0; i < buf.length; ++i) {
			chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}
		return new String(chars);
	}

	private static ThreadLocal<MessageDigest> digestCache = new ThreadLocal<MessageDigest>()
	{
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

	public static MessageDigest getDigest()
	{
		MessageDigest digest = digestCache.get();
		if (digest != null)
			digest.reset();
		return digest;
	}
}
