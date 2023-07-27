package com.ixm.core.models;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.ixm.core.solr.service.SolrService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ixm.core.solr.constants.SolrConstants.*;

@Model(adaptables = SlingHttpServletRequest.class)
public class CollectionDatasourceModel {

    @Inject
    private SlingHttpServletRequest request;

    @Inject
    private Resource resource;

    @Inject
    private ResourceResolver resourceResolver;

    @Inject @Optional
    private String type;

    @OSGiService private SolrService solrService;

    @PostConstruct
    protected void init() {
        request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        final Resource datasource = resource.getChild(DATASOURCE);
        final ValueMap dsProperties = ResourceUtil.getValueMap(datasource);
        final String itemResourceType = dsProperties.get(ITEM_RESOURCE_TYPE, String.class);
        final String genericListPath = solrService.determineCollectionsPath(resourceResolver);
        if (genericListPath != null) {
            final Resource parentResource = resourceResolver.getResource(genericListPath);
            if (parentResource != null) {
                final List<Resource> fakeResourceList = new ArrayList<>();
                parentResource.getChildren().forEach(child -> {
                    final ValueMap vm = new ValueMapDecorator(new HashMap<>());
                    final ValueMap childProperties = ResourceUtil.getValueMap(child);
                    vm.put(INDEX, childProperties.get(INDEX, String.class));
                    vm.put(COLLECTION_NAME, childProperties.get(COLLECTION_NAME, String.class));
                    vm.put(PATHS, childProperties.get(PATHS, String[].class));
                    vm.put(PATH, child.getPath());
                    vm.put(NAME, child.getName());
                    fakeResourceList.add(new ValueMapResource(resourceResolver, StringUtils.EMPTY, itemResourceType, vm));
                });
                final DataSource ds = new SimpleDataSource(fakeResourceList.iterator());
                request.setAttribute(DataSource.class.getName(), ds);
            }
        }
    }
}
