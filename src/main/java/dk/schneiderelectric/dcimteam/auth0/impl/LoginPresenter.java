package dk.schneiderelectric.dcimteam.auth0.impl;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.auth0.NonceUtils;
import com.auth0.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;

public class LoginPresenter extends ConfluenceActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(LoginPresenter.class);
	private static final String CLIENT_ID = "15eNGTm5Y5ZQNzuSDbWVNrpGpdDEWwvn";
	private static final String DOMAIN = "sxwdc-non-prod.auth0.com";
	public static final String NONCE_KEY = "nonce";
	private String state;
	private String os_destination;

	public String execute() {
		String state = UUID.randomUUID().toString();
		HttpServletRequest request = ServletActionContext.getRequest();
		if (request == null) {
			if (log.isDebugEnabled())
				log.debug("Could not get instance of HttpServletRequest");
			return ERROR;
		}
		request.getSession().setAttribute("state", state);
		log.info("Added state:" + state);
		NonceUtils.addNonceToStorage(request);
		this.state = SessionUtils.getState(request);
		return SUCCESS;
	}

	public static String getClientId() {
		return CLIENT_ID;
	}

	public static String getDomain() {
		return DOMAIN;
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
}
