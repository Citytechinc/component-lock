package com.icfi.aem.componentlock.servlet;

import com.icfi.aem.componentlock.constants.JcrProperties;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The servlet responsible for writing component lock data from the console.  This implementation is used instead of
 * Sling POST in order to create a sparse tree (only ALLOW and DENY nodes, and their ancestors).
 */
@SlingServlet(resourceTypes = "apps/component-lock/data-root", methods = "POST")
public class ComponentLockConfigurationServlet extends SlingAllMethodsServlet {

    public static final String USER_ATTR = "/USER";
    public static final String ROOT_ATTR = "/ROOT";

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws ServletException, IOException {
        String user = request.getParameter(USER_ATTR);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String root = request.getParameter(ROOT_ATTR);
        Map<String, String> settings = new HashMap<>();
        for (RequestParameter parameter : request.getRequestParameterList()) {
            String value = parameter.getString();
            if (!parameter.getName().startsWith("/") && value != null && !value.isEmpty()) {
                settings.put(parameter.getName(), parameter.getString());
            }
        }
        ResourceResolver resolver = request.getResourceResolver();
        Resource rootResource = request.getResource();
        String path = rootResource.getPath() + "/" + user;
        Map<String, Object> props = new HashMap<>();
        props.put("jcr:primaryType", "nt:unstructured");
        Resource userResource = resolver.resolve(path);
        if (userResource != null && !ResourceUtil.isNonExistingResource(userResource)) {
            resolver.delete(userResource);
        }
        userResource = ResourceUtil.getOrCreateResource(resolver, path, props, null, false);
        Resource typeResource;
        for (Map.Entry<String, String> setting : settings.entrySet()) {
            String typePath = path + "/" + setting.getKey();
            typeResource = ResourceUtil.getOrCreateResource(resolver, typePath, props, null, false);
            typeResource.adaptTo(ModifiableValueMap.class).put(JcrProperties.CL_PERMISSION, setting.getValue());
        }
        if (root != null && !root.isEmpty()) {
            userResource.adaptTo(ModifiableValueMap.class).put(JcrProperties.CL_PERMISSION, root);
            userResource.adaptTo(ModifiableValueMap.class).put("jcr:mixinTypes", "mix:created");
        }
        resolver.commit();
    }
}
