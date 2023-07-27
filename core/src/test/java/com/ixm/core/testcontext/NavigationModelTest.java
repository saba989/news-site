package com.ixm.core.testcontext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.PageManager;
import com.ixm.core.models.NavigationModel;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })

class NavigationModelTest {
	private final String RESOURCE_PATH = "/content/ixm-aem/us/en";
	private final AemContext aemContext = new AemContext();

	private NavigationModel navigationModel;
	@Mock
	PageManager pageManager;

	@BeforeEach
	public void setUp() throws Exception {
		aemContext.addModelsForClasses(NavigationModel.class);
		aemContext.load().json("/com/ixm/core/models/navigation.json", RESOURCE_PATH);
		aemContext.load().json("/com/ixm/core/models/page.json", "/content/ixm-aem/us/test1");
		aemContext.load().json("/com/ixm/core/models/page.json", "/content/ixm-aem/us/test2");
		navigationModel = aemContext.currentResource(RESOURCE_PATH).adaptTo(NavigationModel.class);
	}

	@Test
	void getNavigationPagesSize() {
		assertEquals(2, navigationModel.getNavigationPages().size());
	}

	@Test
	void getPagePath() {
		assertEquals("/content/ixm-aem/us/test1", navigationModel.getNavigationPages().get(0).getPath());
	}

}