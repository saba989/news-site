package com.ixm.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import com.google.gson.Gson;
import com.ixm.core.constants.Constants;
import com.ixm.core.utils.CardBean;
import com.ixm.core.utils.IxmUtils;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/servlet/filterCardsServlet")
@ServiceDescription("IXM AEM Filter Cards Servlet")
/*
 * Defines the servlet for the filtering the card based on tags.
 * path=/bin/servlet/filterCardsServlet
 * /apps/ixm-aem/components/filterCards} component
 */
public class FilterCardsServlet extends SlingSafeMethodsServlet {

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {
		final String rootPagePath = request.getParameter(Constants.PARAM_ROOT_PAGE_PATH);
		final String tags = request.getParameter(Constants.PARAM_TAG_LIST);
		final String imagePathReference = request.getParameter(Constants.PARAM_IMAGE_PATH_REFERENCE);
		
		final ResourceResolver resourceResolver = request.getResourceResolver();
		final List<String> filteredPagePaths = IxmUtils.getFilteredPagePaths(rootPagePath, tags, resourceResolver);
		final List<CardBean> filteredCards = IxmUtils.getFilteredCards(filteredPagePaths, resourceResolver,
				imagePathReference);
		final Map<String, Integer> tagCount = IxmUtils.getTagCount(filteredCards, IxmUtils.getInputTagList(tags));
		
		final Map<String, Object> filteredCardsMap = new HashMap<>();
		filteredCardsMap.put(Constants.KEY_PAGES, filteredCards);
		filteredCardsMap.put(Constants.KEY_TAGS, tagCount);
		
		final Gson gson = new Gson();
		response.getWriter().print(gson.toJson(filteredCardsMap));
	}
}
