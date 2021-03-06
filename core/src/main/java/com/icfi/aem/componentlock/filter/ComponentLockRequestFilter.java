package com.icfi.aem.componentlock.filter;

import com.icfi.aem.componentlock.sling.ComponentLockResourceResolverWrapper;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
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
import java.util.Map;

/**
 * Request Filter responsible for injecting the LockAwareComponentManager via ComponentLockResourceResolverWrapper.
 * This filter must be placed in front of the WCMComponentFilter.
 */
@SlingFilter(label = "Component Lock: Request Filter", scope = SlingFilterScope.REQUEST, order = 20, metatype = true)
public final class ComponentLockRequestFilter implements Filter {

    @Property(label = "Disable", boolValue = false)
    private static final String DISABLE = "service.disable";
    private boolean disable;

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        ServletRequest req = request;
        if (!disable) {
            if (request instanceof SlingHttpServletRequest) {
                final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
                req = new RequestWrapper(slingRequest);
            }
        }
        chain.doFilter(req, response);
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

    @Activate
    @Modified
    protected void modified(Map<String, Object> props) {
        Object disableProp = props.get(DISABLE);
        if (disableProp instanceof Boolean) {
            disable = (boolean) disableProp;
        }
    }
}
