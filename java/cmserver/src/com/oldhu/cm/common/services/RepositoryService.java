package com.oldhu.cm.common.services;

import java.util.Collection;

import com.oldhu.cm.common.domain.RepositoryInfo;
import com.oldhu.cm.common.domain.Type;

public interface RepositoryService
{
	public Collection<String> getRepositories();

	public RepositoryInfo getRepositoryInfo(String repositoryId);

	public Collection<Type> getTypes(String repositoryId, boolean returnPropertyDefinitions, int maxItems, int skipCount);

	public Type getTypeDefinition(String repositoryId, String typeId);
}
