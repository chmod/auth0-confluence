package dk.schneiderelectric.dcimteam.servlet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.auth0.Auth0Client;
import com.auth0.Auth0ClientImpl;
import com.auth0.Auth0User;
import com.auth0.NonceUtils;
import com.auth0.SessionUtils;
import com.auth0.Tokens;

import dk.schneiderelectric.dcimteam.auth0.api.AuthService;
import dk.schneiderelectric.dcimteam.auth0.api.model.Configuration;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

@Scanned
public class LoginServletCallback extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(LoginServletCallback.class);

    protected String redirectOnFail;
    protected Auth0Client auth0Client;
    public static final String NONCE_KEY = "nonce";
    private final AuthService authService;
    private Configuration config;

    @Inject
    public LoginServletCallback(AuthService authService) {
	this.authService = authService;

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	Optional<Configuration> configuration = authService.loadConfig();
	if (!configuration.isPresent())
	    throw new ServletException("Auth0 configuration is not found");
	this.config = configuration.get();
	redirectOnFail = this.config.getRedirectError();
	final String clientId = this.config.getClientID();
	final String clientSecret = this.config.getClientSecret();
	final String domain = this.config.getDomain();
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
	if (log.isDebugEnabled()) {
	    log.debug("Successfully authenticated " + authUser.getEmail());
	}
	authService.auth(authUser, req, res);
	String destination = req.getParameter("os_destination");
	if (log.isDebugEnabled()) {
	    log.debug("os_destination found, redirecting" + destination);
	}
	if (destination != null)
	    res.sendRedirect(destination);
	else
	    res.sendRedirect(this.config.getRedirectSuccess());
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
}