package com.ixm.core.models;

import com.ixm.core.constants.Constants;
import com.ixm.core.utils.IxmUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Link {

    private static final Logger LOG = LoggerFactory.getLogger(Link.class);

    @ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "No resourceType")
    protected String resourceType;

    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;

    /**
     * Method to get Link Map
     * 
     * @return link map after updating the properties
     */
    public List<Map<String, String>> getLinkDetailsWithMap() {
        List<Map<String, String>> linkDetailsMap = new ArrayList<>();

        Resource linkDetail = currentResource.getChild("linkdetailswithmap");
        if (null != linkDetail) {
            for (Resource link : linkDetail.getChildren()) {
                Map<String, String> linkMap = new HashMap<>();

                ValueMap valueMap = link.getValueMap();

                // getting link url
                String rawLink = IxmUtils.getStringProperties(valueMap, Constants.PN_LINK_URL);

                // changing link url to externalized link
                if (!IxmUtils.isExternalURL(rawLink) && !StringUtils.isEmpty(rawLink.trim())) {
                    rawLink = IxmUtils.getExternalizedLink(rawLink, resourceResolver)
                            + Constants.HTML_EXTENSION;
                }

                // getting alt-text for image
                String imageUrl = IxmUtils.getStringProperties(valueMap, Constants.PN_IMAGE_URL);
                String altText = StringUtils.EMPTY;
                if (!StringUtils.isEmpty(imageUrl.trim())) {
                    altText = IxmUtils.getStringProperties(valueMap, Constants.PN_ALT_TEXT);

                    if (StringUtils.isEmpty(altText.trim())) {
                        altText = IxmUtils.getImageName(resourceResolver,
                                IxmUtils.getStringProperties(valueMap, Constants.PN_IMAGE_URL));
                    }
                }

                // get page target value
                String targetVal;
                if (IxmUtils.getStringProperties(valueMap, Constants.PN_LINK_CHECKBOX).equals("true")) {
                    targetVal = Constants.PAGE_TARGET_BLANK;
                } else {
                    targetVal = Constants.PAGE_TARGET_SELF;
                }

                // add updated values to linkMap
                linkMap.put(Constants.PN_LINK_TEXT, IxmUtils.getStringProperties(valueMap, Constants.PN_LINK_TEXT));
                linkMap.put(Constants.PN_LINK_URL, rawLink);
                linkMap.put(Constants.PN_IMAGE_URL, IxmUtils.getStringProperties(valueMap, Constants.PN_IMAGE_URL));
                linkMap.put(Constants.PN_LINK_CHECKBOX, targetVal);
                linkMap.put(Constants.PN_ALT_TEXT, altText);

                // adding linkMap to linkDetailsMap list
                linkDetailsMap.add(linkMap);
            }
        } else {
            LOG.debug("The resource linkdetailswithmap is null under the path {}.",currentResource.getPath());
        }

        // returning linkDetailsMap list
        return linkDetailsMap;
    }

}