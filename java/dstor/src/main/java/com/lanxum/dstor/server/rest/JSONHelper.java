package com.lanxum.dstor.server.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.lanxum.dstor.util.Consts;

public class JSONHelper
{
	private static final Logger logger = Logger.getLogger(JSONHelper.class.getName());

	private static void setJSONHeader(HttpServletResponse response)
	{
		response.setContentType(Consts.CONTENT_JSON);
		response.setCharacterEncoding(Consts.ENCODING_UTF8);
	}
	
	public static void respondOk(HttpServletResponse response)
	{
		setJSONHeader(response);
		Gson gson = new Gson();
		try {
			ErrorMsg err = new ErrorMsg();
			err.err = Consts.ERR_OK;
			err.msg = "successful";
			response.getWriter().write(gson.toJson(err));
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (IOException ex) {
			logger.fatal("error while writing response", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public static void respondNoContent(HttpServletResponse response)
	{
		setJSONHeader(response);
		Gson gson = new Gson();
		try {
			ErrorMsg err = new ErrorMsg();
			err.err = Consts.ERR_OK;
			err.msg = "No Content";
			response.getWriter().write(gson.toJson(err));
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		} catch (IOException ex) {
			logger.fatal("error while writing response", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public static void respondFileNotFound(HttpServletResponse response)
	{
		setJSONHeader(response);
		Gson gson = new Gson();
		try {
			ErrorMsg err = new ErrorMsg();
			err.err = Consts.ERR_ERROR;
			err.msg = "File Not Found";
			response.getWriter().write(gson.toJson(err));
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (IOException ex) {
			logger.fatal("error while writing response", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public static void respondError(HttpServletResponse response, String msg)
	{
		setJSONHeader(response);
		Gson gson = new Gson();
		try {
			ErrorMsg err = new ErrorMsg();
			err.err = Consts.ERR_ERROR;
			err.msg = msg;
			response.getWriter().write(gson.toJson(err));
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch (IOException ex) {
			logger.fatal("error while writing response", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public static void respondCreatedId(HttpServletResponse response, long id)
	{
		setJSONHeader(response);
		Gson gson = new Gson();
		try {
			Map<String, Long> map = new HashMap<String, Long>();
			map.put("err", (long) Consts.ERR_OK);
			map.put("id", id);
			response.getWriter().write(gson.toJson(map));
			response.setStatus(HttpServletResponse.SC_CREATED);
		} catch (IOException ex) {
			logger.fatal("error while writing response", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public static void respondNotAllowed(HttpServletResponse response, String method)
	{
		setJSONHeader(response);
		Gson gson = new Gson();
		try {
			ErrorMsg err = new ErrorMsg();
			err.err = Consts.ERR_ERROR;
			err.msg = method + " is not allowed";
			response.getWriter().write(gson.toJson(err));
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		} catch (IOException ex) {
			logger.fatal("error while writing response", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private static class ErrorMsg
	{
		@SuppressWarnings("unused")
		int err;
		@SuppressWarnings("unused")
		String msg;
	}
}
