package com.ixm.core.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.Externalizer;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class FilterCardServletTest {

	private final AemContext aemContext = new AemContext();

	FilterCardsServlet filterCardsServlet = new FilterCardsServlet();

	private final String RESOURCE_PATH_PAGE1 = "/content/ixm-aem/us/filtercard/page1";
	private final String RESOURCE_PATH_PAGE2 = "/content/ixm-aem/us/filtercard/page2";

	@Mock
	private Query query;

	@Mock
	private SearchResult searchResult;

	@Mock
	private Hit hit;
	
	@Mock
	private Tag tag;

	@BeforeEach
	void setUp() throws Exception {
		aemContext.load().json("/com/ixm/core/servlet/filterCards/page1.json", RESOURCE_PATH_PAGE1);
		aemContext.load().json("/com/ixm/core/servlet/filterCards/page2.json", RESOURCE_PATH_PAGE2);

	}

	@Test
	void testDoGetSlingHttpServletRequestSlingHttpServletResponse()
			throws AccessControlException, InvalidTagFormatException, IOException {
		final MockSlingHttpServletRequest request = aemContext.request();
		final MockSlingHttpServletResponse response = aemContext.response();
		request.addRequestParameter("rootPagePath", "/content/ixm-aem/us/filtercard");
		request.addRequestParameter("tagList", "ixm:child/fun");
		request.addRequestParameter("imagePathReference", "/jcr:content/root/container/heroimage");

		final QueryBuilder queryBuilder = mock(QueryBuilder.class);
		aemContext.registerAdapter(ResourceResolver.class, QueryBuilder.class, queryBuilder);
		when(queryBuilder.createQuery(any(), any())).thenReturn(query);
		final List<Hit> results = new ArrayList<>();
		Hit pageResult1 = mock(Hit.class);
		Hit pageResult2 = mock(Hit.class);
		try {
			when(pageResult1.getPath()).thenReturn("/content/ixm-aem/us/filtercard/page1");
			when(pageResult2.getPath()).thenReturn("/content/ixm-aem/us/filtercard/page2");
		} catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		results.add(pageResult1);
		results.add(pageResult2);
		when(query.getResult()).thenReturn(searchResult);
		when(searchResult.getHits()).thenReturn(results);

		Page page = mock(Page.class);
		
		

		final Externalizer externalizer = mock(Externalizer.class);
		aemContext.registerAdapter(ResourceResolver.class, Externalizer.class, externalizer);
		when(externalizer.externalLink(any(), any(), any())).thenReturn("/content/ixm-aem/us/filtercard/page1");
		filterCardsServlet.doGet(request, response);
		String res="{\"pages\":[{\"imagePath\":\"Passport_Photograph.jpg\",\"pageUrl\":\"/content/ixm-aem/content/ixm-aem/us/filtercard/page1.html\",\"pageTitle\":\"page1\",\"imageAltText\":\"altText\",\"tags\":[]},{\"imagePath\":\"/content/dam/ixm-aem/asset2.jpg\",\"pageUrl\":\"/content/ixm-aem/content/ixm-aem/us/filtercard/page2.html\",\"pageTitle\":\"page2\",\"imageAltText\":\"/content/dam/ixm-aem/asset2.jpg\",\"tags\":[]}],\"tags\":{}}";
		assertEquals(res, response.getOutputAsString());

	}

}