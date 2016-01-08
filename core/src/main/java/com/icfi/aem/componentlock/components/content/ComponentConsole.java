package com.icfi.aem.componentlock.components.content;

import com.icfi.aem.componentlock.manager.ComponentLockManager;
import com.icfi.aem.componentlock.manager.impl.ComponentLockManagerImpl;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.api.security.user.QueryBuilder;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComponentConsole {

    private final List<Authorizable> authorizables = new ArrayList<>();
    private final String tableRequestPath;

    public ComponentConsole(SlingHttpServletRequest request) {
        JackrabbitSession session = (JackrabbitSession) request.getResourceResolver().adaptTo(Session.class);
        try {
            UserManager userManager = session.getUserManager();
            Iterator<Authorizable> authorizableIterator = userManager.findAuthorizables(new Query() {
                @Override
                public <T> void build(QueryBuilder<T> builder) {}
            });
            while (authorizableIterator.hasNext()) {
                authorizables.add(authorizableIterator.next());
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        ComponentLockManager componentLockManager =
            request.getResourceResolver().adaptTo(ComponentLockManagerImpl.class);
        tableRequestPath = componentLockManager.getConfigurationPath();
    }

    public List<Authorizable> getAuthorizables() {
        return authorizables;
    }

    public String getTableRequestPath() {
        return tableRequestPath;
    }
}
