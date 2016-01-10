package com.icfi.aem.componentlock.repository.impl;

import com.icfi.aem.componentlock.constants.JcrProperties;
import com.icfi.aem.componentlock.constants.Paths;
import com.icfi.aem.componentlock.model.LockPermission;
import com.icfi.aem.componentlock.repository.ComponentLockRepository;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(adaptables = ResourceResolver.class)
public class ComponentLockRepositoryImpl implements ComponentLockRepository {

    private final ResourceResolver resolver;
    private final Map<String, LockPermission> cache = new HashMap<>();

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
        LockPermission out = cache.get(resourceType);
        if (out != null) {
            return out;
        }
        for (String principalId : principalIds) {
            String path = Paths.COMPONENT_LOCK_ROOT + "/" + principalId;
            path = resourceType != null ? path + "/" + resourceType : path;
            Resource resource = resolver.resolve(path);
            if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
                String permission =
                    resource.getValueMap().get(JcrProperties.CL_PERMISSION, LockPermission.DEFAULT.name());
                out = LockPermission.fromName(permission);
                if (out != null && out != LockPermission.DEFAULT) {
                    break;
                }
            }
        }
        out = out == null ? LockPermission.DEFAULT : out;
        cache.put(resourceType, out);
        return out;
    }
}
