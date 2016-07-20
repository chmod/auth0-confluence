package dk.schneiderelectric.dcimteam.auth0.impl.action;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.auth0.NonceUtils;
import com.auth0.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;

import dk.schneiderelectric.dcimteam.auth0.api.AuthService;
import dk.schneiderelectric.dcimteam.auth0.api.model.Configuration;

@Scanned
public class LoginPresenter extends ConfluenceActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(LoginPresenter.class);
	public static final String NONCE_KEY = "nonce";
	public static final String AUTH0_NOT_SETUP = "noauth0";
	private Configuration config;
	private String state;
	private String os_destination;
	private String baseURL;
	@ComponentImport
	SettingsManager settingsManager;
	private final AuthService authService;
	@Inject
	public LoginPresenter(SettingsManager settingsManager, AuthService authService) {
		this.settingsManager=settingsManager;
		this.authService=authService;
	}
	public String execute() {
		Optional<Configuration> config=authService.loadConfig();
		if(!config.isPresent()){
			if(log.isDebugEnabled())
				log.debug("Auth0 is not configured");
			return AUTH0_NOT_SETUP;
		}
		this.config=config.get();
		String state = UUID.randomUUID().toString();
		HttpServletRequest request = ServletActionContext.getRequest();
		if (request == null) {
			if (log.isDebugEnabled())
				log.debug("Could not get instance of HttpServletRequest");
			return ERROR;
		}
		request.getSession().setAttribute("state", state);
		NonceUtils.addNonceToStorage(request);
		this.state = SessionUtils.getState(request);
		this.baseURL=settingsManager.getGlobalSettings().getBaseUrl();
		return SUCCESS;
	}

	public String getState() {
		return state;
	}

	public String getOs_destination() {
		return os_destination;
	}

	public void setOs_destination(String os_destination) {
		this.os_destination = os_destination;
	}
	public String getBaseURL() {
		return baseURL;
	}
	public Configuration getConfig() {
		return config;
	}
	public void setConfig(Configuration config) {
		this.config = config;
	}
}
