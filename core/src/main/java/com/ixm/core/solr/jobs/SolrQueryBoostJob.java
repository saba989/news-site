package com.ixm.core.solr.jobs;

import com.ixm.core.solr.constants.SolrConstants;
import com.ixm.core.solr.service.SolrService;
import com.ixm.core.utils.IxmUtils;
import org.apache.sling.api.resource.*;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component(
        immediate = true,
        service = JobConsumer.class,
        property = {JobConsumer.PROPERTY_TOPICS + "=" + SolrQueryBoostJob.TOPIC})
public class SolrQueryBoostJob implements JobConsumer {

    public static final String TOPIC = "job/solr/queryBoost";
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Reference private ResourceResolverFactory resolverFactory;
    @Reference private SolrService solrService;

    @Override
    public JobResult process(final Job job) {
        try(final ResourceResolver resourceResolver = IxmUtils
                .getResourceResolver(resolverFactory, SolrConstants.SYSTEM_USER_SERVICE_NAME)) {
            final Resource queryBoostNode = solrService.getOrCreateQueryBoostNode(resourceResolver);

            final ModifiableValueMap map = queryBoostNode.adaptTo(ModifiableValueMap.class);
            for (final String key: SolrConstants.KEYS) {
                if(map.containsKey(key)) {
                    map.remove(key);
                    resourceResolver.commit();
                }
            }

            final Map<String, String> paramMap = (Map<String, String>) job.getProperty(SolrConstants.PROP_PARAM_MAP);
            paramMap.entrySet().removeIf(entry -> entry.getValue().equals(SolrConstants.VALUE_ZERO));
            map.putAll(paramMap);
            resourceResolver.commit();
            return JobResult.OK;
        } catch (final PersistenceException | LoginException e) {
            logger.error("Exception occurred. ", e);
            return JobResult.FAILED;
        }
    }
}
