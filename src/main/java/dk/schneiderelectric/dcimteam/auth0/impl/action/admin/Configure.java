package dk.schneiderelectric.dcimteam.auth0.impl.action.admin;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.opensymphony.webwork.ServletActionContext;

import dk.schneiderelectric.dcimteam.auth0.api.AuthService;
import dk.schneiderelectric.dcimteam.auth0.api.model.Configuration;
import dk.schneiderelectric.dcimteam.auth0.api.model.Configuration.ConfBuilder;

@Scanned
public class Configure extends ConfluenceActionSupport implements FormAware {
    private final static Log log = LogFactory.getLog(Configure.class);
    private static final long serialVersionUID = 1L;
    private HttpServletRequest req;
    private String callbackUrl;
    private String domain;
    private String clientID;
    private String clientSecret;
    private String redirectError;
    private String redirectSuccess;
    private final AuthService authService;

    @Inject
    public Configure(AuthService authService) {
	this.authService = authService;
    }

    @Override
    public String execute() throws Exception {
	this.req = ServletActionContext.getRequest();
	if (log.isDebugEnabled())
	    log.debug("Getting request:" + req.getMethod());
	if (isEditMode()) {
	    ConfBuilder builder = new ConfBuilder();
	    builder.withCallbackURL(callbackUrl);
	    builder.withClientID(clientID);
	    builder.withClientSecret(clientSecret);
	    builder.withDomain(domain);
	    builder.withRedirectError(redirectError);
	    builder.withRedirectSuccess(redirectSuccess);
	    authService.saveConfig(builder.build());
	    loadConfig();
	} else {
	    loadConfig();
	}
	return SUCCESS;
    }

    private final void loadConfig() {
	Optional<Configuration> configuration = authService.loadConfig();
	if (configuration.isPresent()) {
	    if (log.isDebugEnabled())
		log.debug("Loaded config:" + configuration.get().toString());
	    callbackUrl = configuration.get().getCallbackUrl();
	    domain = configuration.get().getDomain();
	    clientID = configuration.get().getClientID();
	    clientSecret = configuration.get().getClientSecret();
	    redirectError = configuration.get().getRedirectError();
	    redirectSuccess = configuration.get().getRedirectSuccess();
	}
    }

    @Override
    public boolean isEditMode() {
	return "POST".equals(this.req.getMethod());
    }

    public String getCallbackUrl() {
	return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
	this.callbackUrl = callbackUrl;
    }

    public String getDomain() {
	return domain;
    }

    public void setDomain(String domain) {
	this.domain = domain;
    }

    public String getClientID() {
	return clientID;
    }

    public void setClientID(String clientID) {
	this.clientID = clientID;
    }

    public String getClientSecret() {
	return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
	this.clientSecret = clientSecret;
    }

    public String getRedirectError() {
	return redirectError;
    }

    public void setRedirectError(String redirectError) {
	this.redirectError = redirectError;
    }

    public String getRedirectSuccess() {
	return redirectSuccess;
    }

    public void setRedirectSuccess(String redirectSuccess) {
	this.redirectSuccess = redirectSuccess;
    }

}
