package com.ixm.core.models;

import com.day.cq.wcm.api.Page;
import com.ixm.core.constants.Constants;
import com.ixm.core.utils.IxmUtils;
import com.ixm.core.utils.PageUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines the {@code Service Offerings} Sling Model used for the {@code /apps/ixm-aem/components/serviceofferings} component.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ServiceOfferings {
    private static final Logger log = LoggerFactory.getLogger(ServiceOfferings.class);

    @SlingObject
    private Resource resource;
    @SlingObject
    private ResourceResolver resourceResolver;
    @Inject
    private String title;
    @Inject
    private String subTitle;
    @Inject
    private String ctaText;
    @Inject
    private String ctaLink;

    @Inject
    private String layout;
    @Inject
    private List<Offering> offerings = new ArrayList<>();

    /**
     * Assigns {@code pageTitle, imageURL, pageURL, altText} from each path
     * which is provided in {@code offerings} field of {@code Service Offering} component
     *
     * @see #setOfferingImageProperties(Offering, Resource)
     */
    @PostConstruct
    public void init() {
        offerings.forEach(offering -> {
            try {
                final Page page = PageUtils.getPage(offering.getPageURL(), resourceResolver);
                if (null != page) {
                    final Resource imageResource = page.getContentResource(Constants.HERO_IMAGE_PATH);
                    if (null != imageResource) {
                        setOfferingImageProperties(offering, imageResource);
                    }
                    final String pageURL = IxmUtils.getExternalizedLink(offering.getPageURL(),
                            resourceResolver) + Constants.HTML_EXTENSION;
                    offering.setTitle(page.getTitle());
                    offering.setPageURL(pageURL);
                }
            }
            catch (final IllegalArgumentException e) {
                log.error("Provided path to Hero Image component is not a relative path.", e);
            }
        });
    }

    /**
     * Assigns {@code imageURL, altText} to {@code Offering} object.
     * If altText property is not provided in hero component, it will assign image name.
     *
     * @param offering
     * @param imageResource
     */
    private void setOfferingImageProperties(final Offering offering, final Resource imageResource) {
        ValueMap valueMap = imageResource.getValueMap();
        String altText = IxmUtils.getStringProperties(valueMap, Constants.PN_ALT_TEXT);
        String imageURL = IxmUtils.getStringProperties(valueMap, Constants.PN_DESKTOP_IMAGE_REFERENCE);
        if (!imageURL.isEmpty() && altText.isEmpty()) {
            altText = IxmUtils.getImageName(resourceResolver, imageURL);
        }
        if (imageURL.isEmpty()) {
            imageURL = imageResource.getPath() + Constants.SLASH_DELIMITER + Constants.NN_DESKTOP_IMAGE_FILE;
            if (altText.isEmpty()) {
                altText = IxmUtils.getStringProperties(imageResource.getValueMap(), Constants.PN_DESKTOP_IMAGE_NAME);
            }
        }
        offering.setImageURL(imageURL);
        offering.setAltText(altText);
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getCtaText() {
        return ctaText;
    }

    public String getCtaLink() {
        if (IxmUtils.isExternalURL(ctaLink)) {
            return ctaLink;
        }
        return IxmUtils.getExternalizedLink(ctaLink, resourceResolver) + Constants.HTML_EXTENSION;
    }

    public String getLayout() {
        return layout;
    }

    public List<Offering> getOfferings() {
        return Collections.unmodifiableList(offerings);
    }
}