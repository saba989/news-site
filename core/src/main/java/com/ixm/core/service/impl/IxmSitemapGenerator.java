package com.ixm.core.service.impl;

import java.util.Calendar;
import java.util.Optional;

import com.ixm.core.constants.Constants;
import com.ixm.core.utils.IxmUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.sitemap.SitemapException;
import org.apache.sling.sitemap.builder.Sitemap;
import org.apache.sling.sitemap.builder.Url;

import org.apache.sling.sitemap.spi.generator.ResourceTreeSitemapGenerator;
import org.apache.sling.sitemap.spi.generator.SitemapGenerator;
import org.osgi.service.component.annotations.Component;

import org.osgi.service.component.propertytypes.ServiceRanking;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;


import javax.inject.Inject;


@Component(service = SitemapGenerator.class)
@ServiceRanking(20)
public class IxmSitemapGenerator extends ResourceTreeSitemapGenerator {

    @Inject
    private String location;

    @Inject
    private String locations;


    @Override
    protected void addResource(final String name, final Sitemap sitemap, final Resource resource) throws SitemapException {
        Page page = resource.adaptTo(Page.class);
        /**
         * to get the path of pages
         */
        location = page.getPath() + Constants.HTML_EXTENSION;
        /**
         * return pages
         */

        locations = IxmUtils.getExternalizedLink(location, resource.getResourceResolver());

        final Url url = sitemap.addUrl(locations);

        final Calendar lastmod = Optional.ofNullable(page.getLastModified())
                .orElse(page.getContentResource()
                        .getValueMap()
                        .get(JcrConstants.JCR_CREATED, Calendar.class));
        if (lastmod != null) {
            url.setLastModified(lastmod.toInstant());
            url.setPriority(2);
        }


    }


}

