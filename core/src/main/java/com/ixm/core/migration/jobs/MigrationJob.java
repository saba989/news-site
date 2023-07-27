package com.ixm.core.migration.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.ixm.core.migration.constants.MigrationConstants;
import com.ixm.core.migration.services.MigrationService;
import com.ixm.core.migration.utils.MigrationBean;
import com.ixm.core.migration.utils.MigrationUtils;
import com.ixm.core.constants.*;


/**
 * This class runs the job to create the pages.
 *
 */
@Component(service = JobConsumer.class, property = { JobConsumer.PROPERTY_TOPICS + "=" + MigrationJob.JOB_TOPIC })
public class MigrationJob implements JobConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MigrationJob.class);
	public static final String JOB_TOPIC = "migrationJobConsumer";

	@Reference
	private MigrationService migrationService;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	public JobResult process(Job job) {
		JobResult result = JobResult.FAILED;
		LOGGER.debug("Inside process method");
		InputStream inputStream = null;
		final Map<String, Object> serviceMap = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, MigrationConstants.SYSTEM_USER_SERVICE_NAME);
		try(ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(serviceMap)) {
			Asset asset = migrationService.getAsset(resourceResolver);
			Resource param = asset.getOriginal();
			inputStream = param.adaptTo(InputStream.class);
			if (null == inputStream) {
				 LOGGER.debug("InputStream is null for file name {}", asset.getName());
				 return result;
			}
			List<MigrationBean> migrationBeans = new ArrayList<>();
				 migrationBeans = migrationService.getList(inputStream);
				 processList(migrationBeans, resourceResolver);
		} catch (LoginException e) {
			LOGGER.error("Login exception while getting the getServiceResourceResolver", e);
		} catch (IOException e) {
			LOGGER.error("Exception occurred while getting the list of rows", e);
		} finally {
			try {
				if(null != inputStream)
					 inputStream.close();
			} catch (IOException e) {
				LOGGER.error("Exception occurred while closing input stream", e);
			}
		}
		LOGGER.debug("End of process method");
		return JobResult.OK;
	}

	/**
	 * This method processes the list of rows
	 * 
	 * @param migrationBeans
	 * @param resourceResolver
	 */
	private void processList(List<MigrationBean> migrationBeans, ResourceResolver resourceResolver) {
		final List<Map<String, String>> pageSuccessRows = new ArrayList<>();
		final List<Map<String, String>> pageErrorRows = new ArrayList<>();
		for (MigrationBean migrationBean : migrationBeans) {
			if (null == migrationBean.getErrorMessage()) {
				if (migrationBean.getParentPath().contains(Constants.SLASH_DELIMITER)) {
					processParentPage(resourceResolver, migrationBean, pageSuccessRows, pageErrorRows);
				} else {
					if (createPage(migrationBean, resourceResolver))
						addSuccessRow(migrationBean.getOriginalCompletePath(),
								migrationService.getFolderPath() + migrationBean.getPageName(),
								MigrationConstants.PAGE_SUCCESS_MESSAGE, pageSuccessRows);
				}
			} else {
				final String name = (null == migrationBean.getCompletePath()) ?
						migrationService.getFolderPath().concat(MigrationConstants.PAGE_ERROR_MESSAGE) :
						migrationBean.getCompletePath();
				addErrorRow(name, migrationBean.getErrorMessage(), pageErrorRows);
			}
		}

		migrationService.writePageSuccess(resourceResolver, pageSuccessRows);
		migrationService.writePageFailure(resourceResolver, pageErrorRows);

	}

	/**
	 * This method creates the page and adds the properties
	 * 
	 * @param migrationBean
	 * @param resourceResolver
	 */
	public boolean createPage(MigrationBean migrationBean, ResourceResolver resourceResolver) {
		Page page = null;
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

		if (null == pageManager.getPage(migrationBean.getCompletePath())) {
			try {
				page = pageManager.create(MigrationUtils.getParentPath(migrationBean.getCompletePath()),
						migrationBean.getPageName(), migrationBean.getTemplateName(), StringUtils.EMPTY);
				if (page.hasContent()) {
					Resource resource = page.getContentResource();
					ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
					Map<String, String> propertyMap = migrationBean.getPropertyList();
					for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
						if (entry.getValue().contains(MigrationConstants.GENERIC_COMMA)) {
							String[] values = entry.getValue().split(MigrationConstants.GENERIC_COMMA);
							map.put(entry.getKey(), values);
						} else {
							map.put(entry.getKey(), entry.getValue());
						}
					}
					resourceResolver.commit();
				}
			} catch (WCMException | PersistenceException e) {
				LOGGER.error("Exception while creating the page",e);
			}
			return true;
		}
		return false;

	}

	/**
	 * This method processes the parent page
	 * 
	 * @param resourceResolver
	 * @param migrationBean
	 * @param pageSuccessRows
	 * @param pageErrorRows
	 */
	private void processParentPage(ResourceResolver resourceResolver, MigrationBean migrationBean,
			List<Map<String, String>> pageSuccessRows, List<Map<String, String>> pageErrorRows) {
		String[] strArr = migrationBean.getParentPath().split(Constants.SLASH_DELIMITER);
		StringBuilder str = new StringBuilder(migrationService.getFolderPath());
		int i;
		boolean flag = false;
		for (i = 0; i < strArr.length - 1; i++) {
			if (i > 0) {
				str.append(strArr[i - 1] + Constants.SLASH_DELIMITER);
			}

			Resource resource = resourceResolver
					.getResource(str.toString() + MigrationUtils.getValidPageName(strArr[i]));
			if (null == resource) {
				flag = true;
				addErrorRow(str.toString() + strArr[i], MigrationConstants.PARENT_ERROR_MESSAGE, pageErrorRows);
				break;
			}
		}
		if (!flag && createPage(migrationBean, resourceResolver)) {
			addSuccessRow(migrationBean.getOriginalCompletePath(), migrationBean.getCompletePath(),
					MigrationConstants.PAGE_SUCCESS_MESSAGE, pageSuccessRows);
		}
	}

	/**
	 * This method adds a success row
	 * 
	 * @param pageName
	 * @param createdPageName
	 * @param message
	 * @param pageSuccessRows
	 */
	private void addSuccessRow(String pageName, String createdPageName, String message,
			List<Map<String, String>> pageSuccessRows) {
		Map<String, String> pageSuccessRow = new HashMap<>();
		pageSuccessRow.put(MigrationConstants.PAGE, pageName);
		pageSuccessRow.put(MigrationConstants.CREATED_PAGE, createdPageName);
		pageSuccessRow.put(MigrationConstants.MESSAGE, message);
		pageSuccessRows.add(pageSuccessRow);
	}

	/**
	 * This method adds a error row
	 * 
	 * @param pageName
	 * @param errorMessage
	 * @param pageErrorRows
	 */
	private void addErrorRow(String pageName, String errorMessage, List<Map<String, String>> pageErrorRows) {
		Map<String, String> pageErrorRow = new HashMap<>();
		pageErrorRow.put(MigrationConstants.PAGE, pageName);
		pageErrorRow.put(MigrationConstants.MESSAGE, errorMessage);
		pageErrorRows.add(pageErrorRow);
	}
}
