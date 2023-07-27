package com.ixm.core.testcontext;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.day.cq.commons.Externalizer;
import com.ixm.core.models.LinkItem;
import com.ixm.core.models.TabItem;
import com.ixm.core.models.TabLinksModel;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class,MockitoExtension.class})
class TabLinksModelTest {

    private final String RESOURCE_PATH = "/content/ixm-aem/us/test";
    private final AemContext aemcontext = new AemContext();
    private TabLinksModel tablinkmodel;
    private TabItem tabitem;
    private LinkItem linkitem;


    @BeforeEach
    public void setUp() throws Exception {
        aemcontext.addModelsForClasses(TabLinksModel.class);
        aemcontext.load().json("/com/ixm/core/models/tabLink.json", RESOURCE_PATH);
        tablinkmodel = aemcontext.currentResource(RESOURCE_PATH).adaptTo(TabLinksModel.class);

    }

    @Test
    void getTabs() {
        tablinkmodel = aemcontext.currentResource(RESOURCE_PATH).adaptTo(TabLinksModel.class);
        System.out.println(tablinkmodel.getTabs());
        assertEquals(3,tablinkmodel.getTabs().size());
    }

    @Test
    void getTitle() {
        String expected = "Tab 1";
        String actual = tablinkmodel.getTabs().get(0).getTitle();
        assertEquals(expected,actual);
    }
   @Test
 void getLinks() {
      tabitem = aemcontext.currentResource(RESOURCE_PATH).adaptTo(TabItem.class);
      assertEquals(1,tablinkmodel.getTabs().get(1).getLinks().size());
      
  }

@Test
  void getLinkText() {
	  String expected = "Link1";
      String actual = tablinkmodel.getTabs().get(1).getLinks().get(0).getLinkText();
      assertEquals(expected,actual);
  } 
 @Test
 void getLinkUrl() {
     final String expected = "/content/ixm-aem/us/en.html";
     Externalizer externalizer = mock(Externalizer.class);
     aemcontext.registerAdapter(ResourceResolver.class, Externalizer.class, externalizer);
     when(externalizer.externalLink(any(), any(), any())).thenReturn("/content/ixm-aem/us/en");
     final String actual = tablinkmodel.getTabs().get(1).getLinks().get(0).getLinkUrl();
     Assertions.assertEquals(expected,actual);
 }
 @Test
  void getLinkTarget() {
	  String expected = "_blank";
	  String actual = tablinkmodel.getTabs().get(1).getLinks().get(0).getLinkTarget();
	  assertEquals(expected,actual);
  }
}

