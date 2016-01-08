package com.icfi.aem.componentlock.aem;

import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import com.icfi.aem.componentlock.manager.ComponentLockManager;
import com.icfi.aem.componentlock.manager.impl.ComponentLockManagerImpl;
import com.icfi.aem.componentlock.model.LockPermission;
import com.icfi.aem.componentlock.util.AuthorizableUtil;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class LockAwareComponentManager implements ComponentManager {

    private final ComponentManager wrapped;
    private final ComponentLockManager lockManager;
    private final List<String> principalIds;

    public LockAwareComponentManager(ResourceResolver resolver) {
        this.wrapped = resolver.adaptTo(ComponentManager.class);
        this.lockManager = new ComponentLockManagerImpl(resolver);
        JackrabbitSession jSession = (JackrabbitSession) resolver.adaptTo(Session.class);
        Authorizable authorizable = null;
        try {
            authorizable = jSession.getUserManager().getAuthorizable(jSession.getUserID());
            principalIds = AuthorizableUtil.getPrincipalIds(authorizable);
        } catch (RepositoryException e) {
            throw new IllegalStateException(e); //TODO
        }
    }

    @Override
    public Component getComponentOfResource(Resource resource) {
        Component out = wrapped.getComponentOfResource(resource);
        if (!isVisible(out)) {
            return null;
        }
        return out;
    }

    @Override
    public Component getComponent(String s) {
        Component out = wrapped.getComponent(s);
        if (!isVisible(out)) {
            return null;
        }
        return out;
    }

    @Override
    public Collection<Component> getComponents() {
        List<Component> out = new ArrayList<>(wrapped.getComponents());
        ListIterator<Component> componentListIterator = out.listIterator();
        while (componentListIterator.hasNext()) {
            Component component = componentListIterator.next();
            if (!isVisible(component)) {
                componentListIterator.remove();
            }
        }
        return out;
    }

    private boolean isVisible(Component c) {
        if (c == null) {
            return false;
        }
        return lockManager.getComponentPermissionsInherited(c.getResourceType(), principalIds) != LockPermission.DENY;
    }
}
