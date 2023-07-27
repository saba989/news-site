package com.ixm.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

/**
 * Represents Tile item
 */
public class TileItem {

	@Inject
	private String title;

	@Inject
	private String description;

	/**
     * The method returns the title of the Tile
     * @return the title 
     */
	public String getTitle() {
		return title;
	}

	/**
     * The method returns the description of the Tile
     * @return the description 
     */
	public String getDescription() {
		return description;
	}

}
