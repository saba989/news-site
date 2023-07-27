package com.ixm.core.solr.servlets;

import com.google.gson.Gson;
import com.ixm.core.solr.constants.SolrConstants;
import com.ixm.core.solr.service.SolrService;
import com.ixm.core.solr.utils.ResponseEntity;
import com.ixm.core.utils.IxmUtils;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.ixm.core.solr.constants.SolrConstants.*;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/solr")
@ServiceDescription("Servlet for Updating and Fetching Solr data.")
public class SolrDataServlet extends SlingAllMethodsServlet {

    private transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Reference private transient SolrService solrService;
    @Reference private transient JobManager jobManager;
    @Reference private transient ResourceResolverFactory resolverFactory;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {
            final List<ResponseEntity> responseEntities = solrService.getData(request);
            response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            response.getWriter().println(new Gson().toJson(responseEntities));
        } catch (final URISyntaxException | IOException e) {
            logger.error("An exception occurred while fetching data from Solr. ", e);
        }
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {
        final PrintWriter writer = response.getWriter();
        try(final ResourceResolver resourceResolver =
                    IxmUtils.getResourceResolver(resolverFactory, SolrConstants.SYSTEM_USER_SERVICE_NAME)) {
            final String collectionName = request.getParameter(SolrConstants.PARAM_CORE_NAME);
            final List<String> pagePaths = new ArrayList<>();
            for(final String pagePath : request.getParameterMap().get(PARAM_PATHS))
                pagePaths.add(pagePath);
            final boolean isIndexed = Boolean.parseBoolean(request.getParameter(SolrConstants.PARAM_INDEX_CHECKBOX));

            if(solrService.hasCore(collectionName))
                logger.debug("Core already exists.");
            else {
                if(solrService.createCore(collectionName) == HttpStatus.SC_OK) {
                    logger.debug("Core created successfully.");
                }
                else {
                    logger.debug("Error in creating core.");
                    writer.print(FAILED);
                }
            }

            final Resource collectionNode = solrService.getOrCreateCollectionNode(resourceResolver, collectionName);
            ModifiableValueMap map = collectionNode.adaptTo(ModifiableValueMap.class);
            if(null != map) {
                map.put(SolrConstants.PARAM_CORE_NAME, collectionName);
                map.put(SolrConstants.PARAM_INDEX_CHECKBOX, isIndexed);
                map.put(SolrConstants.PARAM_PATHS, pagePaths.toArray());
            }
            resourceResolver.commit();

            solrService.deleteIndexedData(collectionName);
            logger.debug("Successfully deleted indexed data in core {}.", collectionName);

            if(isIndexed) {
                if (solrService.indexPages(collectionName, pagePaths, resourceResolver) == HttpStatus.SC_OK)
                    logger.debug("Indexing is successful.");
                else {
                    logger.debug("Indexing failed.");
                    writer.print(FAILED);
                }
            }
            writer.print(OK);
        } catch (final URISyntaxException | InterruptedException | IOException e) {
            logger.error("An exception occurred. ", e);
            writer.print(FAILED);
        } catch (LoginException e) {
            logger.error("Login exception occurred while getting resource resolver. ", e);
            writer.print(FAILED);
        }
    }
}