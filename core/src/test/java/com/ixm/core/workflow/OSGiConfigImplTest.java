package com.ixm.core.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.ixm.core.workflows.impl.ApprovalWorkflowConfigImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class OSGiConfigImplTest {

	AemContext aemContext = new AemContext();
	ApprovalWorkflowConfigImpl configTest;

	@BeforeEach
	void setUp() {
		configTest = aemContext.registerService(new ApprovalWorkflowConfigImpl());
		ApprovalWorkflowConfigImpl.Config config = mock(ApprovalWorkflowConfigImpl.Config.class);
		when(config.notifier()).thenReturn("admin");
		configTest.activate(config);
	}

	@Test
	void getAuthorName() {
		String expected = "admin";
		String actual = configTest.getNotifier();
		assertEquals(expected, actual);
	}

}
