package com.icfi.aem.componentlock.components.content;

import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import com.icfi.aem.componentlock.aem.ComponentLockResourceResolverWrapper;
import com.icfi.aem.componentlock.manager.ComponentLockManager;
import com.icfi.aem.componentlock.manager.impl.ComponentLockManagerImpl;
import com.icfi.aem.componentlock.model.LockPermission;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Model(adaptables = SlingHttpServletRequest.class)
public class ComponentTable {

    private final String userId;
    private final ComponentManager componentManager;
    private final ComponentLockManager componentLockManager;
    private final ComponentData rootComponent;

    private final Map<String, ComponentData> components = new TreeMap<>();

    public ComponentTable(SlingHttpServletRequest request) {
        if (request.getRequestPathInfo().getSelectors().length < 2) {
            throw new IllegalArgumentException("Component table requires a user/group id selector");
        }
        userId = request.getRequestPathInfo().getSelectors()[1];
        ResourceResolver resolver = request.getResourceResolver();
        if (resolver instanceof ComponentLockResourceResolverWrapper) {
            resolver = ((ComponentLockResourceResolverWrapper) resolver).getWrapped();
        }
        componentManager = resolver.adaptTo(ComponentManager.class);
        componentLockManager = resolver.adaptTo(ComponentLockManagerImpl.class);

        rootComponent = new ComponentData("[ROOT]");
        rootComponent.setLockPermission(componentLockManager.getComponentPermissions(null, userId));

        for (Component component: componentManager.getComponents()) {
            String resourceType = component.getResourceType();
            ComponentData componentData = components.get(resourceType);
            boolean populateAncestors = false;
            if (componentData == null) {
                componentData = new ComponentData(resourceType);
                components.put(resourceType, componentData);
                populateAncestors = true;
            }
            componentData.setComponent(true);
            componentData.setComponentName(component.getProperties().get("jcr:title", String.class));
            componentData.setComponentGroup(component.getComponentGroup());
            if (componentData.getLockPermission() == null) {
                componentData.setLockPermission(componentLockManager.getComponentPermissions(resourceType, userId));
            }
            resourceType = ResourceUtil.getParent(resourceType);
            while (populateAncestors && resourceType != null) {
                componentData = components.get(resourceType);
                if (componentData == null) {
                    componentData = new ComponentData(resourceType);
                    components.put(resourceType, componentData);
                }
                if (componentData.getLockPermission() == null) {
                    componentData.setLockPermission(componentLockManager.getComponentPermissions(resourceType, userId));
                }
                resourceType = ResourceUtil.getParent(resourceType);
            }
        }
    }

    public String getPostPath() {
        return componentLockManager.getConfigurationPath() + "/" + userId;
    }

    public List<ComponentData> getComponents() {
        return new ArrayList<>(components.values());
    }

    public ComponentData getRootComponent() {
        return rootComponent;
    }

    public static final class ComponentData implements Comparable<ComponentData> {

        private final String resourceType;
        private boolean isComponent;
        private String componentName;
        private String componentGroup;
        private LockPermission lockPermission;

        private ComponentData(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getResourceType() {
            return resourceType;
        }

        public boolean isComponent() {
            return isComponent;
        }

        public void setComponent(boolean component) {
            isComponent = component;
        }

        public String getComponentName() {
            return componentName;
        }

        public void setComponentName(String componentName) {
            this.componentName = componentName;
        }

        public String getComponentGroup() {
            return componentGroup;
        }

        public void setComponentGroup(String componentGroup) {
            this.componentGroup = componentGroup;
        }

        public String getLockPermission() {
            return lockPermission == null ? null : lockPermission.name();
        }

        public void setLockPermission(LockPermission lockPermission) {
            this.lockPermission = lockPermission;
        }

        @Override
        public int compareTo(ComponentData o) {
            return this.resourceType == null ? -1 : this.resourceType.compareTo(o.resourceType);
        }
    }

}
