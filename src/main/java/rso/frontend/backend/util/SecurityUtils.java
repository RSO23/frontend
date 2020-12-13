package rso.frontend.backend.util;

import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwtParser;

public final class SecurityUtils {

    private static final String signingKey = "veryhardsecret";

    private SecurityUtils() {
        // Util methods only
    }

    public static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null && Stream.of(ServletHelper.RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    public static boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = ((String) authentication.getDetails());

        DefaultJwtParser defaultJwtParser = new DefaultJwtParser();
        // TODO @jakobm save this in application.yaml
        defaultJwtParser.setSigningKey(signingKey);
        DefaultClaims object = ((DefaultClaims) defaultJwtParser.parse(token).getBody());

        return object.get("id", Long.class);
    }

    public static boolean isTokenExpired() {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        DefaultJwtParser defaultJwtParser = new DefaultJwtParser();
        defaultJwtParser.setSigningKey("veryhardsecret");

        try {
            defaultJwtParser.parse(token);
        }
        catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }
}