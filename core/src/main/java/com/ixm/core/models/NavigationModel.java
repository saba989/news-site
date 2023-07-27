package com.ixm.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Lists;
import com.ixm.core.constants.Constants;
import com.ixm.core.utils.IxmUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
/*
 * 
 * Defines the {@code NavigationModel} Sling Model used for the {@code
 * /apps/ixm-aem/components/Navigation} component.
 */
public class NavigationModel {

	/**
	 * Injecting Resource Resolver
	 */
	@SlingObject
	private Resource resource;

	@SlingObject
	private ResourceResolver resourceResolver;

	private List<NavigationPage> navigationPages;

	/**
	 *
	 * Returning navigationPages from NavigationPage
	 */
	public List<NavigationPage> getNavigationPages() {
		return Collections.unmodifiableList(navigationPages);
	}

	@PostConstruct
	protected void init() {
		this.navigationPages = determineNavigationPages();
	}

	/**
	 * The method returns list of Root page and child page
	 * 
	 * @return list of root page and child page
	 */
	private List<NavigationPage> determineNavigationPages() {
		final List<NavigationPage> navigationPages = new ArrayList<>();
		final Resource navigationItems = resource.getChild(Constants.NAVIGATION_ITEMS);
		if (null != navigationItems) {
			Lists.newArrayList(navigationItems.listChildren()).forEach(navigationItem -> {
				final String pagePath = navigationItem.getValueMap().get(Constants.ROOT_PAGE_PATH, StringUtils.EMPTY);
				final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
				final Page rootPage = pageManager.getPage(pagePath);
				if (null != rootPage) {
					final String linkCheckbox = navigationItem.getValueMap().get(Constants.LINK_CHECKBOX,
							StringUtils.EMPTY);
					final List<NavigationPage> childNavigationPages =
							getChildNavigationPages(pageManager, pagePath, linkCheckbox);
					final NavigationPage navigationPage =
							new NavigationPage(childNavigationPages, pagePath, IxmUtils.getPageTitle(rootPage));
					navigationPages.add(navigationPage);
				}
			});
		}
		return navigationPages;
	}

	/**
	 * The method returns child pages to determineNavigationPages method
	 * 
	 * @param pageManager
	 * @param pagePath
	 * @return child pages of a page
	 */
	private List<NavigationPage> getChildNavigationPages(final PageManager pageManager, final String pagePath,
			final String linkCheckbox) {
		final Page rootPage = pageManager.getPage(pagePath);
		if (null != rootPage && !Boolean.valueOf(linkCheckbox)) {
			final List<NavigationPage> childNavigationPages = new ArrayList<>();
			Lists.newArrayList(rootPage.listChildren()).forEach(childPage -> {
				if (!childPage.isHideInNav()) {
					final NavigationPage childNavigationPage = new NavigationPage(Collections.emptyList(),
							childPage.getPath(), IxmUtils.getPageTitle(childPage));
					childNavigationPages.add(childNavigationPage);
				}
			});
			return childNavigationPages;
		}
		return Collections.emptyList();
	}

}