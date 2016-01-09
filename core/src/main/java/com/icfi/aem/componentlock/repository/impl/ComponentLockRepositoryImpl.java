package com.icfi.aem.componentlock.repository.impl;

import com.icfi.aem.componentlock.constants.JcrProperties;
import com.icfi.aem.componentlock.constants.Paths;
import com.icfi.aem.componentlock.repository.ComponentLockRepository;
import com.icfi.aem.componentlock.model.LockPermission;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Model;

import java.util.List;

@Model(adaptables = ResourceResolver.class)
public class ComponentLockRepositoryImpl implements ComponentLockRepository {

    private final ResourceResolver resolver;

    public ComponentLockRepositoryImpl(ResourceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public LockPermission getComponentPermissions(String resourceType, String principalId) {
        String path = Paths.COMPONENT_LOCK_ROOT + "/" + principalId;
        path = resourceType != null ? path + "/" + resourceType : path;
        Resource resource = resolver.resolve(path);
        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
            String permission = resource.getValueMap().get(JcrProperties.CL_PERMISSION, LockPermission.DEFAULT.name());
            LockPermission lockPermission = LockPermission.fromName(permission);
            return lockPermission != null ? lockPermission : LockPermission.DEFAULT;
        }
        return LockPermission.DEFAULT;
    }

    @Override
    public LockPermission getComponentPermissionsInherited(String resourceType, List<String> principalIds) {
        while (resourceType != null) {
            LockPermission lockPermission = checkResourceType(resourceType, principalIds);
            if (lockPermission != LockPermission.DEFAULT) {
                return lockPermission;
            }
            resourceType = ResourceUtil.getParent(resourceType);
        }
        // check the ROOT resource type permission
        return checkResourceType(null, principalIds);
    }

    private LockPermission checkResourceType(String resourceType, List<String> principalIds) {
        LockPermission out = LockPermission.DEFAULT;
        for (String principalId : principalIds) {
            String path = Paths.COMPONENT_LOCK_ROOT + "/" + principalId;
            path = resourceType != null ? path + "/" + resourceType : path;
            Resource resource = resolver.resolve(path);
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
        return out;
    }
}
