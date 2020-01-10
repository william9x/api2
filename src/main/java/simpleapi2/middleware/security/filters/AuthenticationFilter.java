package simpleapi2.middleware.security.filters;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import simpleapi2.io.response.ErrorResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        final String API_KEY = "simple_api_key_for_authentication";
        final String authenticationHeader = req.getHeader("Authentication");


        if (null == authenticationHeader || false == authenticationHeader.equals(API_KEY)) {
            res.sendError(HttpStatus.UNAUTHORIZED.value(), ErrorResponse.AUTHENTICATION_FAILED.getErrorMessage());
            return;
        }


        chain.doFilter(request, response);
    }
}
