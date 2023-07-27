package com.ixm.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Defines the cardBean for the card .
 */
public class CardBean {

	private String imagePath;
	private String pageUrl;
	private String pageTitle;
	private String imageAltText;
	private List<String> tags;

	public List<String> getTags() {
		return Collections.unmodifiableList(tags);
	}

	public void setTags(List<String> tags) {
		this.tags = new ArrayList<>(tags);
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getImageAltText() {
		return imageAltText;
	}

	public void setImageAltText(String imageAltText) {
		this.imageAltText = imageAltText;
	}

}
