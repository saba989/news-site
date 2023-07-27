package com.ixm.core.models;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
/**
 * Represents the Tile
 */
public class TileModel {

	/**
	 * Injecting all the fields used in cq dialog of Tile component here !
	 */

	@Inject
	private String title;

	@Inject
	private String subTitle;

	@Inject
	private String quotes;

	@Inject
	private String layout;

	@Inject
	private List<TileItem> tileItems;

	/**
	 * The method returns the title of Component
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * The method returns the subTitle of Component
	 * 
	 * @return the subTitle
	 */
	public String getSubTitle() {
		return subTitle;
	}

	/**
	 * The method returns the quotes value whether or not the Quotes should be there
	 * on Component top-left and bottom-right.
	 * 
	 * @return the quotes
	 */
	public Boolean isQuotes() {
		return Boolean.valueOf(quotes);
	}

	/**
	 * The method returns the layout for Component
	 * 
	 * @return the layout
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * The method returns the values of the multifield inside Tile
	 * 
	 * @return the list of Tile items
	 */
	public List<TileItem> getTileItems() {
		if (null == tileItems) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(tileItems);
	}

}
