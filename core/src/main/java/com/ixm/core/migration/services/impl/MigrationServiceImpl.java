package com.ixm.core.migration.services.impl;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

import com.day.cq.dam.api.AssetManager;
import com.day.text.csv.Csv;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.ixm.core.migration.constants.MigrationConstants;
import com.ixm.core.migration.services.MigrationConfigurationService;
import com.ixm.core.migration.services.MigrationService;
import com.ixm.core.migration.utils.MigrationBean;
import com.ixm.core.migration.utils.MigrationUtils;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;

@Component(service = MigrationService.class, property = { Constants.SERVICE_ID + "= IXM Migration Service",
		Constants.SERVICE_DESCRIPTION
				+ "= This service creates the page structure in repository based on inputs from the xlsx file" })
@Designate(ocd = MigrationConfigurationService.class)
public class MigrationServiceImpl implements MigrationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MigrationServiceImpl.class);

	private MigrationConfigurationService config;

	@Activate
	protected void activate(MigrationConfigurationService config) {
		this.config = config;
	}

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Override
	public List<MigrationBean> getList(InputStream inputStream) throws IOException {
		ArrayList<MigrationBean> migrationBeans = new ArrayList<>();
		List<Row> filteredRows = new ArrayList<>();
		try (Workbook workbook = WorkbookFactory.create(inputStream)) {
			Sheet sheet = workbook.getSheetAt(0);
			filteredRows = MigrationUtils.getFilteredRows(sheet, config.getProcessStatusColNum(),
					MigrationConstants.COLUMN_PROCESS_STATUS_VALUE_YES);
			Row headerRow = MigrationUtils.getHeaderRow(sheet);
			if (filteredRows.isEmpty()) {
				LOGGER.debug("There is no row in the master list, which is having the status set to process the row.");
				return migrationBeans;
			} else {
				for (Row row : filteredRows) {
					MigrationBean migrationBean = processRow(row, headerRow);
					migrationBeans.add(migrationBean);
				}
			}
		}
		return migrationBeans;
	}

	/**
	 * This method sets the properties in the migration bean and returns it
	 * 
	 * @param row
	 * @param headerRow
	 * @return the bean
	 */
	private MigrationBean processRow(Row row, Row headerRow) {
		MigrationBean migrationBean = new MigrationBean();
		String errorMessage = StringUtils.EMPTY;
		Cell pageCell = row.getCell(config.getPageListColNum());
		if (null != pageCell && StringUtils.isNotBlank(pageCell.getStringCellValue())) {
			setValuesInBean(row, headerRow, migrationBean, pageCell);
		} else {
			errorMessage = MigrationConstants.PAGE_NAME_NOT_FOUND;
		}
		if (StringUtils.isNotEmpty(errorMessage)) {
			migrationBean.setErrorMessage(errorMessage);
		}
		return migrationBean;
	}

	private void setValuesInBean(Row row, Row headerRow, MigrationBean migrationBean, Cell pageCell) {
		Map<String, String> propertyMap = new HashMap<>();
		String errorMessage = StringUtils.EMPTY;
		String pageCellName = pageCell.getStringCellValue();
		String pageName = MigrationUtils.getPageName(pageCellName);
		if (StringUtils.isNotBlank(pageName)) {
			Cell templateCell = row.getCell(config.getTemplatesColNum());
			if (null != templateCell && StringUtils.isNotBlank(templateCell.getStringCellValue())) {
				String templateName = templateCell.getStringCellValue();
				migrationBean.setPageName(MigrationUtils.getValidPageName(pageName));
				migrationBean.setParentPath(MigrationUtils.getValidPageName(pageCellName));
				migrationBean.setCompletePath(getFolderPath() + migrationBean.getParentPath());
				migrationBean.setOriginalCompletePath(getFolderPath() + pageCellName);
				migrationBean.setTemplateName(templateName);
				for (int i = config.getPropertiesStartColNum(); i <= config.getPropertiesEndColNum(); i++) {
					if (null != row.getCell(i) && !row.getCell(i).getStringCellValue().equals(StringUtils.EMPTY)) {
						propertyMap.put(headerRow.getCell(i).getStringCellValue(), row.getCell(i).getStringCellValue());
					}
				}
				migrationBean.setPropertyList(propertyMap);
			} else {
				errorMessage = MigrationConstants.TEMPLATE_ERROR;
				migrationBean.setCompletePath(getFolderPath() + pageCellName);
			}
		} else {
			errorMessage = MigrationConstants.PAGE_NAME_NOT_FOUND;
			migrationBean.setCompletePath(getFolderPath() + pageCellName);
		}
		if (StringUtils.isNotEmpty(errorMessage)) {
			migrationBean.setErrorMessage(errorMessage);
		}
	}

	@Override
	public Asset getAsset(ResourceResolver resourceResolver) {
		Resource resource = resourceResolver.getResource(this.config.getExcelPath());
		return resource.adaptTo(Asset.class);
	}

	@Override
	public String getFolderPath() {
		return config.getFolderPath();
	}

	@Override
	public void writePageSuccess(final ResourceResolver resourceResolver, final List<Map<String, String>> rows) {
		final String fileName = setFileName(resourceResolver, config.getSuccessFileName());
		String[] header = { MigrationConstants.PAGE, MigrationConstants.CREATED_PAGE, MigrationConstants.MESSAGE };
		createCSV(resourceResolver, fileName, header, rows);
	}

	@Override
	public void writePageFailure(final ResourceResolver resourceResolver, final List<Map<String, String>> rows) {
		final String fileName = setFileName(resourceResolver, config.getErrorFileName());
		String[] header = { MigrationConstants.PAGE, MigrationConstants.MESSAGE };
		createCSV(resourceResolver, fileName, header, rows);
	}

	private String setFileName(final ResourceResolver resourceResolver, final String fileName) {
		final String path = config.getResultFolderPath() + com.ixm.core.constants.Constants.SLASH_DELIMITER + fileName
				+ MigrationConstants.CSV_EXTENSION;
		Resource resource = resourceResolver.getResource(path);
		if (null != resource) {
			String dateTime = new SimpleDateFormat(MigrationConstants.DATE_FORMAT).format(new Date());
			return fileName + MigrationConstants.UNDERSCORE + dateTime + MigrationConstants.CSV_EXTENSION;
		}
		return fileName + MigrationConstants.CSV_EXTENSION;
	}

	private void createCSV(final ResourceResolver resourceResolver, final String fileName, final String[] headers,
			final List<Map<String, String>> rows) {
		final String path = config.getResultFolderPath() + com.ixm.core.constants.Constants.SLASH_DELIMITER + fileName;
		final File file = new File(FilenameUtils.getName(fileName));
		final Csv csv = new Csv();
		try (final FileWriter fileWriter = new FileWriter(file);
				final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
			csv.writeInit(bufferedWriter);
			csv.writeRow(headers);
			for (Map<String, String> row : rows) {
				final String[] currentRow = new String[headers.length];
				for (int i = 0; i < headers.length; i++) {
					currentRow[i] = row.get(headers[i]);
				}
				csv.writeRow(currentRow);
			}
		} catch (IOException e) {
			LOGGER.error("IOException while writing csv.", e);
		} finally {
			try {
				csv.close();
			} catch (IOException e) {
				LOGGER.error("Error while closing csv object.", e);
			}
		}
		saveCSVAsset(resourceResolver, path, file);
	}

	private void saveCSVAsset(final ResourceResolver resourceResolver, final String path, final File file) {
		try (final InputStream inputStream = Files.newInputStream(file.toPath())) {
			final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
			final Session session = resourceResolver.adaptTo(Session.class);
			if (null != session) {
				final ValueFactory valueFactory = session.getValueFactory();
				final Binary binary = valueFactory.createBinary(inputStream);
				if (null != assetManager) {
					assetManager.createOrReplaceAsset(path, binary, MigrationConstants.CSV_CONTENT_TYPE, true);
				}
			}
			Files.delete(file.toPath());
		} catch (IOException e) {
			LOGGER.error("Error while handling csv file.", e);
		} catch (RepositoryException e) {
			LOGGER.error("Error while saving asset.", e);
		}
	}
}
