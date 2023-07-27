package com.ixm.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Model(
        adaptables = Resource.class,
        resourceType = TabLinksModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TabLinksModel {

	public static final String RESOURCE_TYPE = "ixm-aem/components/tabLinks";
	
	
	@Inject private List<TabItem> tabs;
	
/**
 * 
 * @return list of tabItems
 */

	public List<TabItem> getTabs() {
		return Collections.unmodifiableList(tabs);
	}
	  
}
