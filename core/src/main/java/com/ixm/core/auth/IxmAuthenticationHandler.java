package com.ixm.core.auth;

import com.day.crx.security.token.TokenCookie;
import com.day.crx.security.token.TokenUtil;//NOSONAR
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.auth.core.spi.AuthenticationFeedbackHandler;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static com.ixm.core.auth.IxmAuthenticationHandlerConstants.*;

@Component(service = AuthenticationHandler.class, immediate = true,
		property = {AuthenticationHandler.PATH_PROPERTY + "=/"})
@ServiceDescription("IXM Authentication Handler")
@ServiceRanking(90000)
public class IxmAuthenticationHandler implements AuthenticationHandler, AuthenticationFeedbackHandler {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	@Reference private SlingRepository slingRepository;

	@Override
	public AuthenticationInfo extractCredentials(final HttpServletRequest request, final HttpServletResponse response) {
		AuthenticationInfo authenticationInfo = null;
		if (POST.equals(request.getMethod()) && request.getRequestURI().endsWith(J_IXM_AEM_SECURITY_CHECK)
				&& StringUtils.isNotEmpty(request.getParameter(USERNAME))) {
			if (!AuthUtil.isValidateRequest(request))
				AuthUtil.setLoginResourceAttribute(request, request.getContextPath());

			final SimpleCredentials creds = new SimpleCredentials(request.getParameter(USERNAME),
					request.getParameter(PSWD).toCharArray());

			authenticationInfo = new AuthenticationInfo(HttpServletRequest.FORM_AUTH, creds.getUserID());
			authenticationInfo.put(JcrResourceConstants.AUTHENTICATION_INFO_CREDENTIALS, creds);
			authenticationInfo.put(ResourceResolverFactory.USER, request.getParameter(USERNAME));
		}
		return authenticationInfo;
	}

	@Override
	public boolean authenticationSucceeded(
			final HttpServletRequest request, final HttpServletResponse response, final AuthenticationInfo authInfo) {
		if (null == authInfo)
			return false;
		return handleRedirect(request, response, authInfo);
	}

	private boolean handleRedirect(final HttpServletRequest request, final HttpServletResponse response,
								   final AuthenticationInfo authInfo) {
		final String targetPath = AuthUtil.getAttributeOrParameter(request, SUCCESS_PAGE, StringUtils.EMPTY);

		if (StringUtils.isEmpty(targetPath))
			return false;

		try {
			TokenUtil.createCredentials(request, response, slingRepository, (String) authInfo.get(ResourceResolverFactory.USER), false); //NOSONAR
			response.sendRedirect(targetPath + EXTENSION_HTML);
		} catch (final RepositoryException e) {
			logger.error("Unable to get login!!", e);
		} catch (final IOException e) {
			logger.error("handleRedirect: Failed to send redirect to " + targetPath
							+ ", aborting request without redirect", e);
		}
		return true;
	}

	@Override
	public void authenticationFailed(
			final HttpServletRequest request, final HttpServletResponse response, final AuthenticationInfo authInfo) {
		try {
			final String targetPath = AuthUtil.getAttributeOrParameter(request, FAILURE_PAGE, StringUtils.EMPTY);
			if (StringUtils.isNotBlank(targetPath))
				response.sendRedirect(targetPath + ".html?message=loginFail");
		} catch (final IOException e) {
			logger.error("Exception from authenticationFailed(): ", e);
		}
	}

	@Override
	public boolean requestCredentials(HttpServletRequest request, HttpServletResponse response) {
		return false;
	}

	@Override
	public void dropCredentials(final HttpServletRequest request, final HttpServletResponse response) {
		Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(LOGIN_TOKEN))
			.forEach(cookie -> {
				cookie.setValue(StringUtils.EMPTY);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			});
		TokenCookie.update(request, response, StringUtils.EMPTY, null, null, true);
	}

}