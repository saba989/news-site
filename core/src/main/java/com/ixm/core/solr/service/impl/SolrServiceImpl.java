package com.ixm.core.solr.service.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ixm.core.solr.constants.SolrConstants;
import com.ixm.core.solr.service.SolrConfigurationService;
import com.ixm.core.solr.service.SolrService;
import com.ixm.core.solr.utils.ResponseEntity;
import com.ixm.core.utils.IxmUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.servlets.post.JSONResponse;
import org.jsoup.Jsoup;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.ixm.core.solr.constants.SolrConstants.*;

@Component(service = SolrService.class, property = { Constants.SERVICE_ID + "= IXM Solr Service"})
@ServiceDescription("This service processes the data in solr based on inputs from the author")
@Designate(ocd = SolrConfigurationService.class)
public class SolrServiceImpl implements SolrService {

    private final Logger logger = LoggerFactory.getLogger(SolrServiceImpl.class);
    private SolrConfigurationService config;
    @Reference private RequestResponseFactory requestResponseFactory;
    @Reference private SlingRequestProcessor requestProcessor;
    
    @Activate
    protected void activate(final SolrConfigurationService config) {
        this.config = config;
    }
    
    @Override
    public int createCore(final String collectionName) throws URISyntaxException, IOException {
        final String url =
                getUrl(collectionName, SolrConstants.ADMIN_CORES, SolrConstants.CREATE, SolrConstants.KEY_NAME);
        return retrieveResponse(url).getStatusLine().getStatusCode();
    }
    
    @Override
    public int deleteCore(final String collectionName) throws URISyntaxException, IOException {
        final String url =
                getDeleteUrl(collectionName, true, true, true);
        return retrieveResponse(url).getStatusLine().getStatusCode();
    }

    @Override
    public boolean hasCore(final String collectionName) throws URISyntaxException, IOException {
        final String url =
                getUrl(collectionName, SolrConstants.ADMIN_CORES, SolrConstants.STATUS, SolrConstants.KEY_CORE);
        final CloseableHttpResponse response = retrieveResponse(url);
        final String entityContent = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
        final JsonNode entityNode = new ObjectMapper().readTree(entityContent);
        return !entityNode.get(SolrConstants.STATUS).get(collectionName).isEmpty();
    }
    
    @Override
    public int indexPages(final String collectionName, final List<String> pagePaths,
                          final ResourceResolver resourceResolver) throws IOException, URISyntaxException {
        final List<Map<String, String>> pagePropertiesList = new ArrayList<>();
        pagePaths.forEach(pagePath -> {
            final Resource pageResource = resourceResolver.getResource(pagePath);
            if(null != pageResource) {
                final Page page = pageResource.adaptTo(Page.class);
                if(null != page){
                    pagePropertiesList.add(getPageProperties(page, resourceResolver));
                    Iterator<Page> pageIterator = page.listChildren(null, true);
                    while (pageIterator.hasNext()) {
                        final Page childPage = pageIterator.next();
                        pagePropertiesList.add(getPageProperties(childPage, resourceResolver));
                    }
                }
            }
        });
        final String pagePropertiesJson = new ObjectMapper().writeValueAsString(pagePropertiesList);
        final String url = getUrl(collectionName, SolrConstants.UPDATE, null, null);
        return retrieveResponse(url,
                new StringEntity(pagePropertiesJson, ContentType.APPLICATION_JSON)).getStatusLine().getStatusCode();
    }

    @Override public Resource getOrCreateQueryBoostNode(final ResourceResolver resourceResolver)
            throws PersistenceException {
        final String parentPath =
                config.configuration_path() + SolrConstants.GENERIC_SLASH + config.parent_resource_name();
        return getOrCreateFolderStructure(resourceResolver, parentPath, config.query_boost_resource_name(),
                JcrConstants.NT_UNSTRUCTURED);
    }

    @Override public Resource getOrCreateCollectionNode(final ResourceResolver resourceResolver,
                                                        final String collectionName) throws PersistenceException {
        final String parentPath =
                config.configuration_path() + SolrConstants.GENERIC_SLASH + config.parent_resource_name();
        final Resource collectionFolder = getOrCreateFolderStructure(resourceResolver, parentPath,
                config.collection_folder_resource_name(), JcrResourceConstants.NT_SLING_FOLDER);
        if (null == collectionFolder)
            return null;

        return getOrCreateFolderStructure(resourceResolver, collectionFolder.getPath(), collectionName,
                JcrConstants.NT_UNSTRUCTURED);
    }

