package com.oldhu.cm.common.services;

public interface VersioningService
{
	void checkOut();

	void cancelCheckOut();

	void checkIn();

	void getPropertiesOfLatestVersion();

	void getAllVersions();

	void deleteAllVersions();
}
