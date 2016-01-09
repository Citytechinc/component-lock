package com.icfi.aem.componentlock.repository;

import com.icfi.aem.componentlock.model.LockPermission;

import java.util.List;

public interface ComponentLockRepository {

    LockPermission getComponentPermissions(String resourceType, String principalId);

    LockPermission getComponentPermissionsInherited(String resourceType, List<String> principalIds);

}
