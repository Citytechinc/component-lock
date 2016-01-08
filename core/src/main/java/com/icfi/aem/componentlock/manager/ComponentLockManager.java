package com.icfi.aem.componentlock.manager;

import com.icfi.aem.componentlock.model.LockPermission;

import java.util.List;
import java.util.Map;

public interface ComponentLockManager {

    String getConfigurationPath();

    LockPermission getComponentPermissions(String resourceType, String principalId);

    LockPermission getComponentPermissionsInherited(String resourceType, List<String> principalIds);

    Map<String, Boolean> getComponentPermissions(String resourceType);

}
