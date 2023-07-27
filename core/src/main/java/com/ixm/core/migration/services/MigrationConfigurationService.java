package com.ixm.core.migration.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "IXM Migration Configuration", description = "This configuration reads the folder path")
public @interface MigrationConfigurationService {

	@AttributeDefinition(name = "Folder path", description = "Enter the folder path")
	public String getFolderPath();
	
	//excel path - to be added in the configuration
	@AttributeDefinition(name = "Excel path", description = "Enter the excel path")
	public String getExcelPath();
	
	@AttributeDefinition(name = "Page List Column Number", description = "Enter the column number for page list", type = AttributeType.INTEGER)
	int getPageListColNum() default 0;

	@AttributeDefinition(name = "Template List Column Number", description = "Enter the column number for templates", type = AttributeType.INTEGER)
	int getTemplatesColNum() default 0;

	@AttributeDefinition(name = "Process Status Column Number", description = "Enter the column number for process status", type = AttributeType.INTEGER)
	int getProcessStatusColNum() default 1;
	
	@AttributeDefinition(name = "Property List Start Column Number", description = "Enter the properties start column number", type = AttributeType.INTEGER)
	int getPropertiesStartColNum() default 0;
	
	@AttributeDefinition(name = "Property List End Column Number", description = "Enter the properties end column number", type = AttributeType.INTEGER)
	int getPropertiesEndColNum() default 3;

	@AttributeDefinition(name = "Result Folder Path", description = "Enter the path to the folder for storing result excel files.")
	String getResultFolderPath() default "/content/dam/ixm-aem/migrationResult";

	@AttributeDefinition(name = "Success file name (without extension)", description = "Enter the name of the file for storing result when page successfully created.")
	String getSuccessFileName() default "successfile";

	@AttributeDefinition(name = "Error file name (without extension)", description = "Enter the name of the file for storing result when error in creating pages.")
	String getErrorFileName() default "errorfile";
}
