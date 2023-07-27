package com.ixm.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.commons.Externalizer;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;


@ExtendWith(AemContextExtension.class)
class BlogsTest {
	private final String RESOURCE_PATH = "/content/ixm-aem/us/en/home-page/blogs";
	private final AemContext aemContext = new AemContext();
	private BlogsModel blogsModel;
	
	@BeforeEach
	void setUp() throws Exception {
		aemContext.addModelsForClasses(BlogsModel.class);
		aemContext.load().json("/com/ixm/core/models/blogs/blog.json", RESOURCE_PATH);
		blogsModel = aemContext.currentResource(RESOURCE_PATH).adaptTo(BlogsModel.class);
		
	}
	
	@Test
	void getTitleTest() {
		assertEquals("My Blog", blogsModel.getTitle());
	}

	@Test
	void getSubTitleTest() {
		assertEquals("my personal blog",blogsModel.getSubtitle());
	}
	
	@Test
	void getBlogItems() {
		
		final Externalizer externalizer = mock(Externalizer.class);
		aemContext.registerAdapter(ResourceResolver.class, Externalizer.class, externalizer);
		when(externalizer.externalLink(any(), any(),any())).thenReturn("/content/ixm-aem/us/en/home-page");
	
		
		
		assertEquals(1, blogsModel.getBlogItems().size());
		
		BlogsItems item = blogsModel.getBlogItems().get(0);
		assertEquals("my blog", item.getBlogTitle());
		assertEquals("my blog subtitle", item.getBlogSubTitle());
		assertEquals("This is my first blog", item.getBlogDescription());
		assertEquals("https://www.youtube.com/", item.getBlogLinkUrl());
		assertEquals(false, item.isBlogLinkTarget());

	}
	
	
}
