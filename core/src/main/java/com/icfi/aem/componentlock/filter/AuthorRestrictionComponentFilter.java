package com.icfi.aem.componentlock.filter;

import com.day.cq.wcm.api.WCMMode;
import com.icfi.aem.componentlock.manager.ComponentLockManager;
import com.icfi.aem.componentlock.manager.impl.ComponentLockManagerImpl;
import com.icfi.aem.componentlock.model.LockPermission;
import com.icfi.aem.componentlock.util.AuthorizableUtil;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Component filter responsible for blocking author capabilities for restricted components
 * NOTE: The filter order must be before the WCMComponentFilter for this logic to work correctly.
 */
@SlingFilter(scope = SlingFilterScope.COMPONENT, order = 0)
public final class AuthorRestrictionComponentFilter implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        final ResourceResolver resolver = slingRequest.getResourceResolver();
        final ComponentLockManager lockManager = resolver.adaptTo(ComponentLockManagerImpl.class);
        final JackrabbitSession session = (JackrabbitSession) resolver.adaptTo(Session.class);
        try {
            final Authorizable authorizable = session.getUserManager().getAuthorizable(session.getUserID());

            final boolean isSecured = isSecuredResource(lockManager, slingRequest.getResource(), authorizable);
            final WCMMode current = WCMMode.fromRequest(slingRequest);
            if (isSecured) {
                WCMMode.DISABLED.toRequest(slingRequest);
            }

            chain.doFilter(request, response);

            if (isSecured) {
                current.toRequest(slingRequest);
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    private boolean isSecuredResource(final ComponentLockManager lockManager, final Resource resource,
        final Authorizable authorizable) throws RepositoryException {

        LockPermission permission = lockManager.getComponentPermissionsInherited(resource.getResourceType(),
                AuthorizableUtil.getPrincipalIds(authorizable));
        return permission == LockPermission.DENY;
    }

    @Override
    public void destroy() {
    }
}
