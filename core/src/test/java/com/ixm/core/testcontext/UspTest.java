package com.ixm.core.testcontext;

import com.ixm.core.models.UspModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith({AemContextExtension.class,MockitoExtension.class})
class UspTest {

	private final String RESOURCE_PATH = "/content/ixm-aem/us/en";
	private final AemContext aemcontext = new AemContext();
	private UspModel uspComponent;
	Node node = mock(Node.class);
	Resource resource = mock(Resource.class);

	@BeforeEach
	public void setUp() throws Exception {
		aemcontext.addModelsForClasses(UspModel.class);
		aemcontext.load().json("/com/ixm/core/models/usp.json", RESOURCE_PATH);
		uspComponent = aemcontext.currentResource(RESOURCE_PATH).adaptTo(UspModel.class);
	}

	@Test
	void titleTest() {
		String expected = "Title of usp component ";
		String actual = uspComponent.getTitle();
		assertEquals(expected,actual);
	}

	@Test
	void imageTest() throws RepositoryException {
		String expected = "/content/dam/ixm-aem/bg1.jpg";
		String actual = uspComponent.getUsps().get(0).getImage();
		assertEquals(expected,actual);
	}

	@Test
	void altTest() throws RepositoryException {
		String expected = "image one";
		String actual = uspComponent.getUsps().get(0).getAltText();
		assertEquals(expected,actual);
	}

	@Test
	void uspTitleTest() throws RepositoryException {
		String expected = "Background image";
		String actual = uspComponent.getUsps().get(0).getUspTitle();
		assertEquals(expected,actual);

	}

	@Test
	void bulletsTest() throws RepositoryException {
		assertEquals(2,uspComponent.getUsps().size());
	}

	@Test
	void uspText() throws RepositoryException {
		String expected = "this image show sun rise.";
		String actual = uspComponent.getUsps().get(0).getBullets().get(0).getText();
		assertEquals(expected,actual);

	}

}