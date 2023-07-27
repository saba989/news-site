package com.ixm.core.migration.utils;

import java.util.Map;
/**
 * A class which contains {@code properties} for a row in excel.
 */
public class MigrationBean {

	private String pageName;

	private String templateName;

	private Map<String, String> propertyList;

	private String errorMessage;

	private String completePath;

	private String parentPath;

	private String originalCompletePath;

	public String getOriginalCompletePath() {
		return originalCompletePath;
	}

	public void setOriginalCompletePath(String originalCompletePath) {
		this.originalCompletePath = originalCompletePath;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getCompletePath() {
		return completePath;
	}

	public void setCompletePath(String completePath) {
		this.completePath = completePath;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getPageName() {
		return pageName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public Map<String, String> getPropertyList() {
		return propertyList;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public void setPropertyList(Map<String, String> propertyList) {
		this.propertyList = propertyList;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
