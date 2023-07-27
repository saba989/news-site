package com.ixm.core.service.impl;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.sitemap.SitemapException;
import org.apache.sling.sitemap.builder.Sitemap;
import org.apache.sling.sitemap.builder.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class IxmSitemapGeneratorTest {

    private final String RESOURCE_PATH = "/content/ixm-aem/us/en";
    private final AemContext aemContext = new AemContext();


    private IxmSitemapGenerator ixmSitemap = new IxmSitemapGenerator();


    @BeforeEach
    public void setUp() throws Exception {

        aemContext.load().json("/com/ixm/core/service/impl/IxmSitemap.json", RESOURCE_PATH);

        Resource ixmSitemapResource = aemContext.resourceResolver().getResource(RESOURCE_PATH);

    }

    @Test
    void addResourceTest() throws SitemapException {
        final Page page = mock(Page.class);
        aemContext.registerAdapter(ResourceResolver.class, Page.class, page);
        when(page.getPath()).thenReturn("/content/ixm-aem/us/en");
        final Sitemap sitemap = mock(Sitemap.class);
        aemContext.registerAdapter(ResourceResolver.class, Sitemap.class, sitemap);
        Url url = mock(Url.class);
        aemContext.registerAdapter(ResourceResolver.class,Url.class,url);
        final Externalizer externalizer = mock(Externalizer.class);
        when(sitemap.addUrl(any())).thenReturn(url);
        aemContext.registerAdapter(ResourceResolver.class, Externalizer.class, externalizer);
        when(externalizer.externalLink(any(), any(), any())).thenReturn("/content/ixm-aem/us/en");
        Resource ixmSitemapResource = aemContext.resourceResolver().getResource(RESOURCE_PATH);
        ixmSitemap.addResource("",sitemap,ixmSitemapResource);



    }



}
