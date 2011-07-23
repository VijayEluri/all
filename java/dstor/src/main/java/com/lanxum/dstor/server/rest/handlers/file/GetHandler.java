package com.lanxum.dstor.server.rest.handlers.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

import com.lanxum.dstor.server.db.FileObject;
import com.lanxum.dstor.server.rest.JSONHelper;
import com.lanxum.dstor.util.C;
import com.lanxum.dstor.util.Util;

public class GetHandler extends AbstractHttpHandler {
	private static final Logger logger = Logger.getLogger(GetHandler.class.getName());

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
		long id = getId(target, response);
		if (id < 0) return;

		try {
			FileObject fobj = dbService.find(id);
			if (fobj == null) {
				logger.warn("file not found for " + id);
				JSONHelper.respondFileNotFound(response);
				return;
			}
			
			//TODO: make digest calculation configable
			MessageDigest digest = Util.getDigest();

			outputFileAndUpdateDigest(response, fobj, digest);

			byte[] md5 = digest.digest();
			if (fobj.getMd5Sum().equals(Util.getHexString(md5))) {
				logger.debug("object " + id + " md5 check ok.");
			} else {
				logger.error("object " + id + " md5 check error.");
                // TODO: tell the user md5 error after downloading
			}

			logger.debug("object " + id + " sent to client.");
		} catch (Exception e) {
			logger.error("error while sending file to client", e);
			JSONHelper.respondError(response, "error while sending file to client");
		}

	}

	private void outputFileAndUpdateDigest(HttpServletResponse response, FileObject fobj, MessageDigest digest)
		throws Exception {
		InputStream is = fileStore.getInputStreamByURI(fobj.getURI());
		setHeader(response, fobj);

		OutputStream os = response.getOutputStream();
		int len = 0;
		byte[] buf = new byte[1024 * 16];
		while ((len = is.read(buf)) > 0) {
			os.write(buf, 0, len);
			digest.update(buf, 0, len);
		}
		is.close();
	}

	private void setHeader(HttpServletResponse response, FileObject fobj) throws UnsupportedEncodingException {
		response.setContentType(C.CONTENT_TYPE_DOWNLOAD);
		response.setCharacterEncoding(C.ENCODING_UTF8);
		response.setHeader("Content-Disposition",
				"attachment; filename=" + URLEncoder.encode(fobj.getFileName(), C.ENCODING_UTF8));
	}

}
