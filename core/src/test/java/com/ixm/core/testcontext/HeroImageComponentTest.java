package com.ixm.core.testcontext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.Externalizer;
import com.ixm.core.models.HeroImageComponent;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class HeroImageComponentTest {

	private final String RESOURCE_PATH = "/content/ixm-aem/us/en";
	private final AemContext aemContext = new AemContext();
	private HeroImageComponent heroImageComponent;
	Externalizer externalizer = mock(Externalizer.class);

	@BeforeEach
	public void setUp() throws Exception {
		aemContext.addModelsForClasses(HeroImageComponent.class);
		aemContext.load().json("/com/ixm/core/models/heroImage_test.json", RESOURCE_PATH);
		heroImageComponent = aemContext.currentResource(RESOURCE_PATH).adaptTo(HeroImageComponent.class);
	}

	@Test
	void desktopImageNameTest() {
		assertEquals("asset.jpg", heroImageComponent.getDesktopImageName());
	}

	@Test
	void desktopImageReferenceTest() {
		when(externalizer.externalLink((aemContext.resourceResolver()),
				"/content/dam/ixm-aem/nagarro.png",heroImageComponent.getDesktopImageReference())).thenReturn("pass");
	}

	@Test
	void mobileImageReferenceTest() {
		when(externalizer.externalLink((aemContext.resourceResolver()),
				"/content/dam/ixm-aem/asset.jpg",heroImageComponent.getMobileImageReference())).thenReturn("pass");

	}

	@Test
	void mobileImageReferenceTest1() {
		when(externalizer.externalLink((aemContext.resourceResolver()),
				null,heroImageComponent.getDesktopImageReference())).thenReturn("pass");
	}


	@Test
	void tabletImageReferenceTest() {
		when(externalizer.externalLink((aemContext.resourceResolver()),
				"/content/dam/ixm-aem/nagarro.png",heroImageComponent.getTabletImageReference())).thenReturn("pass");
	}

	@Test
	void imageBorderTest() {
		assertEquals("0", heroImageComponent.getImageBorder());
	}

	@Test
	void altTextTest() {
		assertEquals(null, heroImageComponent.getAltText());
	}

	@Test
	void textTest() {
		assertEquals("Welcome to nagarro", heroImageComponent.getText());
	}

	@Test
	void descriptionTest() {
		assertEquals("<p>description</p>\r\n", heroImageComponent.getDescription());
	}

	@Test
	void textAlignmentTest() {
		assertEquals("left", heroImageComponent.getTextAlignment());
	}

	@Test
	void linkTextTest() {
		assertEquals("Home", heroImageComponent.getLinkText());
	}

	@Test
	void linkUrlTest() {
		assertEquals("/content/ixm/us/en", heroImageComponent.getLinkUrl());
	}

	@Test
	void tabletImageName() {
		assertEquals(null, heroImageComponent.getTabletImageName());
	}

	@Test
	void mobileImageName() {
		assertEquals(null, heroImageComponent.getMobileImageName());
	}
}
