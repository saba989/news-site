package com.ixm.core.models;

import com.ixm.core.constants.Constants;
import com.ixm.core.utils.IxmUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.inject.Inject;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LinkItem {

	@SlingObject private ResourceResolver resolver;

	@Inject
	private String linkText;
	
	@Inject
	private String linkUrl;
	
	@Inject
	private String linkTarget;
	
/**
 * 
 * @return link text from LinkItem
 */
	
	public String getLinkText() {
		return linkText;
	}
	
/**
 * 
 * @return link url from LinkItem
 */
	
	public String getLinkUrl() {
		if (IxmUtils.isExternalURL(linkUrl))
			return linkUrl;
		return IxmUtils.getExternalizedLink(linkUrl, resolver).concat(Constants.HTML_EXTENSION);
	}
	
/**
 * 
 * @return link target from LinkItem
 */
	
	public String getLinkTarget() {
		return linkTarget;
	}
}
