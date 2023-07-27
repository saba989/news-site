package com.ixm.core.utils;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * A {@code utility} class which contains helper functions for Page resources.
 */
public final class PageUtils {

    private PageUtils() {
    }

    /**
     * Returns {@code page} object of the provided path
     * or {@code null} if {@code path} is {@code null} or invalid.
     *
     * @param path Path to the {@code page} resource.
     * @param resourceResolver
     * @return The Page or {@code null}
     * @see ResourceResolver#getResource(String)
     */
    public static Page getPage(final String path, final ResourceResolver resourceResolver) {
        if (null == path) {
            return null;
        }
        final Resource resource = resourceResolver.getResource(path);
        if (null == resource) {
            return null;
        }
        return resource.adaptTo(Page.class);
    }
}
