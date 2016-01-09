package com.icfi.aem.componentlock.aem;

import com.day.cq.wcm.api.components.ComponentManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

public class ComponentLockResourceResolverWrapper implements ResourceResolver {

    private final ResourceResolver wrapped;
    private LockAwareComponentManager cachedLockAwareComponentManager;

    public ComponentLockResourceResolverWrapper(ResourceResolver wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Resource resolve(HttpServletRequest httpServletRequest, String s) {
        return wrapped.resolve(httpServletRequest, s);
    }

    @Override
    public Resource resolve(String s) {
        return wrapped.resolve(s);
    }

    @Override
    @Deprecated
    public Resource resolve(HttpServletRequest httpServletRequest) {
        return wrapped.resolve(httpServletRequest);
    }

    @Override
    public String map(String s) {
        return wrapped.map(s);
    }

    @Override
    public String map(HttpServletRequest httpServletRequest, String s) {
        return wrapped.map(httpServletRequest, s);
    }

    @Override
    public Resource getResource(String s) {
        return wrapped.getResource(s);
    }

    @Override
    public Resource getResource(Resource resource, String s) {
        return wrapped.getResource(resource, s);
    }

    @Override
    public String[] getSearchPath() {
        return wrapped.getSearchPath();
    }

    @Override
    public Iterator<Resource> listChildren(Resource resource) {
        return wrapped.listChildren(resource);
    }

    @Override
    public Iterable<Resource> getChildren(Resource resource) {
        return wrapped.getChildren(resource);
    }

    @Override
    public Iterator<Resource> findResources(String s, String s1) {
        return wrapped.findResources(s, s1);
    }

    @Override
    public Iterator<Map<String, Object>> queryResources(String s, String s1) {
        return wrapped.queryResources(s, s1);
    }

    @Override
    public boolean hasChildren(Resource resource) {
        return wrapped.hasChildren(resource);
    }

    @Override
    public ResourceResolver clone(Map<String, Object> map) throws LoginException {
        return wrapped.clone(map);
    }

    @Override
    public boolean isLive() {
        return wrapped.isLive();
    }

    @Override
    public void close() {
        wrapped.close();
    }

    @Override
    public String getUserID() {
        return wrapped.getUserID();
    }

    @Override
    public Iterator<String> getAttributeNames() {
        return wrapped.getAttributeNames();
    }

    @Override
    public Object getAttribute(String s) {
        return wrapped.getAttribute(s);
    }

    @Override
    public void delete(Resource resource) throws PersistenceException {
        wrapped.delete(resource);
    }

    @Override
    public Resource create(Resource resource, String s, Map<String, Object> map) throws PersistenceException {
        return wrapped.create(resource, s, map);
    }

    @Override
    public void revert() {
        wrapped.revert();
    }

    @Override
    public void commit() throws PersistenceException {
        wrapped.commit();
    }

    @Override
    public boolean hasChanges() {
        return wrapped.hasChanges();
    }

    @Override
    public String getParentResourceType(Resource resource) {
        return wrapped.getParentResourceType(resource);
    }

    @Override
    public String getParentResourceType(String s) {
        return wrapped.getParentResourceType(s);
    }

    @Override
    public boolean isResourceType(Resource resource, String s) {
        return wrapped.isResourceType(resource, s);
    }

    @Override
    public void refresh() {
        wrapped.refresh();
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> aClass) {
        if (aClass.isAssignableFrom(ComponentManager.class)) {
            if (cachedLockAwareComponentManager == null) {
                cachedLockAwareComponentManager = new LockAwareComponentManager(wrapped);
            }
            return (AdapterType) cachedLockAwareComponentManager;
        }
        return wrapped.adaptTo(aClass);
    }

    public ResourceResolver getWrapped() {
        return wrapped;
    }
}
