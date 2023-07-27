package com.ixm.core.solr.jobs;

import com.ixm.core.solr.constants.SolrConstants;
import com.ixm.core.solr.service.SolrService;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

@Component(
        immediate = true,
        service = JobConsumer.class,
        property = {JobConsumer.PROPERTY_TOPICS + "=" + SolrDeleteDataJob.TOPIC})
public class SolrDeleteDataJob implements JobConsumer {

    public static final String TOPIC = "job/solr/deleteCore";
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Reference private SolrService solrService;
    @Reference private ResourceResolverFactory resolverFactory;

    @Override
    public JobResult process(final Job job) {
        try {
            final String collectionName = job.getProperty(SolrConstants.PARAM_CORE_NAME, String.class);
            solrService.deleteCore(collectionName);
            return JobResult.OK;
        } catch (final URISyntaxException | IOException e) {
            logger.error("An exception occurred. ", e);
            return JobResult.FAILED;
        }
    }
}
