package com.ixm.core.solr.constants;

public class SolrConstants {
	public static final String ROWS = "rows";

	private SolrConstants() {}

	public static final String COLLECTION_NAME = "collectionName";
	public static final String CREATE = "create";
	public static final String DATASOURCE = "datasource";
	public static final String DATASOURCE_PATH
			= "/apps/ixm-aem/content/managecollection/configurations/manage-collection/content/views/list/datasource";//NOSONAR
	public static final String DELETE = "UNLOAD";
	public static final String DELETE_DATA_DIR = "deleteDataDir";
	public static final String DELETE_INDEX = "deleteIndex";
	public static final String DELETE_INSTANCE_DIR = "deleteInstanceDir";
	public static final String DELETE_PAYLOAD = "{ \"delete\": {\"query\":\"*:*\"} }";
	public static final String FAILED = "FAILED";
	public static final String GENERIC_SLASH = "/";
	public static final String GENERIC_CARET = "^";
	public static final String HEADER_ACCEPT = "accept";
	public static final String INDEX = "index";
	public static final String ITEM_RESOURCE_TYPE = "itemResourceType";
	public static final String KEY_ACTION = "action";
	public static final String KEY_CONFIG_SET = "configSet";
	public static final String KEY_COMMIT_WITHIN = "commitWithin";
	public static final String KEY_CORE = "core";
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_PAGE_TEXT = "pageText";
	public static final String KEY_TITLE = "title";
	public static final String NAME = "name";
	public static final String OK = "OK";
	public static final String PARAM_CORE_NAME = "collectionName";
	public static final String PARAM_INDEX_CHECKBOX = "index";
	public static final String PARAM_PATHS = "paths";
	public static final String PATH = "path";
	public static final String PATHS = "paths";
	public static final String PROP_PARAM_MAP = "parameterMap";
	public static final String SEARCH_TERM = "searchTerm";
	public static final String STATUS = "status";
	public static final String KEY_Q = "q";
	public static final String KEY_DEF_TYPE = "defType";
	public static final String KEY_QF = "qf";
	public static final String RESPONSE = "response";
	public static final String DOCS = "docs";
	public static final String DEFAULT_BOOST_VALUE = "1.0";
	public static final String ADMIN_CORES = "/admin/cores";
    public static final String UPDATE = "/update";
    public static final String EDISMAX = "edismax";
    public static final String SELECT = "/select";
	public static final String SYSTEM_USER_SERVICE_NAME = "solrSubService";
	public static final String VALUE_ZERO = "0";
	public static final String[] KEYS = {"id", "pageText", "title"};//NOSONAR
	public static final String RESULT_SIZE = "resultsSize";
}
