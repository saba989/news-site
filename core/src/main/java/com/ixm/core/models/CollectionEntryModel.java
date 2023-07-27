package com.ixm.core.models;

import com.ixm.core.solr.constants.SolrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;

import java.util.Arrays;

@Model(adaptables = SlingHttpServletRequest.class) public class CollectionEntryModel {

	private String index;
	private String collectionName;
	private String[] paths;

	public CollectionEntryModel(SlingHttpServletRequest request) {
		ResourceResolver resourceResolver = request.getResourceResolver();
		String path = request.getParameter(SolrConstants.PATH);

		if (null != path) {
			Resource entryResource = resourceResolver.getResource(path);
			if (null != entryResource) {
				ValueMap valueMap = entryResource.getValueMap();
				this.index = valueMap.get(SolrConstants.PARAM_INDEX_CHECKBOX, String.class);
				this.collectionName = valueMap.get(SolrConstants.PARAM_CORE_NAME, String.class);
				this.paths = valueMap.get(SolrConstants.PARAM_PATHS, String[].class);
			}
		}
	}

	public String getIndex() {
		return index;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String[] getPaths() {
		return Arrays.copyOf(paths, paths.length);
	}

}
