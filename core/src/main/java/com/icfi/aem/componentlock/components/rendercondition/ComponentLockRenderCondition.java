package com.icfi.aem.componentlock.components.rendercondition;

import com.adobe.granite.ui.components.ComponentHelper;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.icfi.aem.componentlock.model.LockPermission;
import com.icfi.aem.componentlock.repository.ComponentLockRepository;
import com.icfi.aem.componentlock.util.AuthorizableUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Model;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * A RenderCondition implementation that blocks rendering for a users that lack authoring permissions.  This component
 * evaluates the "path" property as EL, resolves the resource at the resulting path, and looks up its resource type. If
 * the current user does not have authoring permissions for that resource type, rendering is blocked.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ComponentLockRenderCondition implements RenderCondition {

    public ComponentLockRenderCondition(SlingHttpServletRequest request, ComponentHelper componentHelper) {
        ResourceResolver resolver = request.getResourceResolver();
        ComponentLockRepository lockRepository = resolver.adaptTo(ComponentLockRepository.class);
        try {
            List<String> principalIds = AuthorizableUtil.getPrincipalIds(resolver);
            Config cfg = componentHelper.getConfig();
            String path = componentHelper.getExpressionHelper().getString(cfg.get("path", ""));
            Resource resource = path != null ? resolver.resolve(path) : null;
            if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
                String type = resource.getResourceType();
                if (lockRepository.getComponentPermissionsInherited(type, principalIds) == LockPermission.DENY) {
                    request.setAttribute(RenderCondition.class.getName(), this);
                }
            }
        } catch (RepositoryException e) {
            throw new IllegalStateException(e); // TODO
        }
    }

    @Override
    public boolean check() throws ServletException, IOException {
        return false;
    }
}
