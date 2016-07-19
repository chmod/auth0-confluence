package dk.schneiderelectric.dcimteam.auth0.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.user.actions.LoginAction;

public class LoginLocal extends LoginAction {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(LoginLocal.class);
	@Override
	public String doDefault() throws Exception {
		log.info("doDefault");
		return super.doDefault();
	}
	@Override
	public String execute() throws Exception {
		log.info("execute");
		return super.execute();
	}
}
