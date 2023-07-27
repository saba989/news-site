package com.ixm.core.migration.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ixm.core.migration.jobs.MigrationJob;

/**
 * Defines the {@code MigrationServlet} used for the {@code /apps/ixm-aem/components/utilities/migrationUtility}.
 *
 */
@Component(service = Servlet.class) 
@SlingServletPaths("/bin/migration")
@ServiceDescription("IXM Migration servlet")
public class MigrationServlet extends SlingAllMethodsServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(MigrationServlet.class);

	@Reference
	private transient JobManager jobManager;

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.debug("Inside servlet");
		Map<String, Object> map = new HashMap<>();
		jobManager.addJob(MigrationJob.JOB_TOPIC, map);
		response.getWriter().println("The request for processing the site structure is taken and will continue to run in background.");
		response.setStatus(SlingHttpServletResponse.SC_OK);
	}

}
