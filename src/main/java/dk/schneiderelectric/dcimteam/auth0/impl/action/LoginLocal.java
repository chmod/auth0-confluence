package dk.schneiderelectric.dcimteam.auth0.impl.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.user.actions.LoginAction;

public class LoginLocal extends LoginAction {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(LoginLocal.class);

    @Override
    public String doDefault() throws Exception {
	String result = super.doDefault();
	if(log.isDebugEnabled())
	log.debug("Result from doDefault:" + result);
	return result;
    }

    @Override
    public void validate() {
	if(log.isDebugEnabled())
	    log.debug("Invoking super validate");
	super.validate();
    }

    @Override
    public String execute() throws Exception {
	String result = super.execute();
	if(log.isDebugEnabled())
	    log.debug("Result from execute:" + result);
	return result;
    }
}
