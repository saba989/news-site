package com.ixm.core.models;

import com.ixm.core.constants.Constants;
import com.ixm.core.utils.IxmUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

/**
 * Represents blog items multifield
 */
public class BlogsItems {
	
	@SlingObject
	private ResourceResolver resolver;

	
	@Inject
	private String blogTitle;

	@Inject
	private String blogSubTitle;

	@Inject
	private String blogDescription;

	@Inject
	private String blogLinkUrl;

	@Inject
	private String blogImage;
	

	@Inject
	private String blogLinkTarget;

	/**
     * The method returns the title of the multifield 
     * @return the title 
     */
	public String getBlogTitle() {
		return blogTitle;
	}

	/**
     * The method returns the subTitle of the multifield 
     * @return the subTitle 
     */
	public String getBlogSubTitle() {
		return blogSubTitle;
	}

	/**
     * The method returns the description of the multifield 
     * @return the description 
     */
	public String getBlogDescription() {
		return blogDescription;
	}

	/**
     * The method returns the linkUrl of the multifield 
     * @return the linkUrl 
     */
	public String getBlogLinkUrl() {
		if (IxmUtils.isExternalURL(blogLinkUrl)) {
			return blogLinkUrl;
		} else {
			return IxmUtils.getExternalizedLink(blogLinkUrl, resolver) + Constants.HTML_EXTENSION;
		}
	}

	/**
	 * The method returns the image of the multifield
	 * @return the image
	 */
	public String getBlogImage() {
		return blogImage;
	}

	/**
     * The method returns the linkTarget of the multifield 
     * @return the linkTarget 
     */
	public boolean isBlogLinkTarget() {
		return Boolean.valueOf(blogLinkTarget);
	}
}
