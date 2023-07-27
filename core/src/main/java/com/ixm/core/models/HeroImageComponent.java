package com.ixm.core.models;

import com.ixm.core.utils.IxmUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.inject.Inject;


@Model(adaptables = { Resource.class,SlingHttpServletRequest.class } , defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeroImageComponent {

	/**
	 * Injecting all the fields used in cq dialog of hero image component here !
	 */
	@Inject private String desktopImageName;
	@Inject private String desktopImageReference;
	@Inject private String mobileImageName;
	@Inject private String mobileImageReference;
	@Inject private String tabletImageName;
	@Inject private String tabletImageReference;
	@Inject private String imageBorder;
	@Inject private String altText;
	@Inject private String text;
	@Inject private String description;
	@Inject private String textAlignment;
	@Inject private String linkText;
	@Inject private String linkUrl;
	private String defaultAltText;
	@SlingObject
	private ResourceResolver resourceResolver;

	/**
	 *  To get desktop image name
	 */
	public String getDesktopImageName() {

		return desktopImageName;
	}
	
	/**
	 * to set the default alt text that will be extracted from desktop image reference by getImageName()
	 * function from IxmUtils. And to get an externalized link for desktop image reference .
	 */
	public String getDesktopImageReference() {
		defaultAltText = IxmUtils.getImageName(resourceResolver,desktopImageReference);
		desktopImageReference = IxmUtils.getExternalizedLink(desktopImageReference, resourceResolver);
		return desktopImageReference;
	}

	/** 
	 * to get mobile image name.
	 * 
	 */
	public String getMobileImageName() {
		return mobileImageName;
	}

	/**
	 * to get externalized link for mobile image reference . In case of mobile image reference is null
	 * then function will return externalized desktop image reference as default.
	 */
	public String getMobileImageReference() {
		if(mobileImageReference == null)
			 return desktopImageReference;
		mobileImageReference = IxmUtils.getExternalizedLink(mobileImageReference, resourceResolver);
		return mobileImageReference;
	}

	/**
	 *  to get tablet image name
	 */
	public String getTabletImageName() {
		return tabletImageName;
	}
	
	/**
	 * to get externalized link for tablet image reference . In case of tablet image reference is null
	 * then function will return externalized desktop image reference as default.
	 */
	public String getTabletImageReference() {
		if(tabletImageReference == null)
			 return desktopImageReference;
		tabletImageReference = IxmUtils.getExternalizedLink(tabletImageReference, resourceResolver);
		return tabletImageReference;
	}

	/** 
	 * to get image border
	 */
	public String getImageBorder() {
		return imageBorder;
	}

	/**
	 * to get alt text of an image. In case of alt text is null default alt text (image name from
	 * desktop image reference will be returned.
	 */
	public String getAltText() {
		if(altText==null)
			return defaultAltText;
		return altText;
	}
	
	/**
	 * to get text
	 */
	public String getText() {
		return text;
	}

	/** 
	 * to get description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * to get text alignment
	 */
	public String getTextAlignment() {
		return textAlignment;
	}

	/**
	 * to get link text for CTA button
	 */
	public String getLinkText() {
		return linkText;
	}

	/**
	 * to get link url for CTA button
	 */
	public String getLinkUrl() {
		return linkUrl;
	}
	
	
}
