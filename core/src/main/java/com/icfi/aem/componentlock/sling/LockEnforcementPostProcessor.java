package com.icfi.aem.componentlock.sling;

import com.icfi.aem.componentlock.model.LockPermission;
import com.icfi.aem.componentlock.repository.ComponentLockRepository;
import com.icfi.aem.componentlock.util.AuthorizableUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.SlingPostProcessor;

import java.util.List;

/**
 * A Sling POST Processor that verifies that users modifying content via Sling POST have appropriate permissions for
 * any component being modified.  When attempting to modify a component type for which the user has insufficient
 * permissions, an exception will be thrown, preventing commit of the changes.
 */
@Service
@Component
public class LockEnforcementPostProcessor implements SlingPostProcessor {

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> list) throws Exception {
        ResourceResolver resolver = request.getResourceResolver();
        ComponentLockRepository lockRepository = resolver.adaptTo(ComponentLockRepository.class);
        String resourceType = request.getResource().getResourceType();
        List<String> principalIds = AuthorizableUtil.getPrincipalIds(resolver);
        if (lockRepository.getComponentPermissionsInherited(resourceType, principalIds) == LockPermission.DENY) {
            throw new IllegalStateException("Insufficient permissions to author component.");
        }
    }
}
