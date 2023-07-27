package com.ixm.core.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import com.ixm.core.workflows.AuthorDynamicParticipantStep;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.ixm.core.workflows.impl.ApprovalWorkflowConfigImpl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class AuthorDynamicParticipantStepTest {
	private final String RESOURCE_PATH = "/var/workflow/instances/server0/2022-08-26_2/content-approval-workflow_52";
	private final AemContext aemContext = new AemContext();

	@InjectMocks
	AuthorDynamicParticipantStep dynamicParticipant = new AuthorDynamicParticipantStep();

	@Mock
	ResourceResolver resourceResolver;

	@Mock
	WorkItem workItem;

	@Mock
	WorkflowData workflowData;

	@Mock
	WorkflowSession workflowSession;

	@Mock
	Workflow workflow;

	@Mock
	MetaDataMap workItemMetadataMap;

	@Mock
	PageManager pageManager;

	@Mock
	Page page;
	
	@Mock
	Resource resource;
	
	@Mock
	ValueMap valueMap;

	@Mock
	ApprovalWorkflowConfigImpl configTest;

	@BeforeEach
	void setUp() throws Exception {
		aemContext.load().json("/com/ixm/core/workflow/contentApprovalWorkflow.json", RESOURCE_PATH);
		lenient().when(workItem.getWorkflow()).thenReturn(workflow);
		lenient().when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resourceResolver);
		lenient().when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
		lenient().when(workItem.getWorkflowData()).thenReturn(workflowData);
		lenient().when(workflowData.getPayload()).thenReturn(RESOURCE_PATH + "jcr:content/dynamic_participant");
		lenient().when(pageManager.getPage(RESOURCE_PATH + "jcr:content/dynamic_participant")).thenReturn(page);
		lenient().when(resourceResolver.getResource(RESOURCE_PATH + "jcr:content/dynamic_participant"+ "/jcr:content")).thenReturn(resource);
		lenient().when(resource.adaptTo(ValueMap.class)).thenReturn(valueMap);
		lenient().when(page.getLastModifiedBy()).thenReturn("admin");
		lenient().when(workflow.getInitiator()).thenReturn("admin");
		ApprovalWorkflowConfigImpl.Config config = mock(ApprovalWorkflowConfigImpl.Config.class);
		lenient().when(config.notifier()).thenReturn("admin");
		configTest.activate(config);
	}

	@Test
	void getParticipant() throws WorkflowException {
		String expected = "admin";
		String actual = dynamicParticipant.getParticipant(workItem, workflowSession, workItemMetadataMap);
		assertEquals(expected, actual);
	}
}
