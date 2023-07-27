package com.ixm.core.solr.service;

import com.ixm.core.solr.utils.ResponseEntity;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface SolrService {

	 int createCore(final String collectionName) throws URISyntaxException, IOException, InterruptedException;

	 boolean hasCore(final String collectionName) throws URISyntaxException, IOException, InterruptedException;

	 int indexPages (final String collectionName, final List<String> pagePaths, final ResourceResolver resourceResolver)
			throws IOException, URISyntaxException, InterruptedException;

	Resource getOrCreateQueryBoostNode (final ResourceResolver resourceResolver) throws PersistenceException;

	Resource getOrCreateCollectionNode (final ResourceResolver resourceResolver, String collectionName) throws PersistenceException;

	int deleteIndexedData(final String collectionName) throws URISyntaxException, IOException;

	List<ResponseEntity> getData(final SlingHttpServletRequest request) throws IOException, URISyntaxException;

	int deleteCore(final String collectionName) throws URISyntaxException, IOException;

	String determineCollectionsPath(final ResourceResolver resourceResolver);
}

