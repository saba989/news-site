package com.ixm.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StyleNameModel {
	@Inject
	private String styleName;

	public String getStyleNameSite() {
		return styleName != null ? styleName.concat(".site") : "ixm-aem.site";
	}

	public String getStyleNameDependency() {
		return styleName != null ? styleName.concat(".dependencies") : "ixm-aem.dependencies";
	}
}