package com.icfi.aem.componentlock.manager.impl;

import com.icfi.aem.componentlock.constants.JcrProperties;
import com.icfi.aem.componentlock.manager.ComponentLockManager;
import com.icfi.aem.componentlock.model.LockPermission;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(adaptables = ResourceResolver.class)
public class ComponentLockManagerImpl implements ComponentLockManager {

    private final ResourceResolver resolver;

    public ComponentLockManagerImpl(ResourceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public String getConfigurationPath() {
        return "/etc/component-lock";
    }

    @Override
    public LockPermission getComponentPermissions(String resourceType, String principalId) {
        Resource resource = resolver.resolve(getConfigurationPath() + "/" + principalId + "/" + resourceType);
        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
            String permission = resource.getValueMap().get(JcrProperties.CL_PERMISSION, LockPermission.DEFAULT.name());
            LockPermission lockPermission = LockPermission.fromName(permission);
            return lockPermission != null ? lockPermission : LockPermission.DEFAULT;
        }
        return LockPermission.DEFAULT;
    }

    @Override
    public LockPermission getComponentPermissionsInherited(String resourceType, List<String> principalIds) {
        LockPermission out = LockPermission.DEFAULT;
        while (resourceType != null) {
            for (String principalId : principalIds) {
                Resource resource = resolver.resolve(getConfigurationPath() + "/" + principalId + "/" + resourceType);
                if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
                    String permission =
                        resource.getValueMap().get(JcrProperties.CL_PERMISSION, LockPermission.DEFAULT.name());
                    LockPermission lockPermission = LockPermission.fromName(permission);
                    out = lockPermission != null ? lockPermission : LockPermission.DEFAULT;
                    if (out != LockPermission.DEFAULT) {
                        return out;
                    }
                }
            }
            resourceType = ResourceUtil.getParent(resourceType);
        }
        return out;
    }

    @Override
    public Map<String, Boolean> getComponentPermissions(String resourceType) {
        return new HashMap<>();
    }
}