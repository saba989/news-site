package com.ixm.core.migration.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ixm.core.constants.Constants;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * A {@code utility} class which contains helper functions for migration.
 *
 */
public class MigrationUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(MigrationUtils.class);

	private MigrationUtils() {
		// prevent outside initialization
	}

	/**
	 * Returns the filtered list of the rows based on {@code status}
	 * 
	 * @param sheet
	 * @param columnNumber
	 * @param columnValue
	 * @return filteredRows List
	 */
	public static List<Row> getFilteredRows(final Sheet sheet, final int columnNumber, final String columnValue) {
		LOGGER.debug("Filtering rows for sheet {}", sheet.getSheetName());
		List<Row> filteredRows = new ArrayList<>();
		Iterator<Row> rowIterator = sheet.rowIterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() == 0) {
				continue;
			}

			Cell c = row.getCell(columnNumber);
			if (null != c && c.getStringCellValue().equalsIgnoreCase(columnValue)) {
				filteredRows.add(row);
			}
		}
		return filteredRows;
	}

	/**
	 * Returns the {@code header row} of the sheet
	 * 
	 * @param sheet
	 * @return header row
	 */
	public static Row getHeaderRow(final Sheet sheet) {
		LOGGER.debug("Header row for sheet {}", sheet.getSheetName());
		Iterator<Row> rowIterator = sheet.rowIterator();
		Row headerRow = null;
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() == 0) {
				headerRow = row;
			}
		}
		return headerRow;
	}

	/**
	 * Returns {@code page name} of the provided path
	 * 
	 * @param path
	 * @return The Page name or {@code empty string}
	 */
	public static String getPageName(String path) {
		if (StringUtils.isBlank(path))
			return StringUtils.EMPTY;
		int index = path.lastIndexOf(Constants.SLASH_DELIMITER);
		if (path.endsWith(Constants.SLASH_DELIMITER)) {
			return StringUtils.EMPTY;
		} else if (-1 != index) {
			return path.substring(index + 1);
		}
		return path;
	}

	/**
	 * Returns {@code parent page path} of the provided path
	 * 
	 * @param path
	 * @return The parent page path or {@code empty string}
	 */
	public static String getParentPath(String path) {
		if (StringUtils.isBlank(path))
			return StringUtils.EMPTY;
		int index = path.lastIndexOf(Constants.SLASH_DELIMITER);
		if (-1 != index) {
			return path.substring(0, index);
		}
		return path;
	}

	/**
	 * Returns valid {@code page name} of the provided name
	 * 
	 * @param name
	 * @return The valid page name or {@code empty string}
	 */
	public static String getValidPageName(String name) {
		if (StringUtils.isBlank(name))
			return StringUtils.EMPTY;
		String regex = "[-._!\"`'#%&,:;<>=@{}~\\$\\(\\)\\*\\+\\\\\\?\\[\\]\\^\\|\\s]+";
		return name.replaceAll(regex, "-").toLowerCase();
	}
}
