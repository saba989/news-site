package com.ixm.core.solr.servlets;

import com.ixm.core.solr.constants.SolrConstants;
import com.ixm.core.solr.jobs.SolrDeleteDataJob;
import com.ixm.core.solr.service.SolrService;
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

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/solr/delete")
@ServiceDescription("Servlet for Deleting Solr data.")
public class SolrDeleteCoreServlet extends SlingAllMethodsServlet {

    private transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Reference private transient SolrService solrService;
    @Reference private transient JobManager jobManager;
    
    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
    	final String path = request.getParameter(SolrConstants.PATH);
        final Map<String,Object> map = new HashMap();
        map.put(SolrConstants.PARAM_CORE_NAME, path.substring(path.lastIndexOf("/")+1));
        jobManager.addJob(SolrDeleteDataJob.TOPIC, map);

    }
}