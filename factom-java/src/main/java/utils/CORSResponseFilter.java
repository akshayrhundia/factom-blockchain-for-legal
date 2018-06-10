package utils;

import org.jboss.resteasy.spi.CorsHeaders;

import javax.ws.rs.container.*;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class CORSResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private final String allowedMethods = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    private final String allowedHeaders = "origin, content-type, accept, authorization";
    private final Boolean allowCredentials = true;

    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            // don't do anything if its an OPTIONS request from swaggger.
            return;
        }
        // Add CORS headers to all responses
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.add(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, allowedHeaders);
        headers.add(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMethods);
        headers.add(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, allowCredentials);
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Security-Policy", "default-src 'self'");
        headers.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        headers.add("Expires", "0");
    }

    public void filter(ContainerRequestContext requestContext) throws IOException {
        String origin = requestContext.getHeaderString(CorsHeaders.ORIGIN);
        if (origin == null) {
            return;
        }
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            preflight(origin, requestContext);
        }
    }

    private void preflight(String origin, ContainerRequestContext requestContext) {
        Response.ResponseBuilder builder = Response.ok();
        builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, allowCredentials);
        String requestMethods = requestContext.getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_METHOD);
        if (requestMethods == null) {
            requestMethods = this.allowedMethods;
        }
        builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethods);
        String requestHeaders = requestContext.getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        if (requestHeaders == null) {
            requestHeaders = this.allowedHeaders;
        }
        builder.header(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders);
        builder.header(CorsHeaders.ACCESS_CONTROL_MAX_AGE, "600");
        requestContext.abortWith(builder.build());
    }
}