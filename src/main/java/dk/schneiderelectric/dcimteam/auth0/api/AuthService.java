
package dk.schneiderelectric.dcimteam.auth0.api;

import java.util.Optional;
import com.atlassian.confluence.user.ConfluenceUser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.auth0.Auth0User;

import dk.schneiderelectric.dcimteam.auth0.api.model.Configuration;

/**
 * @author chmod
 */
public interface AuthService {
    /**
     * The bandana key where data are saved
     */
    String BANDANA_CTX = "AUTH0-CTX";

    /**
     * Creates user based on data from auth0.
     * 
     * @param username
     *            the username - auth0 implies email=username
     * @param fullName
     *            the fullname of user
     * @param emailAddress
     *            the email address
     */
    Optional<ConfluenceUser> createUser(final String username, final String fullName, final String emailAddress);

    /**
     * Authenticates the user
     * 
     * @param user
     *            the auth0user as taken from loginservlet
     * @param request
     * @param response
     */
    void auth(Auth0User user, HttpServletRequest request, HttpServletResponse response);

    /**
     * Loads an the configuration: domain, secret, client, callbackurl, success
     * and fail url
     * 
     * @return an optional object containing the configuration
     */
    Optional<Configuration> loadConfig();

    /**
     * Saves the object in the bandanamanager
     * 
     * @param config
     */
    void saveConfig(Configuration config);
}
