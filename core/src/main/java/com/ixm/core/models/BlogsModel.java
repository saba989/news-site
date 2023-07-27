package com.ixm.core.models;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

/**
 * Represents the blog
 */

public class BlogsModel {
	@Inject
	private List<BlogsItems> blogItems;
	
	@Inject
	private String title;
	
	@Inject
	private String subtitle;

	/**
     * The method returns the values of the nested multifield inside blog
     * @return the list of blog items
     */
	public List<BlogsItems> getBlogItems() {
		if (null == blogItems) {
			return null;
		}
		return Collections.unmodifiableList(blogItems);
	}
	
	/**
     * The method returns the title of the blog
     * @return the button style
     */
	
	public String getTitle() {
		return title;
	}

	/**
     * The method returns the subtitle of the blog 
     * @return the subTitle
     */
	public String getSubtitle() {
		return subtitle;
	}
	
}
