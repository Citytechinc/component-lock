package com.icfi.aem.componentlock.jcr;

import com.icfi.aem.componentlock.constants.Paths;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.servlet.Servlet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component(immediate = true)
@Service
public class ComponentLockModificationListener implements EventListener {

    private static final int EVENTS = Event.NODE_ADDED | Event.NODE_REMOVED | Event.PROPERTY_ADDED
        | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;

    @Reference
    private SlingRepository repository;

    @Reference(target = "(service.pid=com.day.cq.wcm.core.impl.components.ComponentServlet)",
        cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy = ReferencePolicy.DYNAMIC)
    private Servlet servlet;

    @Override
    public void onEvent(EventIterator events) {
        if (servlet != null) {
            try {
                Class<?> c = servlet.getClass();
                Method invalidated = c.getMethod("invalidated");
                invalidated.invoke(servlet);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void bindServlet(Servlet cacheListener) {
        this.servlet = cacheListener;
    }

    public void unbindServlet(Servlet servlet) {
        if (this.servlet == servlet) {
            this.servlet = null;
        }
    }

    @Activate
    protected void activate(ComponentContext componentContext) {
        try {
            Session session = repository.loginAdministrative(null);
            session.getWorkspace().getObservationManager().addEventListener(
                this,
                EVENTS,
                Paths.COMPONENT_LOCK_ROOT,
                true,
                null,
                null,
                false
            );
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        try {
            Session session = repository.loginAdministrative(null);
            session.getWorkspace().getObservationManager().removeEventListener(this);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
