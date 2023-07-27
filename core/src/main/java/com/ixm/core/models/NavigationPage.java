package com.ixm.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementing getters and setters
 */
public class NavigationPage {

	private List<NavigationPage> childPages;
	private String path;
	private String title;
	
	public NavigationPage(final List<NavigationPage> childPages, final String path, final String title) {
		this.childPages = new ArrayList<>(childPages);
		this.path = path;
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<NavigationPage> getChildPages() {
		return Collections.unmodifiableList(childPages);
	}
	public void setChildPages(List<NavigationPage> childPages) {
		this.childPages = new ArrayList<>(childPages);
	}
	

}
