package com.ixm.core.workflows.impl;

import com.ixm.core.workflows.ApprovalWorkflowConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.*;

/**
 * 
 * This class represents an OSGi configuration which can be found at -
 * /system/console/configMgr - AEM Approval Workflow Configuration
 *
 */

@Component(service=ApprovalWorkflowConfig.class, immediate=true)
@Designate(ocd=ApprovalWorkflowConfigImpl.Config.class)
public class ApprovalWorkflowConfigImpl implements ApprovalWorkflowConfig {

	@ObjectClassDefinition(name="AEM Approval Workflow Configuration")
	public @interface Config {
		@AttributeDefinition(name="Send Notification to: ", description="Notification will be sent to selected item.",
			options={
			@Option(label="Initiated By", value="initiatedBy"),
			@Option(label="Last Modified By", value="lastModifiedBy"),
			/**
			 * This group has been added in page properties
			 */
			@Option(label="Send to authors", value="sendToAuthors")}, type=AttributeType.STRING)
		String notifier() default "modifiedBy";
	}

	private String notifier;

	@Activate
	public void activate(Config config) {
		notifier=config.notifier();
	}

	@Override
	public String getNotifier() {
		return notifier;
	}
}