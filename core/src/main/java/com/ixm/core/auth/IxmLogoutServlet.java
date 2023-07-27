package com.ixm.core.auth;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import java.io.IOException;

import static com.ixm.core.auth.IxmAuthenticationHandlerConstants.*;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
	methods = {HttpConstants.METHOD_GET},
	resourceTypes = {"cq/Page"},
	selectors = {"logout"},
	extensions = {"html"}
)
@ServiceDescription("IXM AEM Logout Servlet")
public class IxmLogoutServlet extends SlingSafeMethodsServlet {

	@Reference private transient AuthenticationHandler slingAuthenticator;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {
		if (request.getAuthType().equals(TOKEN) && slingAuthenticator != null) {
			slingAuthenticator.dropCredentials(request, response);
			final String resourcePath = request.getResource().getPath();
			if(StringUtils.isNotEmpty(resourcePath))
				response.sendRedirect(SLING_LOGOUT.concat(resourcePath).concat(EXTENSION_HTML));
		}
	}
}
