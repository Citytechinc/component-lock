package com.icfi.aem.componentlock.aem;

import com.day.cq.commons.ListInfoProvider;
import com.icfi.aem.componentlock.model.LockPermission;
import com.icfi.aem.componentlock.repository.ComponentLockRepository;
import com.icfi.aem.componentlock.util.AuthorizableUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import javax.jcr.RepositoryException;
import java.util.List;

@Service
@Component(immediate = true)
public class ComponentLockInfoProvider implements ListInfoProvider {

    public static final String CL_LOCKED_PROP = "cl:locked";

    @Override
    public void updateListGlobalInfo(SlingHttpServletRequest request, JSONObject jsonObject,
        Resource resource) throws JSONException {

        outputLockState(request, jsonObject, resource);
    }

    @Override
    public void updateListItemInfo(SlingHttpServletRequest request, JSONObject jsonObject,
        Resource resource) throws JSONException {

        outputLockState(request, jsonObject, resource);
    }

    private void outputLockState(SlingHttpServletRequest request, JSONObject jsonObject, Resource resource)
        throws JSONException {

        ResourceResolver resolver = request.getResourceResolver();
        ComponentLockRepository lockRepository = resolver.adaptTo(ComponentLockRepository.class);
        String resourceType = resource.getResourceType();
        if ("cq:Page".equals(resourceType)) {
            Resource pageContent = resource.getChild("jcr:content");
            if (pageContent != null && !ResourceUtil.isNonExistingResource(pageContent)) {
                resourceType = pageContent.getResourceType();
            }
        }
        try {
            List<String> principalIds = AuthorizableUtil.getPrincipalIds(resolver);
            jsonObject.put(CL_LOCKED_PROP,
                lockRepository.getComponentPermissionsInherited(resourceType, principalIds) == LockPermission.DENY);
        } catch (RepositoryException e) {
            e.printStackTrace(); //TODO
        }
    }
}
