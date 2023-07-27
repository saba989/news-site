package com.ixm.core.migration.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.dam.api.Asset;
import com.ixm.core.migration.utils.MigrationBean;
/**
 * This is the interface for migration
 *
 */
public interface MigrationService {
	/**
	 * This method returns the list of pages, templates and properties of the page.
	 * @param inputStream
	 * @return a list of bean with pages, templates and properties
	 * @throws IOException
	 */
	public List<MigrationBean> getList (InputStream inputStream ) throws IOException;
	
	/**
	 * This method returns the excel file as Asset
	 * @param resourceResolver
	 * @return excel file
	 */
	public Asset getAsset(ResourceResolver resourceResolver);
	
	/**
	 * This method returns the path where pages need to be created.
	 * @return the folder path 
	 */
	public String getFolderPath();


	/**
	 * This method creates a csv file containing list of all successfully created pages.
	 * @param resourceResolver
	 * @param rows
	 */
	void writePageSuccess(ResourceResolver resourceResolver, List<Map<String, String>> rows);

	/**
	 * This method creates a csv file containing list of all pages which had error while creating.
	 * @param resourceResolver
	 * @param rows
	 */
	void writePageFailure(ResourceResolver resourceResolver, List<Map<String, String>> rows);

}
