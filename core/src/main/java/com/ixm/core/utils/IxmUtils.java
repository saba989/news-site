package com.ixm.core.utils;

import com.day.cq.commons.Externalizer;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.ixm.core.constants.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IxmUtils {

	private static Logger LOG = LoggerFactory.getLogger(IxmUtils.class);

    /**
     * The method generates an external link
     *
     * @param url
     * @param resourceResolver
     * @return
     */
    public static String getExternalizedLink(final String url, final ResourceResolver resourceResolver) {
        final Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
        if (externalizer == null || Externalizer.LOCAL.isEmpty())
            return null;
        return externalizer.externalLink(resourceResolver, Externalizer.LOCAL, url);
    }

    /**
     * Gets the string property.
	 * @param eventProperties the event properties
	 * @param propertyName the property name
	 * @return the string property
     */
    public static String getStringProperties(final ValueMap eventProperties, final String propertyName) {
        return eventProperties.containsKey(propertyName) ? eventProperties.get(propertyName, String.class)
                : StringUtils.EMPTY;
    }

    /**
     * This method can be used to tell if a url is an external object.
     *
     * @param url The URL to be tested
     * @return A Boolean that will be true if the resource is external. and False if
     *         the resource is internal.
     */
    public static boolean isExternalURL(final String url) {
        if (StringUtils.isBlank(url))
            return false;
        return url.matches(
                "^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?(\\/[a-z0-9])*(\\/?|(\\?[a-z0-9]=[a-z0-9](&[a-z0-9]=[a-z0-9]*)?))$");
    }

    /**
     * Returns the Image name for the fallback of empty alt text.
     *
     * @param resourceResolver The Resourceresolver object
     * @param imageURL         path of image drop in dialog
     * @return String the name of image. If image path is not empty
     */
    public static String getImageName(final ResourceResolver resourceResolver, final String imageURL) {
        if (StringUtils.isNotEmpty(imageURL)) {
            final Resource logoResource = resourceResolver.getResource(imageURL);
            if (null != logoResource && logoResource.getName().indexOf(Constants.GENERIC_DOT) > 0) {
                return StringUtils.substringBeforeLast(logoResource.getName(), Constants.GENERIC_DOT);
            }
        }
        return imageURL;
    }
    /**
	 * The method returns navigation title, page title and jcr title of page.
	 *
	 * @param page
	 * @return navigation title, page title and jcr title of page.
	 */
    public static String getPageTitle(final Page page) {
		final String navigationTitle = page.getNavigationTitle();
		if (StringUtils.isNotEmpty(navigationTitle))
			return navigationTitle;

		final String pageTitle = page.getPageTitle();
		if (StringUtils.isNotEmpty(pageTitle))
			return pageTitle;

		 return page.getTitle();
	}

    /**
     * Returns the Alt Text when image resource found.
     * @param resourceResolver
     * @param imageUrl
     * @return String, The value of property altText. If image is found.s
     */
    public static String getAltText(final ResourceResolver resourceResolver, final String imageUrl) {
        final Resource imageRes = resourceResolver.getResource(imageUrl);
        if (null!= imageRes) {
          ValueMap vMap = imageRes.getValueMap();
          return vMap.get("altText", String.class);
        }
		return null;
    }

	/**
	 * Returns the list of filtered pages.
	 *
	 * @param rootPagePath     The root page path
	 * @param tags             String of tags
	 * @param resourceResolver The Resourceresolver object
	 * @return List<String> list of filtered page paths
	 */
	public static List<String> getFilteredPagePaths(final String rootPagePath, final String tags,
			final ResourceResolver resourceResolver) {
		final QueryBuilder builder = resourceResolver.adaptTo(QueryBuilder.class);
		List<String> filteredPages = new ArrayList<>();
		final Session session = resourceResolver.adaptTo(Session.class);
		Map<String, String> predicate = new HashMap<>();
		predicate.put("path", rootPagePath);
		predicate.put("type", "cq:Page");	//NOSONAR
		predicate.put("property", "jcr:content/@cq:tags");
		predicate.put("property.or", "true");
		int i = 1;
		for (String tag : tags.split(Constants.GENERIC_COMMA, 0)) {
			predicate.put("property." + i + "_value", tag);
			i++;
		}
		final Query query = builder.createQuery(PredicateGroup.create(predicate), session);
		final SearchResult searchResult = query.getResult();
		searchResult.getHits().forEach(hit -> {
			try {
				String path = hit.getPath();
				filteredPages.add(path);
			} catch (final RepositoryException e) {
				LOG.error("Error, cannot find page path", e);
			}
		});
		return filteredPages;
	}

	/**
	 * Returns the list of pages with details in form of CardBean object.
	 *
	 * @param pagePaths          List of Page paths
	 * @param resourceResolver   The Resourceresolver object
	 * @param imagePathReference image path reference i.e path from where we have to
	 *                           get image
	 * @return List<CardBean> list of CardBean
	 */
	public static List<CardBean> getFilteredCards(final List<String> pagePaths, final ResourceResolver resourceResolver,
			final String imagePathReference) {
		final List<CardBean> cards = new ArrayList<>();
		pagePaths.forEach(pagePath -> {
			final CardBean cardBean = new CardBean();
			cardBean.setPageTitle(getPageTitle(PageUtils.getPage(pagePath, resourceResolver)));
			if (isExternalURL(pagePath)) {
				cardBean.setPageUrl(pagePath);
			} else {
				final String url = getExternalizedLink(pagePath, resourceResolver);
				int slashCount = 0, index = 0;
				while (slashCount != 3) {
					if (url.charAt(index) == '/') {
						slashCount++;
					}
					index++;
				}
				final String pageUrl = url.substring(0, index - 1) + pagePath;
				cardBean.setPageUrl(pageUrl + Constants.HTML_EXTENSION);
			}

			final Map<String, String> imageMap = getImageAndAltTextPath(imagePathReference, pagePath, resourceResolver);
			cardBean.setImagePath(imageMap.get(Constants.KEY_IMAGE_PATH));
			cardBean.setImageAltText(imageMap.get(Constants.KEY_IMAGE_ALT_TEXT));
			cardBean.setTags(getTagList(pagePath, resourceResolver));
			cards.add(cardBean);
		});
		return cards;
	}

	/**
	 * Returns the list of Tags of Page.
	 *
	 * @param pagePath         path of page
	 * @param resourceResolver The Resourceresolver object
	 * @return List<String> list of tags
	 */
	public static List<String> getTagList(final String pagePath, final ResourceResolver resourceResolver) {
		final Page page = PageUtils.getPage(pagePath, resourceResolver);
		final List<String> tags = new ArrayList<>();
		for (Tag tag1 : page.getTags()) {
			String tg = tag1.toString();
			tags.add(tg.substring(tg.lastIndexOf(Constants.IXM) + 4));
		}
		return tags;
	}

	/**
	 * Returns the list of Tags from String consist of tags by splitting on basis of
	 * ','.
	 *
	 * @param tags String of tags
	 * @return List<String> list of tags
	 */
	public static List<String> getInputTagList(final String tags) {
		final List<String> tagList = new ArrayList<>();
		for (final String tag : tags.split(Constants.GENERIC_COMMA, 0))
			tagList.add(tag.substring(tag.lastIndexOf(Constants.IXM) + 4));
		return tagList;
	}

	/**
	 * Returns the Map of tag with their respective count in result cards.
	 *
	 * @param cards list of cards
	 * @param tagList list of tags given by the user
	 * @return Map<String, Integer> map of tags with their respective count in
	 *         resultant cards.
	 */
	public static Map<String, Integer> getTagCount(final List<CardBean> cards, final List<String> tagList) {
		final Map<String, Integer> tagCount = new HashMap<>();
		cards.forEach(card -> {
			card.getTags().forEach(tag -> {
				if (tagList.contains(tag)) {
					if (null == tagCount.get(tag)) {
						tagCount.put(tag, 1);
					} else {
						int count = tagCount.get(tag);
						tagCount.put(tag, count + 1);
					}
				}
			});
		});
		return tagCount;
	}

	/**
	 * Returns the imagePath,imageAltText in form of map.
	 *
	 * @param imagePathReference path from where we have to get image eg: /jcr:content/root/container/heroimage.
	 * @param pagePath path of page from where to get get image
	 * @param resourceResolver The Resourceresolver object
	 * @return Map<String, String> The path of desktopImageReference if found else
	 *         desktopImageName and the altText if found else desktopImageReference.
	 */
	public static Map<String, String> getImageAndAltTextPath(final String imagePathReference, final String pagePath,
			final ResourceResolver resourceResolver) {
		final String componentPath = pagePath.concat(imagePathReference);
		final Map<String, String> imageMap = new HashMap<>();
		imageMap.put(Constants.KEY_IMAGE_PATH,StringUtils.EMPTY);
		imageMap.put(Constants.KEY_IMAGE_ALT_TEXT,StringUtils.EMPTY);
		final Resource resource = resourceResolver.getResource(componentPath);
		if (null == resource)
			return imageMap;

		final ValueMap valueMap = resource.getValueMap();
		String imagePath;
		if (StringUtils.isNotEmpty(valueMap.get(Constants.PN_DESKTOP_IMAGE_REFERENCE, String.class))) {
			imagePath = valueMap.get(Constants.PN_DESKTOP_IMAGE_REFERENCE, String.class);
		} else {
			imagePath = valueMap.get(Constants.PN_DESKTOP_IMAGE_NAME, String.class);
		}
		String imageAltText = StringUtils.EMPTY;
		if (StringUtils.isNotEmpty(valueMap.get(Constants.PN_ALT_TEXT, String.class))) {
			imageAltText = valueMap.get(Constants.PN_ALT_TEXT, String.class);
		} else if (imagePath.startsWith(Constants.SLASH_DELIMITER)) {
			imageAltText = getImageName(resourceResolver, valueMap.get(Constants.PN_DESKTOP_IMAGE_REFERENCE, String.class));
		}
		imageMap.put(Constants.KEY_IMAGE_PATH, imagePath);
		imageMap.put(Constants.KEY_IMAGE_ALT_TEXT, imageAltText);
		return imageMap;
	}

	public static ResourceResolver getResourceResolver
			(final ResourceResolverFactory resourceResolverFactory, final String systemUser) throws LoginException {
		final Map<String, Object> param = new HashMap<>();
		param.put(ResourceResolverFactory.SUBSERVICE, systemUser);
		return resourceResolverFactory.getServiceResourceResolver(param);
	}

}
