package com.icfi.aem.componentlock.filter;

import com.icfi.aem.componentlock.aem.ComponentLockResourceResolverWrapper;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Request Filter responsible for injecting the LockAwareComponentManager
 */
@SlingFilter(scope = SlingFilterScope.REQUEST, order = Integer.MAX_VALUE)
public final class ComponentLockRequestFilter implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        ServletRequest out = request;
        if (request instanceof SlingHttpServletRequest) {
            final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
            out = new RequestWrapper(slingRequest);
        }
        chain.doFilter(out, response);
    }

    @Override
    public void destroy() {
    }

    private static class RequestWrapper extends SlingHttpServletRequestWrapper {

        private final ResourceResolver wrapped;

        public RequestWrapper(SlingHttpServletRequest wrappedRequest) {
            super(wrappedRequest);
            wrapped = new ComponentLockResourceResolverWrapper(wrappedRequest.getResourceResolver());
        }

        @Override
        public ResourceResolver getResourceResolver() {
            return wrapped;
        }
    }
}
