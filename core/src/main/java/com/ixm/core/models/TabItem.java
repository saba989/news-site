package com.ixm.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TabItem {

	@Inject
	private String title;
	
	/**
	 * list of linksitem
	 */
	
	
	@Inject
	private List<LinkItem> links; 
	
	
	/**
	 * 
	 * @return title of the tab
	 */
	
	
	public String getTitle() {
		return title;
	}
	
	/**
	 * 
	 * @return list of links from linkitem
	 */
	
	public List<LinkItem> getLinks() {
		return Collections.unmodifiableList(links);
	}
}
