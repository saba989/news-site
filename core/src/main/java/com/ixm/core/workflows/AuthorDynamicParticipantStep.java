package com.ixm.core.workflows;

import java.util.List;
import com.adobe.granite.workflow.exec.*;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.ixm.core.workflows.Constants.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(service = ParticipantStepChooser.class, property = {"chooser.label=" + "Dynamic Participant Step for Author"})
public class AuthorDynamicParticipantStep implements ParticipantStepChooser {

	@Reference
	private transient ApprovalWorkflowConfig config;

	/**
	 * returns Author who will receive the rejection notification.
	 */
	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
			throws WorkflowException {
		final WorkflowData workflowData = workItem.getWorkflowData();
		final String payloadPath = workflowData.getPayload().toString();
		final ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
		final PageManager pageManager = resolver.adaptTo(PageManager.class);
		final Page rootPage = pageManager.getPage(payloadPath);
		final Resource resource = resolver.getResource(payloadPath.concat("/").concat(JcrConstants.JCR_CONTENT));
		final ValueMap valueMap = resource.adaptTo(ValueMap.class);
		final Workflow workflow = workItem.getWorkflow();
		final List<HistoryItem> workflowHistory = workflowSession.getHistory(workflow);
		if (StringUtils.equals(config.getNotifier(), Constants.INITIATED_BY) && !workflowHistory.isEmpty())
			return workflow.getInitiator();
		else if (StringUtils.equals(config.getNotifier(),Constants.SEND_TO_AUTHORS) && !workflowHistory.isEmpty())
			return valueMap.get(Constants.AUTHOR, String.class);
		return rootPage.getLastModifiedBy();
	}
}