package com.ixm.core.solr.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ixm.core.models.CollectionEntryModel;
import org.apache.http.entity.ContentType;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/solr/editServlet")
@ServiceDescription("Servlet for editing")
public class SolrEditServlet extends SlingAllMethodsServlet {
	private transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
		CollectionEntryModel collectionEntryModel = new CollectionEntryModel(request);
		try {
			response.getWriter().println(new ObjectMapper().writeValueAsString(collectionEntryModel));
			response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
		} catch (JsonProcessingException e) {
			logger.error("Error while creating object of collectionEntryModel");
		} catch (IOException e) {
			logger.error("IOException occurred");
		}
	}
}