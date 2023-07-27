package com.ixm.core.solr.servlets;

import com.ixm.core.solr.constants.SolrConstants;
import com.ixm.core.solr.jobs.SolrQueryBoostJob;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import java.util.*;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/solr/queryBoost")
@ServiceDescription("Servlet for query boosting")
public class QueryBoostServlet extends SlingAllMethodsServlet {

	@Reference private transient JobManager jobManager;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
		final Map<String, String> paramMap = new HashMap();
		request.getRequestParameterList()
				.forEach(param -> paramMap.put(param.getName(), param.getString()));
		final Map<String, Object> jobMap = new HashMap();
		jobMap.put(SolrConstants.PROP_PARAM_MAP, paramMap);
		jobManager.addJob(SolrQueryBoostJob.TOPIC, jobMap);
	}
}