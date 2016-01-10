package com.icfi.aem.componentlock.util;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class AuthorizableUtil {

    public static List<String> getPrincipalIds(ResourceResolver resolver) throws RepositoryException {
        return getPrincipalIds(getAuthorizable(resolver));
    }

    public static Authorizable getAuthorizable(ResourceResolver resolver) throws RepositoryException {
        JackrabbitSession jSession = (JackrabbitSession) resolver.adaptTo(Session.class);
        return jSession.getUserManager().getAuthorizable(jSession.getUserID());
    }

    public static List<String> getPrincipalIds(Authorizable authorizable) throws RepositoryException {
        List<String> out = new ArrayList<>();
        out.add(authorizable.getID());
        Iterator<Group> groupIterator = authorizable.memberOf();
        while (groupIterator.hasNext()) {
            Group group = groupIterator.next();
            out.add(group.getID());
        }
        return out;
    }

    private AuthorizableUtil() { }
}
