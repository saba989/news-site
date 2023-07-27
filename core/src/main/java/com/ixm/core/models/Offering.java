package com.ixm.core.models;

import com.ixm.core.constants.Constants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * A class which contains {@code title, imageURL, pageURL, altText} for Sling Model of Offering field in {@code Service Offerings} component.
 */
@Model(adaptables = Resource.class)
public class Offering {
    private String title;
    private String imageURL;
    @Inject
    @Named(Constants.PN_PATH)
    private String pageURL;
    private String altText;

    public Offering() {
    }

    public Offering(String title, String imageURL, String pageURL, String altText) {
        this.title = title;
        this.imageURL = imageURL;
        this.pageURL = pageURL;
        this.altText = altText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPageURL() {
        return pageURL;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

}
