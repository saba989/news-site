package com.ixm.core.models;

import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.Externalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class LinkTest {

	private final String RESOURCE_PATH = "/content/ixm/us/en";
	private final AemContext aemContext = new AemContext();
	private Link link;

	private Externalizer externalizer;

	@BeforeEach
	void setUp() throws Exception {
		aemContext.addModelsForClasses(Link.class);
		aemContext.load().json("/com/ixm/core/models/link-test.json", RESOURCE_PATH);
		externalizer = mock(Externalizer.class);
		aemContext.registerAdapter(ResourceResolver.class, Externalizer.class, externalizer);
		link = aemContext.currentResource(RESOURCE_PATH).adaptTo(Link.class);
	}

	@Test
	void getLinkDetailsWithMap() {
		assertEquals(3, link.getLinkDetailsWithMap().size());
	}

	@Test
	void checkLinkTargetCheckbox() {
		String expected = "_blank";
		String actual = link.getLinkDetailsWithMap().get(0).get("linkCheckbox");
		assertEquals(expected, actual);
	}

	@Test
	void checkImageUrl() {
		String expected = "/content/dam/ixm-aem/asset.jpg";
		String actual = link.getLinkDetailsWithMap().get(1).get("imageUrl");
		assertEquals(expected, actual);
	}

	@Test
	void checkLinkUrlExternal() {
		String expected = "https://www.amazon.com";
		String actual = link.getLinkDetailsWithMap().get(2).get("linkUrl");
		assertEquals(expected, actual);
	}

	@Test
	void checkLinkUrlInternal() {
        when(externalizer.externalLink(any(), any(),any())).thenReturn("/content/ixm-aem/us/en/HomePage");
        String expected = "/content/ixm-aem/us/en/HomePage.html";
        String actual = link.getLinkDetailsWithMap().get(1).get("linkUrl");
        assertEquals(expected, actual);
    }
	

	@Test
	void checkLinkText() {
		String expected = "Amazon";
		String actual = link.getLinkDetailsWithMap().get(2).get("linkText");
		assertEquals(expected, actual);

	}

	@Test
	void checkAltText() {
		String expected = "This is my entered alt Text.";
		String actual = link.getLinkDetailsWithMap().get(1).get("altText");
		assertEquals(expected, actual);
		Resource imgResource = aemContext.load().json("/com/ixm/core/models/link-test.json",
				"/content/dam/ixm-aem/asset.jpg");
		assertEquals("asset", link.getLinkDetailsWithMap().get(2).get("altText"));

	}

	@Test
	void checkLinkTextNotNull() {
		assertNotNull(link.getLinkDetailsWithMap().get(1).get("linkText"));
	}

	@Test
	void checkLinkUrlNotNull() {
		assertNotNull(link.getLinkDetailsWithMap().get(1).get("linkUrl"));
	}

	@Test
	void checkImageUrlNotNull() {
		assertNotNull(link.getLinkDetailsWithMap().get(1).get("imageUrl"));
	}

	@Test
	void checkLinkTargetCheckboxNotNull() {
		assertNotNull(link.getLinkDetailsWithMap().get(1).get("linkCheckbox"));
	}

	@Test
	void checkAltTextNotNull() {
		assertNotNull(link.getLinkDetailsWithMap().get(1).get("altText"));
	}

}
