package com.ixm.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
public class StyleNameModelTest {
	private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
	private final String DEST_PATH = "/content/ixm-aem/us/en";
	private StyleNameModel styleNameModel;
		
	@BeforeEach
	public void setUp() throws Exception {
		aemContext.addModelsForClasses(StyleNameModel.class);
	}

	@Test
	void getStyleNameTest() {
		aemContext.load().json("/com/ixm/core/models/styleName.json", DEST_PATH);
		Resource styleResource = aemContext.resourceResolver().getResource(DEST_PATH + "/stylePresent/jcr:content");
		styleNameModel = aemContext.getService(ModelFactory.class).createModel(styleResource, StyleNameModel.class);
		assertEquals("ixm-aem.site", styleNameModel.getStyleNameSite());
	}

	@Test
	void getStyleNameDependencyTest() {
		aemContext.load().json("/com/ixm/core/models/styleName.json", DEST_PATH);
		Resource styleResource = aemContext.resourceResolver().getResource(DEST_PATH + "/stylePresent/jcr:content");
		styleNameModel = aemContext.getService(ModelFactory.class).createModel(styleResource, StyleNameModel.class);
		assertEquals("ixm-aem.dependencies", styleNameModel.getStyleNameDependency());
	}
	
	@Test 
	void getStyleNotPresentTest() {
		aemContext.load().json("/com/ixm/core/models/styleName.json", DEST_PATH);
		Resource styleResource = aemContext.resourceResolver().getResource(DEST_PATH+"/styleNotPresent/jcr:content");
		styleNameModel = aemContext.getService(ModelFactory.class).createModel(styleResource, StyleNameModel.class);
		assertEquals("ixm-aem.site", styleNameModel.getStyleNameSite());
	}
	@Test
	void getStyleNotPrsntDependencyTest() {
		aemContext.load().json("/com/ixm/core/models/styleName.json", DEST_PATH);
		Resource styleResource = aemContext.resourceResolver().getResource(DEST_PATH+"/styleNotPresent/jcr:content");
		styleNameModel = aemContext.getService(ModelFactory.class).createModel(styleResource, StyleNameModel.class);
		assertEquals("ixm-aem.dependencies", styleNameModel.getStyleNameDependency());
	}
}