package com.ixm.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.commons.Externalizer;
import com.ixm.core.constants.Constants;
import com.ixm.core.utils.IxmUtils;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class TitleTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);
    private final String DEST_PATH="/content/ixm-aem/us/en";
    private Title title;

    @BeforeEach
    void setUp() {
        aemContext.addModelsForClasses(Title.class);
        aemContext.load().json("/com/ixm/core/models/title.json", DEST_PATH);
        Resource titleResource = aemContext.resourceResolver().getResource(DEST_PATH + "/title");
        title = aemContext.getService(ModelFactory.class).createModel(titleResource, Title.class);
    }
    
    @Test
    void getTitle() {
        String expected = "helllo";
        String actual = title.getTitle();
        assertEquals(expected,actual);
    }

    @Test
    void getType() {
    	String expected = "h5";
        String actual = title.getType();
        assertEquals(expected,actual);
    }
       
    @Test
    void getLinkUrl() {
		String expected = "/content/ixm-aem/us/en/second.html";
		Externalizer externalizer = mock(Externalizer.class);
        aemContext.registerAdapter(ResourceResolver.class, Externalizer.class, externalizer);
        when(externalizer.externalLink(any(), any(), any())).thenReturn("/content/ixm-aem/us/en/second");
        
        String actual = title.getLinkUrl();
        assertEquals(expected,actual);
    }

    @Test
    void getLinkTarget() {
    	String expected = "_blank";
        String actual = title.getLinkTarget();
        assertEquals(expected,actual);
    }

    @Test
    void checkExternalLink() {
        Resource titleResource = aemContext.resourceResolver().getResource(DEST_PATH + "/titleTwo");
        title = aemContext.getService(ModelFactory.class).createModel(titleResource, Title.class);
        String expected = "www.google.com";
        String actual = title.getLinkUrl();
        assertEquals(expected,actual);
    }
	
}
