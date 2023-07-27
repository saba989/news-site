package com.ixm.core.models;

import com.day.cq.commons.Externalizer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ServiceOfferingsTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
    private final String DEST_PATH="/content/ixm-aem/us/en";
    private ServiceOfferings serviceOfferings;
    private Externalizer externalizer;

    @BeforeEach
    void setUp() {
        aemContext.addModelsForClasses(ServiceOfferings.class);
        aemContext.load().json("/com/ixm/core/models/service-offerings.json", DEST_PATH);
        externalizer = mock(Externalizer.class);
        aemContext.registerAdapter(ResourceResolver.class, Externalizer.class, externalizer);
        when(externalizer.externalLink(any(), any(), eq("/content/ixm-aem/us/en/homepage-one"))).thenReturn("/content/ixm-aem/us/en/homepage-one");
        when(externalizer.externalLink(any(), any(), eq("/content/ixm-aem/us/en/homepage-two"))).thenReturn("/content/ixm-aem/us/en/homepage-two");
        when(externalizer.externalLink(any(), any(), eq("/content/ixm-aem/us/en/homepage-three"))).thenReturn("/content/ixm-aem/us/en/homepage-three");
        Resource serviceOfferingsResource = aemContext.resourceResolver().getResource(DEST_PATH + "/serviceofferings");
        serviceOfferings = aemContext.getService(ModelFactory.class).createModel(serviceOfferingsResource, ServiceOfferings.class);
    }

    @Test
    void getTitle() {
        final String expected = "Title";
        String actual = serviceOfferings.getTitle();
        assertEquals(expected, actual);
    }

    @Test
    void getSubTitle() {
        final String expected = "Sub Title";
        String actual = serviceOfferings.getSubTitle();
        assertEquals(expected, actual);
    }

    @Test
    void getCtaText() {
        final String expected = "CTA text";
        String actual = serviceOfferings.getCtaText();
        assertEquals(expected, actual);
    }

    @Test
    void getCtaLinkInternal() {
        final String expected = "/content/ixm-aem/us/en/homepage-one.html";
        final String actual = serviceOfferings.getCtaLink();
        assertEquals(expected, actual);
    }

    @Test
    void getCtaLinkExternal() {
        Resource resource = aemContext.resourceResolver().getResource(DEST_PATH + "/serviceofferings-two");
        ServiceOfferings serviceOfferings = aemContext.getService(ModelFactory.class).createModel(resource, ServiceOfferings.class);
        final String expected = "www.google.com";
        final String actual = serviceOfferings.getCtaLink();
        assertEquals(expected, actual);
    }

    @Test
    void getLayout() {
        final String expected = "tiles";
        String actual = serviceOfferings.getLayout();
        assertEquals(expected, actual);
    }

    @Test
    void offeringsCount() {
        final int expected = 3;
        final int actual = serviceOfferings.getOfferings().size();
        assertEquals(expected, actual);
    }

    @Test
    void testDesktopImageFile() {
        final String expected = "/content/ixm-aem/us/en/homepage-one/jcr:content/root/container/heroimage/desktopImage";
        final String actual = serviceOfferings.getOfferings().get(0).getImageURL();
        assertEquals(expected, actual);
    }
    @Test
    void testDesktopImageReference() {
        final String expected = "/content/ixm-aem/us/en/asset.jpg";
        final String actual = serviceOfferings.getOfferings().get(1).getImageURL();
        assertEquals(expected, actual);
    }

    @Test
    void testOfferingPageTitle() {
        final String expected = "Homepage One";
        final String actual = serviceOfferings.getOfferings().get(0).getTitle();
        assertEquals(expected, actual);
    }

    @Test
    void testOfferingPageURL() {
        final String expected = "/content/ixm-aem/us/en/homepage-one.html";
        final String actual = serviceOfferings.getOfferings().get(0).getPageURL();
        assertEquals(expected, actual);
    }

    private void testAltText(final String expected, final int index) {
        final String actual = serviceOfferings.getOfferings().get(index).getAltText();
        assertEquals(expected, actual);
    }

    @Test
    void getAltText() {
        testAltText("This is alt-text", 1);
    }

    @Test
    void testNoDedicatedAltTextWithoutAssetImage() {
        testAltText("local-machine-uploaded-image.jpg", 0);
    }

    @Test
    void testNoDedicatedAltTextWithAssetImage() {
        testAltText("asset", 2);
    }

    @Test
    void testPageWithoutHeroImageComponent() {
        when(externalizer.externalLink(any(), any(), eq("/content/ixm-aem/us/en/page-without-heroimage"))).thenReturn("/content/ixm-aem/us/en/page-without-heroimage");
        Resource resource = aemContext.resourceResolver().getResource(DEST_PATH + "/serviceofferings-two");
        ServiceOfferings serviceOfferings = aemContext.getService(ModelFactory.class).createModel(resource, ServiceOfferings.class);
        final String offeringTitle = "Homepage";
        final String offeringPageURL = "/content/ixm-aem/us/en/page-without-heroimage.html";
        assertEquals(offeringTitle, serviceOfferings.getOfferings().get(0).getTitle());
        assertEquals(offeringPageURL, serviceOfferings.getOfferings().get(0).getPageURL());
        assertNull(serviceOfferings.getOfferings().get(0).getImageURL());
        assertNull(serviceOfferings.getOfferings().get(0).getAltText());
    }
}