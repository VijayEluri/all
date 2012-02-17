package com.oldhu.cm.common.services;

public interface NavigationService
{
	void getDescendants();

	void getChildren();

	void getFolderParent();

	void getObjectParents();

	void getCheckedoutDocuments();
}
