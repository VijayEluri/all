package com.lanxum.dstor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GsonTest extends TestCase
{
	public void testMarshal()
	{
		Map map = new HashMap();
		map.put("int", 1);
		map.put("double", 2.2);
		map.put("date", new Date());
		map.put("string", "hello");
		
		Gson gson = new Gson();
		System.out.println(gson.toJson(map));
	}
	
	public void testUnmarshal()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"int\":1");
		sb.append(",");
		sb.append("\"double\":2.2");
		sb.append(",");
		sb.append("\"date\":\"2010-7-28 0:08:00\"");
		sb.append(",");
		sb.append("\"string\":\"hello\"");
		sb.append("}");
		Gson gson = new Gson();
		
		System.out.println(sb.toString());
		
		Map<String, String> map = gson.fromJson(sb.toString(), new TypeToken<Map<String, String>>() {}.getType());
		for (String key : map.keySet()) {
			System.out.println(key + ", " + map.get(key));
		}
	}
}
