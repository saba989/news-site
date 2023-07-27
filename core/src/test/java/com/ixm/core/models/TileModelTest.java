package com.ixm.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class TileModelTest {

	private final String RESOURCE_PATH = "/content/ixm-aem/us/en/page/tile";
	private final String RESOURCE_PATH_ITEM_NOT_FOUND = "/content/ixm-aem/us/en/page/tile-item-not-found";
	private final AemContext aemContext = new AemContext();
	private TileModel tileModel;
	private TileModel tileItemsNotFound;

	@BeforeEach
	void setUp() throws Exception {
		aemContext.addModelsForClasses(TileModel.class);
		aemContext.load().json("/com/ixm/core/models/tile/tile.json", RESOURCE_PATH);
		aemContext.load().json("/com/ixm/core/models/tile/tile-item-not-found.json", RESOURCE_PATH_ITEM_NOT_FOUND);
		tileModel = aemContext.currentResource(RESOURCE_PATH).adaptTo(TileModel.class);
		tileItemsNotFound = aemContext.currentResource(RESOURCE_PATH_ITEM_NOT_FOUND).adaptTo(TileModel.class);
	}

	@Test
	void testGetTitle() {
		assertEquals("this is component title", tileModel.getTitle());
	}

	@Test
	void testGetSubTitle() {
		assertEquals("this is component subTitle", tileModel.getSubTitle());
	}

	@Test
	void testGetQuotes() {
		assertEquals("true", String.valueOf(tileModel.isQuotes()));
	}

	@Test
	void testGetLayout() {
		assertEquals("3x3", tileModel.getLayout());
	}

	@Test
	void testGetTileItems() {
		assertEquals(2, tileModel.getTileItems().size());
		TileItem tileItem1 = tileModel.getTileItems().get(0);
		assertEquals("title of tile1", tileItem1.getTitle());
		assertEquals("description of tile1", tileItem1.getDescription());
	}

	@Test
	void isTileItemsPresent() {
		assertEquals(Collections.emptyList(), tileItemsNotFound.getTileItems());
	}

}
