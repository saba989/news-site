package com.ixm.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class UspModel {
	
	@Inject private String title;
	
	@Inject private String layout;


	@Inject private List<UspItem> usps;

	public String getTitle() {
		return title;
	}

	public String getLayout() {
		return layout;
	}
	
	public List<UspItem> getUsps() {
		if (null == usps)
			return null;
		return Collections.unmodifiableList(usps);
	}
}