    @Override public int deleteIndexedData(final String collectionName) throws URISyntaxException, IOException {
        final String url = getUrl(collectionName, SolrConstants.UPDATE, null, null);
        return retrieveResponse(url,
                new StringEntity(DELETE_PAYLOAD, ContentType.APPLICATION_JSON)).getStatusLine().getStatusCode();
    }

    private Map<String, String> getPageProperties(final Page page, final ResourceResolver resourceResolver) {
        final Map<String, String> pageProperties = new HashMap<>();
        pageProperties.put(SolrConstants.KEY_ID, IxmUtils.getExternalizedLink(page.getPath(),
                resourceResolver) + com.ixm.core.constants.Constants.HTML_EXTENSION);
        pageProperties.put(SolrConstants.KEY_TITLE, page.getTitle());
        try {
            pageProperties.put(SolrConstants.KEY_PAGE_TEXT, getHtmlParsedText(page.getPath(), resourceResolver));
        } catch (final ServletException | IOException e) {
            logger.error("An exception occurred: ", e);
        }
        return pageProperties;
    }
    
    private String getHtmlParsedText(final String pagePath, final ResourceResolver resourceResolver)
            throws ServletException, IOException {
        final HttpServletRequest request = this.requestResponseFactory.
                createRequest(HttpConstants.METHOD_GET, String.format("%s.html", pagePath));
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            final HttpServletResponse response = this.requestResponseFactory.createResponse(outputStream);
            this.requestProcessor.processRequest(request, response, resourceResolver);
            final String html = outputStream.toString();
            return Jsoup.parse(html).text();
        }
    }
    
    private String getUrl(final String collectionName, final String handler, final String action, final String keyCore)
            throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(config.api_url());
        if (StringUtils.isEmpty(action) || StringUtils.isEmpty(keyCore)) {
            uriBuilder.setPath(uriBuilder.getPath().concat(SolrConstants.GENERIC_SLASH).concat(collectionName)
                    .concat(handler));
            uriBuilder.addParameter(SolrConstants.KEY_COMMIT_WITHIN, config.commit_value());
            return uriBuilder.build().toString();
        }
        uriBuilder.addParameter(SolrConstants.KEY_ACTION, action);
        uriBuilder.addParameter(keyCore, collectionName);
        if(action.equals(SolrConstants.CREATE))
            uriBuilder.addParameter(SolrConstants.KEY_CONFIG_SET, config.config_set());
        uriBuilder.setPath(uriBuilder.getPath().concat(handler));
        return uriBuilder.build().toString();
    }

    private String getDeleteUrl(final String collectionName, final Boolean deleteIndex, final Boolean deleteDataDir,
            final Boolean deleteInstanceDir) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(config.api_url());
        uriBuilder.setPath(uriBuilder.getPath().concat(SolrConstants.ADMIN_CORES));
        uriBuilder.addParameter(SolrConstants.KEY_ACTION, SolrConstants.DELETE);
        uriBuilder.addParameter(SolrConstants.KEY_CORE, collectionName);
        uriBuilder.addParameter(SolrConstants.DELETE_INDEX, deleteIndex.toString());
        uriBuilder.addParameter(SolrConstants.DELETE_DATA_DIR, deleteDataDir.toString());
        uriBuilder.addParameter(SolrConstants.DELETE_INSTANCE_DIR, deleteInstanceDir.toString());
        return uriBuilder.build().toString();
    }

    // <solrUrl>/solr/<coreName>/select?q=home&defType=edismax&qf=title^3.0 pageText^1.0
    private String getSearchUrl(final String collectionName, final String handler, final String searchTerm,
                                final String titleValue, final String pageText,final String resultSize) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(config.api_url());
        uriBuilder.setPath(
                uriBuilder.getPath().concat(SolrConstants.GENERIC_SLASH).concat(collectionName).concat(handler));
        final String value = SolrConstants.KEY_TITLE.concat(SolrConstants.GENERIC_CARET).concat(titleValue)
                .concat(" ").concat(SolrConstants.KEY_PAGE_TEXT)
                .concat(SolrConstants.GENERIC_CARET).concat(pageText);
        uriBuilder.addParameter(SolrConstants.KEY_QF, value);
        uriBuilder.addParameter(SolrConstants.KEY_Q, searchTerm);
        uriBuilder.addParameter(SolrConstants.KEY_DEF_TYPE, SolrConstants.EDISMAX);
        uriBuilder.addParameter(SolrConstants.ROWS, resultSize);
        return uriBuilder.build().toString();
    }

    private CloseableHttpResponse retrieveResponse(final String url) throws IOException {
        final HttpGet httpRequest = new HttpGet(url);
        httpRequest.setHeader(SolrConstants.HEADER_ACCEPT, JSONResponse.RESPONSE_CONTENT_TYPE);
        return HttpClients.createDefault().execute(httpRequest);//NOSONAR
    }

    private CloseableHttpResponse retrieveResponse(final String url, final StringEntity entity) throws IOException {
        final HttpPost httpRequest = new HttpPost(url);
        httpRequest.setHeader(SolrConstants.HEADER_ACCEPT, JSONResponse.RESPONSE_CONTENT_TYPE);
        httpRequest.setEntity(entity);
        return HttpClients.createDefault().execute(httpRequest);//NOSONAR
    }

    private Resource getOrCreateFolderStructure(final ResourceResolver resourceResolver, String parentPath,
            String childName, String childPrimaryType) throws PersistenceException {
        if (null == resourceResolver)
            return null;

        Resource parentResource = resourceResolver.getResource(parentPath);
        if (null == parentResource) {
            final Map<String, Object> folderMap = new HashMap<>();
            folderMap.put(JcrConstants.JCR_PRIMARYTYPE, JcrResourceConstants.NT_SLING_FOLDER);
            parentResource = resourceResolver.create(resourceResolver.getResource(config.configuration_path()),
                    parentPath.substring(parentPath.lastIndexOf(SolrConstants.GENERIC_SLASH) + 1), folderMap);
            resourceResolver.commit();
        }

        Resource childResource = resourceResolver.getResource(
                parentResource.getPath() + SolrConstants.GENERIC_SLASH + childName);
        if (null == childResource) {
            final Map<String, Object> childResourceMap = new HashMap<>();
            childResourceMap.put(JcrConstants.JCR_PRIMARYTYPE, childPrimaryType);
            childResource = resourceResolver.create(parentResource, childName, childResourceMap);
            resourceResolver.commit();
        }
        return childResource;
    }

    @Override
    public List<ResponseEntity> getData(final SlingHttpServletRequest request) throws IOException, URISyntaxException {
        final Resource resource = request.getResourceResolver().getResource(
                config.configuration_path() + SolrConstants.GENERIC_SLASH
                        + config.parent_resource_name() + SolrConstants.GENERIC_SLASH + config.query_boost_resource_name());

        final String collectionName = config.collection_name();
        final String searchTerm = request.getParameter(SolrConstants.SEARCH_TERM);
        final String resultSize = request.getParameter(SolrConstants.RESULT_SIZE);

        final String url;
        if(null == resource)
            url = getSearchUrl(collectionName, SolrConstants.SELECT, searchTerm,
                    SolrConstants.DEFAULT_BOOST_VALUE, SolrConstants.DEFAULT_BOOST_VALUE,resultSize);
        else {
            final ValueMap map = resource.getValueMap();
            final String titleValue = map.containsKey(SolrConstants.KEY_TITLE) ?
                    map.get(SolrConstants.KEY_TITLE).toString() : SolrConstants.DEFAULT_BOOST_VALUE;
            final String pageText = map.containsKey(SolrConstants.KEY_PAGE_TEXT) ?
                    map.get(SolrConstants.KEY_PAGE_TEXT).toString() : SolrConstants.DEFAULT_BOOST_VALUE;
            url = getSearchUrl(collectionName, SolrConstants.SELECT, searchTerm, titleValue, pageText,resultSize);
        }

        final String entityContent =
                IOUtils.toString(retrieveResponse(url).getEntity().getContent(), StandardCharsets.UTF_8);
        final JsonNode entityNode = new ObjectMapper().readTree(entityContent);
        return Arrays.asList(new Gson().fromJson(
                String.valueOf(entityNode.get(SolrConstants.RESPONSE).get(SolrConstants.DOCS)), ResponseEntity[].class));
    }

    public String determineCollectionsPath(final ResourceResolver resourceResolver) {
        String path = null;
        try {
            final Resource datasourceResource = resourceResolver.getResource(DATASOURCE_PATH);
            if(null != datasourceResource) {
                path = config.configuration_path() + GENERIC_SLASH + config.parent_resource_name() +
                        GENERIC_SLASH + config.collection_folder_resource_name();
                final Node datasourceNode = datasourceResource.adaptTo(Node.class);
                if(!datasourceNode.hasProperty(PATH))
                    datasourceNode.setProperty(PATH, path);
            }
            resourceResolver.commit();
        } catch (final RepositoryException | PersistenceException e) {
            logger.error("Exception occurred while setting Collections Path property: ", e);
        }
        return path;
    }

}