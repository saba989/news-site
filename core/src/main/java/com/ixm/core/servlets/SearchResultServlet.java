package com.ixm.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.ixm.core.constants.Constants;
import com.ixm.core.utils.IxmUtils;

/**
 * 
 * Servlet to create search component
 *
 */
@Component(service = Servlet.class)
@SlingServletPaths("/bin/servlet/searchResultServlet")
@ServiceDescription("IXM AEM Search Servlet")
public class SearchResultServlet extends SlingAllMethodsServlet {
	private static final Logger log = LoggerFactory.getLogger(SearchResultServlet.class);

	/**
	 * method to send search query as reponse
	 */
	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException, ServletException {

		final String searchTerm = request.getParameter(Constants.PARAM_SEARCH_TERM);
		final String resultsSize = request.getParameter(Constants.PARAM_RESULTS_SIZE);
		final String searchPath = request.getParameter(Constants.PARAM_SEARCH_PATH);

		final Map<String, String> predicate = new HashMap<>();
		predicate.put(Constants.KEY_PATH, searchPath);
		predicate.put(Constants.KEY_TYPE, "cq:Page");	//NOSONAR
		predicate.put(Constants.KEY_FULLTEXT, searchTerm);
		predicate.put(Constants.KEY_P_LIMIT, resultsSize);

		final ResourceResolver resourceResolver = request.getResourceResolver();
		final Session session = resourceResolver.adaptTo(Session.class);
		final QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
		final Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);
		final SearchResult searchResult = query.getResult();

		final Map<String, String> pageMap = new HashMap<>();

		searchResult.getHits().forEach(hit -> {
			try {
				final Page page = hit.getResource().adaptTo(Page.class);
				if (null != page) {
					final String link = IxmUtils.getExternalizedLink(page.getPath(), resourceResolver)
							+ Constants.HTML_EXTENSION;
					pageMap.put(page.getTitle(), link);
				}
			} catch (final RepositoryException e) {
				log.error("Error, cannot find page", e);
			}
		});

		final Gson gson = new Gson();
		final String jsonString = gson.toJson(pageMap);
		response.setContentType(Constants.CONTENT_TYPE_JSON);
		response.getWriter().print(jsonString);
	}
}