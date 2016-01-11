package com.icfi.aem.componentlock.aem;

import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import com.icfi.aem.componentlock.model.LockPermission;
import com.icfi.aem.componentlock.repository.ComponentLockRepository;
import com.icfi.aem.componentlock.util.AuthorizableUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * A custom ComponentManager implementation that restricts the view of components based on the effective component lock
 * configuration for a particular user.  This implementation is injected via ComponentLockResourceResolverWrapper into
 * the AEM internals that support authoring functionality.  The end result is that only allowed components will appear
 * as editable in AEM.
 */
public class LockAwareComponentManager implements ComponentManager {

    private final ComponentManager wrapped;
    private final ComponentLockRepository lockManager;
    private final List<String> principalIds;

    public LockAwareComponentManager(ResourceResolver resolver) {
        this.wrapped = resolver.adaptTo(ComponentManager.class);
        this.lockManager = new ComponentLockRepository(resolver);
        try {
            principalIds = AuthorizableUtil.getPrincipalIds(resolver);
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
