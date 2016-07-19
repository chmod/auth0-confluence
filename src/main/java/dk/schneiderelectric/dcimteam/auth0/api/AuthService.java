package dk.schneiderelectric.dcimteam.auth0.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.auth0.Auth0User;
public interface AuthService
{
    void createUser(final String username, final String fullName, final String emailAddress);
    void auth(Auth0User user,HttpServletRequest request,HttpServletResponse response);
}