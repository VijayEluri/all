package com.lanxum.dstor;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.eclipse.jetty.util.log.Log;

import com.lanxum.dstor.util.Util;

@SuppressWarnings({"rawtypes", "unchecked"})
public class JacksonTest extends TestCase {

	private void processDateFields(Map<String, Object> map) {
		for (String key : map.keySet()) {
			if (!key.startsWith("__date")) continue;
			
			Object val = map.get(key);
			
			if (!(val instanceof java.lang.String)) continue;

			String str = (String) val;
			try {
				long ms = Util.dateStringToTimestamp(str);
				map.put(key, new Long(ms));
			} catch (Exception e) {
				Log.info("cannot parse " + str, e);
			}
		}
	}

	public void testMarshal() throws JsonParseException, JsonMappingException, IOException {
		Map map = new HashMap();
		map.put("int", 1);
		map.put("double", 2.2);
		map.put("__date", new Date());
		map.put("string", "hello");

		ObjectMapper m = new ObjectMapper();
		m.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		String s = m.writeValueAsString(map);
		System.out.println(s);

		Map<String, Object> map2 = m.readValue(s, Map.class);

		processDateFields(map2);
		
		for (String key : map2.keySet()) {
			Object o = map2.get(key);
			System.out.println(key + ", " + o + ", " + o.getClass());
		}
	}

	public void testUnmarshal() throws JsonParseException, JsonMappingException, IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"int\":1");
		sb.append(",");
		sb.append("\"double\":2.2");
		sb.append(",");
		sb.append("\"date\":\"2010-7-28T0:08:00.000+0000\"");
		sb.append(",");
		sb.append("\"string\":\"hello\"");
		sb.append("}");

		System.out.println(sb.toString());

		ObjectMapper m = new ObjectMapper();
		Map<String, Object> map = m.readValue(sb.toString(), Map.class);

		for (String key : map.keySet()) {
			Object o = map.get(key);
			System.out.println(key + ", " + o + ", " + o.getClass());
		}
	}

}
