package com.lanxum.dstor.server.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.lanxum.dstor.util.C;
import com.lanxum.dstor.util.Util;

public class JSONHelper {
	private static final Logger logger = Logger.getLogger(JSONHelper.class.getName());

	private static void setJSONHeader(HttpServletResponse response) {
		response.setContentType(C.CONTENT_JSON);
		response.setCharacterEncoding(C.ENCODING_UTF8);
	}

	private static void writeErrorMsg(HttpServletResponse response, Object err, int responseCode) {
		ObjectMapper m = Util.getObjectMapper();

		try {
			response.setHeader("Err", m.writeValueAsString(err));
			response.setStatus(responseCode);
		} catch (Exception ex) {
			logger.fatal("error while writing response", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public static void respondOk(HttpServletResponse response) {
		setJSONHeader(response);
		ErrorMsg err = new ErrorMsg();
		err.err = C.ERR_OK;
		err.msg = "successful";
		writeErrorMsg(response, err, HttpServletResponse.SC_OK);
	}

	public static void respondNoContent(HttpServletResponse response) {
		setJSONHeader(response);
		ErrorMsg err = new ErrorMsg();
		err.err = C.ERR_OK;
		err.msg = "No Content";
		writeErrorMsg(response, err, HttpServletResponse.SC_NO_CONTENT);
	}

	public static void respondFileNotFound(HttpServletResponse response) {
		setJSONHeader(response);
		ErrorMsg err = new ErrorMsg();
		err.err = C.ERR_ERROR;
		err.msg = "File Not Found";
		writeErrorMsg(response, err, HttpServletResponse.SC_NOT_FOUND);
	}

	public static void respondError(HttpServletResponse response, String msg) {
		setJSONHeader(response);
		ErrorMsg err = new ErrorMsg();
		err.err = C.ERR_ERROR;
		err.msg = msg;
		writeErrorMsg(response, err, HttpServletResponse.SC_BAD_REQUEST);
	}

	public static void respondCreatedId(HttpServletResponse response, long id) {
		setJSONHeader(response);
		Map<String, Long> map = new HashMap<String, Long>();
		map.put("err", (long) C.ERR_OK);
		map.put("id", id);
		writeErrorMsg(response, map, HttpServletResponse.SC_CREATED);
	}

	public static void respondNotAllowed(HttpServletResponse response, String method) {
		setJSONHeader(response);
		ErrorMsg err = new ErrorMsg();
		err.err = C.ERR_ERROR;
		err.msg = method + " is not allowed";
		writeErrorMsg(response, err, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	private static class ErrorMsg {
		private int err;
		private String msg;

		public int getErr() {
			return err;
		}
		public void setErr(int err) {
			this.err = err;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
