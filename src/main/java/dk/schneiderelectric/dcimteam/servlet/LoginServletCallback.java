package dk.schneiderelectric.dcimteam.servlet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.LoginAction;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.UserManager;
import com.auth0.Auth0Client;
import com.auth0.Auth0ClientImpl;
import com.auth0.Auth0User;
import com.auth0.NonceUtils;
import com.auth0.SessionUtils;
import com.auth0.Tokens;

import dk.schneiderelectric.dcimteam.auth0.api.AuthService;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.Properties;

@Scanned
public class LoginServletCallback extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(LoginServletCallback.class);

	protected Properties properties = new Properties();
	protected String redirectOnFail;
	protected Auth0Client auth0Client;
	public static final String NONCE_KEY = "nonce";
	private final AuthService authService;

	@Inject
	public LoginServletCallback(AuthService authService) {
		this.authService = authService;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		redirectOnFail = readParameter("auth0.redirect_on_error", config);
		for (String param : asList("auth0.client_id", "auth0.client_secret", "auth0.domain")) {
			properties.put(param, readParameter(param, config));
		}
		final String clientId = (String) properties.get("auth0.client_id");
		final String clientSecret = (String) properties.get("auth0.client_secret");
		final String domain = (String) properties.get("auth0.domain");
		this.auth0Client = new Auth0ClientImpl(clientId, clientSecret, domain);
	}

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse res)
			throws IOException, ServletException {
		if (isValidRequest(req)) {
			try {
				final Tokens tokens = fetchTokens(req);
				final Auth0User auth0User = auth0Client.getUserProfile(tokens);
				store(tokens, auth0User, req);
				NonceUtils.removeNonceFromStorage(req);
				onSuccess(req, res);
			} catch (RuntimeException ex) {
				onFailure(req, res, ex);
			}
		} else {
			onFailure(req, res, new IllegalStateException("Invalid state or error"));
		}
	}

	protected void onSuccess(final HttpServletRequest req, final HttpServletResponse res)
			throws ServletException, IOException {
		
		// Auth user here
		Auth0User authUser = SessionUtils.getAuth0User(req);
		if(log.isDebugEnabled()){
			log.debug("Successfully authenticated "+authUser.getEmail());
		}
		authService.auth(authUser, req, res);
		String destination = req.getParameter("os_destination");
		if(log.isDebugEnabled()){
			log.debug("os_destination found, redirecting"+destination);
		}
		if(destination!=null)
			res.sendRedirect(destination);
		else
			res.sendRedirect("/");
	}

	protected void onFailure(final HttpServletRequest req, final HttpServletResponse res, Exception ex)
			throws ServletException, IOException {
		ex.printStackTrace();
		final String redirectOnFailLocation = req.getContextPath() + redirectOnFail;
		res.sendRedirect(redirectOnFailLocation);
	}

	protected void store(final Tokens tokens, final Auth0User user, final HttpServletRequest req) {
		SessionUtils.setTokens(req, tokens);
		SessionUtils.setAuth0User(req, user);
	}

	protected Tokens fetchTokens(final HttpServletRequest req) throws IOException {
		final String authorizationCode = req.getParameter("code");
		final String redirectUri = req.getRequestURL().toString();
		return auth0Client.getTokens(authorizationCode, redirectUri);
	}

	protected boolean isValidRequest(final HttpServletRequest req) throws IOException {
		return !hasError(req) && isValidState(req);
	}

	protected boolean hasError(final HttpServletRequest req) {
		return req.getParameter("error") != null;
	}

	protected boolean isValidState(final HttpServletRequest req) {
		final String stateFromRequest = req.getParameter("state");
		return NonceUtils.matchesNonceInStorage(req, stateFromRequest);
	}

	protected String readParameter(final String parameter, final ServletConfig config) {
		final String initParam = config.getInitParameter(parameter);
		if (StringUtils.isNotEmpty(initParam)) {
			return initParam;
		}
		final String servletContextInitParam = config.getServletContext().getInitParameter(parameter);
		if (StringUtils.isNotEmpty(servletContextInitParam)) {
			return servletContextInitParam;
		}
		throw new IllegalArgumentException(parameter + " needs to be defined");
	}
}