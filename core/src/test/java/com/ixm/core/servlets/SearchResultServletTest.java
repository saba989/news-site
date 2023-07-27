package com.ixm.core.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.Externalizer;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class SearchResultServletTest {

	private final AemContext aemContext = new AemContext();

	SearchResultServlet searchResultServlet = new SearchResultServlet();

	@Mock
	private Query query;

	@Mock
	private SearchResult searchResult;

	@Mock
	private Session session;

	@Mock
	private Hit hit;

	@Test
	void testDoGetSlingHttpServletRequestSlingHttpServletResponse()
			throws IOException, ServletException, RepositoryException {
		final MockSlingHttpServletRequest request = aemContext.request();
		final MockSlingHttpServletResponse response = aemContext.response();

		request.addRequestParameter("searchTerm", "car");
		request.addRequestParameter("searchPath", "/content/ixm-aem/us/en");
		request.addRequestParameter("resultsSize", "10");

		Session session = mock(Session.class);
		aemContext.registerAdapter(ResourceResolver.class, Session.class, session);

		QueryBuilder queryBuilder = mock(QueryBuilder.class);
		aemContext.registerAdapter(ResourceResolver.class, QueryBuilder.class, queryBuilder);
		when(queryBuilder.createQuery(any(), any())).thenReturn(query);

		List<Hit> results = new ArrayList<>();
		Hit pageResult1 = mock(Hit.class);
		Resource resource1 = mock(Resource.class);
		when(pageResult1.getResource()).thenReturn(resource1);
		Page page1 = mock(Page.class);
		when(resource1.adaptTo(Page.class)).thenReturn(page1);

		when(page1.getPath()).thenReturn("/content/ixm-aem/us/en/page1");
		when(page1.getTitle()).thenReturn("page1");

		results.add(pageResult1);

		when(query.getResult()).thenReturn(searchResult);

		when(searchResult.getHits()).thenReturn(results);

		final Externalizer externalizer = mock(Externalizer.class);
		aemContext.registerAdapter(ResourceResolver.class, Externalizer.class, externalizer);
		when(externalizer.externalLink(any(), any(), any())).thenReturn("/content/ixm-aem/us/en/page1");

		searchResultServlet.doGet(request, response);

		assertEquals("{\"page1\":\"/content/ixm-aem/us/en/page1.html\"}", response.getOutputAsString());
	}
}
