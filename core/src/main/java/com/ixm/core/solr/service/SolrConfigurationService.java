package com.ixm.core.solr.service;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
@ObjectClassDefinition(name = "IXM Solr Configuration", description = "This configuration reads the solr url")
public @interface SolrConfigurationService {
    @AttributeDefinition(name = "Solr URL",
            description = "Enter the solr url. For eg: http://localhost:8983/solr",
            type = AttributeType.STRING)
    String api_url() default "http://localhost:8983/solr";
    @AttributeDefinition(name = "Solr Config Set",
            description = "Enter solr cofig set.",
            type = AttributeType.STRING)
    String config_set() default "_default";
    @AttributeDefinition(name = "Solr Commit Value",
            description = "Enter no of records to be committed in solr.",
            type = AttributeType.STRING)
    String commit_value() default "1000";
    @AttributeDefinition(name = "Solr Query Boosting Config Path",
            description = "Enter path for storing query boosting values.",
            type = AttributeType.STRING)
    String configuration_path() default "/var";
    @AttributeDefinition(name = "Solr Query Boosting Parent Folder Name",
            description = "Enter parent resource name for query boosting values.",
            type = AttributeType.STRING)
    String parent_resource_name() default "ixm-aem";
    @AttributeDefinition(name = "Solr Query Boosting Node Name",
            description = "Enter node name for query boosting values.",
            type = AttributeType.STRING)
    String query_boost_resource_name() default "queryBoost";
    @AttributeDefinition(name = "Solr Collection Folder Name",
            description = "Enter folder name for storing collections.",
            type = AttributeType.STRING)
    String collection_folder_resource_name() default "collections";
    @AttributeDefinition(name = "Solr Collection Name",
            description = "Enter collection name",
            type = AttributeType.STRING)
    String collection_name() default "ixm-aem";
}