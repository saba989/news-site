package com.ixm.core.models;

import com.ixm.core.constants.Constants;
import com.ixm.core.utils.IxmUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;


import javax.inject.Inject;

/**
 * Represents the title
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class Title {
	 @SlingObject
	    private ResourceResolver resolver;


	    @Inject
	    private String title;

	    @Inject
	    private String type;

	    @Inject
	    private String linkUrl;

	    @Inject
	    private String linkTarget;

	    /**
	     * The method returns the title
	     * @return title
	     */
	    public String getTitle() {
	        return title;
	    }

	    /**
	     * The method returns the type of title
	     * @return type
	     */
	    public String getType() {
	        return type;
	    }
	    /**
	     * The method returns the link for the href tag
	     * @return the linkUrl
	     */
	    public String getLinkUrl() {
	        if (IxmUtils.isExternalURL(linkUrl)) {
	            return linkUrl;
	        } else {
	            return IxmUtils.getExternalizedLink(linkUrl, resolver) + Constants.HTML_EXTENSION;
	        }
	    }

	    /**
	     * The method returns the link target
	     * @return  linkTarget
	     */
	    public String getLinkTarget() {
	        return linkTarget;
	    }

	
}
