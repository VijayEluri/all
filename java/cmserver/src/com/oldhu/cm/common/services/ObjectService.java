package com.oldhu.cm.common.services;

import com.oldhu.cm.common.domain.ContentStream;
import com.oldhu.cm.common.domain.Document;
import com.oldhu.cm.common.domain.Folder;

public interface ObjectService
{
	public String createDocument(String type, Document document, String folderId, ContentStream contentStream);

	public Folder createFolder(String folderName, String fullPath);

	public void createRelationship();

	public void createPolicy();

	public void getAllowableActions();

	public void getProperties();

	public void getPropertiesOfLatestVersion();

	public void getContentStream();

	public void updateProperties();

	public void moveObject();

	public void deleteObject();

	public void deleteTree();

	public void setContentStream();

	public void deleteContentStream();
}
