package com.lanxum.dstor.server.rest.handlers.files;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.log.Log;

import com.lanxum.dstor.server.db.DbService;
import com.lanxum.dstor.server.db.FileObject;
import com.lanxum.dstor.server.db.IdGenerator;
import com.lanxum.dstor.server.rest.JSONHelper;
import com.lanxum.dstor.server.rest.handlers.HttpHandler;
import com.lanxum.dstor.server.store.FileStore;
import com.lanxum.dstor.server.store.StoreOutputStream;
import com.lanxum.dstor.util.C;
import com.lanxum.dstor.util.Util;

/**
 * handle /files/ POST
 * 
 * saving content
 */
public class PostHandler implements HttpHandler {
	private static final Logger logger = Logger.getLogger(PostHandler.class.getName());

	private IdGenerator idGenerator;
	private FileStore fileStore;
	private DbService dbService;

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	public void setFileStore(FileStore fileStore) {
		this.fileStore = fileStore;
	}

	public void setDbService(DbService dbService) {
		this.dbService = dbService;
	}

	@SuppressWarnings("unchecked")
	// List to List<FileItem> conversion
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			logger.warn("not multipart request");
			JSONHelper.respondError(response, "not multipart request");
			return;
		}

		Map<String, Object> metaData = parseMetadata(request);

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> items;

		try {
			items = (List<FileItem>) upload.parseRequest(request);
		} catch (FileUploadException ex) {
			logger.error("Error in parsing upload request", ex);
			JSONHelper.respondError(response, "Error in parsing upload request");
			return;
		}

		// won't handle more than 1 file
		if (items.size() > 1) {
			logger.warn("cannot handle more than 1 file");
			JSONHelper.respondError(response, "cannot handle more than 1 file");
			return;
		}

		Iterator<FileItem> iter = items.iterator();
		if (iter.hasNext()) {
			FileItem item = iter.next();
			if (item.isFormField()) {
				// ignore all post fields
			} else {
				processFile(response, item, metaData);
			}
		}
	}

	private Map<String, Object> parseMetadata(HttpServletRequest request) {
		String strMetadata = request.getHeader(C.HTTP_HEADER_METADATA);
		if (strMetadata == null) return null;
		if (strMetadata.length() == 0) return null;

		try {
			ObjectMapper m = new ObjectMapper();

			@SuppressWarnings("unchecked")
			Map<String, Object> map = m.readValue(strMetadata, Map.class);

			processDateFields(map);

			return map;
		} catch (Exception e) {
			logger.error("Error while parsing meta data json", e);
			return null;
		}
	}

	/*
	 * special process of fields start with __date
	 * convert the value into milliseconds
	 */
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

	private void processFile(HttpServletResponse response, FileItem item, Map<String, Object> metaData) {
		long id = -1;
		StoreOutputStream os = null;
		try {
			MessageDigest digest = Util.getDigest();
			id = idGenerator.getNextFileId();
			os = fileStore.getOutputStream(id, item.getSize());
			InputStream is = item.getInputStream();
            
			byte[] buf = new byte[1024 * 16];
			long fileSize = 0;
			int len;
			while ((len = is.read(buf)) > 0) {
				fileSize += len;
				os.write(buf, 0, len);
				digest.update(buf, 0, len);
			}
			os.close();
            
			byte[] md5 = digest.digest();
			String fileName = item.getName();
			if (fileName != null) {
				fileName = FilenameUtils.getName(fileName);
			}

			FileObject fobj = new FileObject();
			fobj.setFileName(fileName);
			fobj.setFileSize(fileSize);
			fobj.setURI(os.getURI());
			fobj.setMd5Sum(Util.getHexString(md5));
			fobj.setMetaData(metaData);

			dbService.save(id, fobj);

			JSONHelper.respondCreatedId(response, id);
		} catch (Exception ex) {
			logger.fatal("error in saving file", ex);
			JSONHelper.respondError(response, "error in saving file");

			/*
			 *  roll back all changes on errors
			 */
			cleanup(id, os);
		}
	}

	private void cleanup(long id, StoreOutputStream os) {
		// delete file
		if (os != null) {
			try {
				os.close();
			} catch (Exception e) {
			}

			try {
				fileStore.removeByURI(os.getURI());
			} catch (Exception e) {
			}
		}

		// delete DB record
		if (id >= 0) {
			try {
				dbService.remove(id);
			} catch (Exception e) {
			}
		}
	}
}
