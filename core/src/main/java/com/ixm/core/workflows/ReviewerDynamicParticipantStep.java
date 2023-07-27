package com.ixm.core.workflows;
import com.adobe.granite.workflow.exec.*;
import com.ixm.core.workflows.Constants.Constants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.jcr.JcrConstants;

@Component(service = ParticipantStepChooser.class, property = { "chooser.label=" + "Dynamic Participant Step For Reviewer" })
public class ReviewerDynamicParticipantStep implements ParticipantStepChooser {

    /**
     * returns reviewer who will receive the notification for approval and rejection.
     */
    @Override
    public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        final WorkflowData workflowData = workItem.getWorkflowData();
        final String payloadPath = workflowData.getPayload().toString();
        final ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
        final Resource resource = resolver.getResource(payloadPath.concat("/").concat(JcrConstants.JCR_CONTENT));
        final ValueMap valueMap= resource.adaptTo(ValueMap.class);
        return valueMap.get(Constants.REVIEWER, String.class);
    }
}