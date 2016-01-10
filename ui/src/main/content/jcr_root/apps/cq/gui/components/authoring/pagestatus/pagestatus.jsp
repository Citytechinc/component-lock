<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%@page session="false" import="com.adobe.cq.contentinsight.ProviderSettingsManager,
                                    com.adobe.cq.wcm.launches.utils.LaunchUtils,
                                    com.adobe.granite.security.user.UserPropertiesManager,
                                    com.adobe.granite.security.user.util.AuthorizableUtil,
                                    javax.jcr.security.Privilege,
                                    com.adobe.granite.ui.components.AttrBuilder,
                                    com.adobe.granite.ui.components.Config,
                                    com.day.cq.commons.date.RelativeTimeFormat,
                                    com.day.cq.security.util.CqActions,
                                    com.day.cq.wcm.api.Page,
                                    com.day.cq.wcm.api.components.Component,
                                    com.day.cq.wcm.msm.api.BlueprintManager,
                                    com.day.cq.wcm.msm.api.LiveRelationshipManager,
                                    org.apache.jackrabbit.api.JackrabbitSession,
                                    org.apache.jackrabbit.api.security.principal.PrincipalIterator,
                                    org.apache.jackrabbit.api.security.user.Authorizable,
                                    org.apache.sling.api.resource.Resource,
                                    org.apache.sling.api.resource.ValueMap,
                                    javax.jcr.RepositoryException,
                                    javax.jcr.Session,
                                    javax.jcr.security.AccessControlManager,
                                    java.net.URLEncoder,
                                    java.security.Principal,
                                    java.util.Calendar,
                                    java.util.Collection,
                                    java.util.LinkedHashSet,
                                    java.util.List,
                                    java.util.ResourceBundle,
                                    java.util.Set" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="com.icfi.aem.componentlock.repository.ComponentLockRepository" %>
<%@ page import="com.icfi.aem.componentlock.model.LockPermission" %>
<%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    ResourceBundle resourceBundle = slingRequest.getResourceBundle(slingRequest.getLocale());

    AccessControlManager acm = null;
    Session session = resourceResolver.adaptTo(Session.class);
    Authorizable authorizable = resourceResolver.adaptTo(Authorizable.class);

    try {
        acm = session.getAccessControlManager();
    } catch (RepositoryException e) {
        log.error("Unable to get access manager", e);
    }

    // utils
    RelativeTimeFormat relativeTime = new RelativeTimeFormat("r", resourceBundle);


    Page targetPage = null;

    // get page object from suffix
    String pagePath = slingRequest.getRequestPathInfo().getSuffix();
    if( pagePath != null ) {
        Resource pageResource = slingRequest.getResourceResolver().resolve(pagePath);
        if( pageResource != null ) {
            targetPage = pageResource.adaptTo(Page.class);
        }
    }

    if( targetPage == null ) return;

    // page properties
    ValueMap targetPageProperties = targetPage.getProperties();

    Calendar modifiedDateRaw = targetPage.getLastModified();
    String modifiedDate = modifiedDateRaw == null ?
            i18n.get("never") :
            relativeTime.format(modifiedDateRaw.getTimeInMillis(), true);
    String modifiedBy = AuthorizableUtil.getFormattedName(resourceResolver, targetPage.getLastModifiedBy());

    Calendar publishedDateRaw = targetPageProperties.get("cq:lastReplicated", Calendar.class);
    String publishedDate = publishedDateRaw == null ?
            i18n.get("never") :
            relativeTime.format(publishedDateRaw.getTimeInMillis(), true);
    String publishedBy = AuthorizableUtil.getFormattedName(resourceResolver, targetPageProperties.get("cq:lastReplicatedBy", ""));

    String lastReplicationAction = targetPageProperties.get("cq:lastReplicationAction", String.class);
    Calendar lastReplicationDateRaw = targetPageProperties.get("cq:lastRolledout", Calendar.class);
    String rolledOutDate = lastReplicationDateRaw == null ?
            i18n.get("never") :
            relativeTime.format(lastReplicationDateRaw.getTimeInMillis(), true);
    boolean isDeactivated = "Deactivate".equals(lastReplicationAction);
    String publishStatus = "";
    if(isDeactivated){
        publishStatus = i18n.get("Page has been deactivated");
    }else if(publishedDateRaw == null){
        publishStatus = i18n.get("Page is not published");
    }else{
        publishStatus = i18n.get("Page has been published") + " " + publishedDate;
    }

    boolean canModify = false;
    try {
        // Get the set of principals for authorizable
        Set<Principal> principals = new LinkedHashSet<Principal>();
        Principal principal = authorizable.getPrincipal();
        principals.add(principal);

        for (PrincipalIterator it = ((JackrabbitSession) session).getPrincipalManager().getGroupMembership(principal); it.hasNext();) {
            principals.add(it.nextPrincipal());
        }

        // Test the modify permission from allowed actions
        CqActions cqActions = new CqActions(session);
        Collection<String> allowedActions = cqActions.getAllowedActions(targetPage.getPath(), principals);
        canModify = allowedActions.contains("modify");
    } catch (RepositoryException e) {
        log.error("Unable to retrieve allowed user actions", e);
    }

    LiveRelationshipManager relationshipManager = sling.getService(LiveRelationshipManager.class);
    BlueprintManager bpm = resourceResolver.adaptTo(BlueprintManager.class);
    boolean isBlueprint = false;
    if (relationshipManager != null) {
        isBlueprint =  bpm != null
                && bpm.getContainingBlueprint(targetPage.getPath()) != null
                && relationshipManager.isSource(targetPage.adaptTo(Resource.class));
    }

    ProviderSettingsManager providerSettingsManager = sling.getService(ProviderSettingsManager.class);
    boolean hasAnalytics = false;
    if(providerSettingsManager != null) {
        hasAnalytics = providerSettingsManager.hasActiveProviders(targetPage.adaptTo(Resource.class));
    }

    // dom attributes
    Config cfg = new Config(resource);
    AttrBuilder divAttrs = new AttrBuilder(request, xssAPI);
    divAttrs.add("id", cfg.get("id", String.class));
    divAttrs.addOther("path", resource.getPath());
    divAttrs.addOthers(cfg.getProperties(), "id");

    String propertyConfig = cfg.get("propertyURL", "/libs/wcm/core/content/sites/properties.html");
    String propertyURL = request.getContextPath() + propertyConfig + "?" + cfg.get("propertyURLParam", "item") + "=" + URLEncoder.encode(targetPage.getPath(), "utf-8");

    String publishLabel = xssAPI.filterHTML(i18n.get("Publish"));
    String unpublishLabel = xssAPI.filterHTML(i18n.get("Unpublish"));
    String propertiesLabel = xssAPI.filterHTML(i18n.get("Properties"));
    String analyticsLabel = xssAPI.filterHTML(i18n.get("Analytics & Recommendations"));
    String lockPageLabel = xssAPI.filterHTML(i18n.get("Lock Page"));
    String rolloutPageLabel = xssAPI.filterHTML(i18n.get("Rollout Page"));

    boolean hasPermission = hasPermission(acm, targetPage, "crx:replicate");
    if (!hasPermission) {
        publishLabel = xssAPI.filterHTML(i18n.get("Request publication"));
        unpublishLabel = xssAPI.filterHTML(i18n.get("Request unpublication"));
    }

    boolean hasLockUnlockPermission = hasPermission(acm, targetPage, "jcr:lockManagement");

    Component component = resourceResolver.getResource(targetPage.getContentResource().getResourceType()).adaptTo(Component.class);
    Resource dialog = component.getLocalResource("cq:dialog");
    if (dialog == null) {
        dialog = component.getLocalResource("dialog");
    }
    String dialogSrc = request.getContextPath() + dialog.getResourceResolver().map(dialog.getPath()) + ".html" + targetPage.getContentResource().getPath();

    boolean isLaunchResource = LaunchUtils.isLaunchResourcePath(targetPage.getContentResource().getPath());

    AttrBuilder propertiesActivatorAttrs = new AttrBuilder(request, xssAPI);
    propertiesActivatorAttrs.addClass("properties-activator");
    propertiesActivatorAttrs.add("data-path", targetPage.getContentResource().getPath());
    propertiesActivatorAttrs.add("data-dialog-src", dialogSrc);

%><div <%= divAttrs.build() %>>

    <div class="pageinfo-unpublishconfirm coral-Popover-content">
        <div class="popover-action-header">
            <h2 class="coral-Heading coral-Heading--2"> <%= unpublishLabel %> </h2>
        </div>

        <div class="popover-action-content">
            <span> <%= xssAPI.filterHTML(i18n.get("You are going to unpublish:")) %><br>
                <%= xssAPI.filterHTML(targetPageProperties.get("jcr:title", "")) %> </span>
            <div class="popover-action-actions">
                <button class="coral-Button unpublishconfirm-cancel"> <%= xssAPI.filterHTML(i18n.get("Cancel")) %> </button>
                <button class="coral-Button coral-Button--primary unpublishconfirm-confirm primary cq-siteadmin-admin-actions-quickunpublish-activator" data-path="<%= targetPage.getPath() %>" data-edit=true> <%= xssAPI.filterHTML(i18n.get("Confirm")) %> </button>
            </div>
        </div>
    </div>

    <div class="pageinfo-statusandactions">
        <ul class="coral-Popover-content coral-List coral-List--minimal pageinfo-pagestatus">
            <li class="coral-List-item info pageinfo-title">
                <h1 class="coral-Heading coral-Heading--1"><%= xssAPI.filterHTML(targetPageProperties.get("jcr:title", "")) %></h1>
            </li>

            <li class="coral-List-item info pageinfo-status">
                <i class="coral-Icon coral-Icon--edit coral-Icon--sizeXS" title="<%= xssAPI.filterHTML(i18n.get("Modified")) %>"></i>
                <span>
                <span><%= modifiedDate %></span> <%= xssAPI.filterHTML(i18n.get("by")) %> <span class="pageinfo-user"><%= modifiedBy %></span>
                </span>
            </li>
            <%
                if (!isLaunchResource) {
            %>
            <li class="coral-List-item info pageinfo-status">
                <i class="coral-Icon coral-Icon--globe coral-Icon--sizeXS" title="<%= xssAPI.filterHTML(i18n.get("Published")) %>"></i>
                <span>
                    <span><%= publishStatus %></span>
                        <% if(publishedDateRaw != null) { %>
                            <%= xssAPI.filterHTML(i18n.get("by")) %> <span class="pageinfo-user"><%= publishedBy %></span>
                        <% } %>
                </span>
            </li>

            <li class="coral-List-item action">
                <button class="coral-Button unpublish-confirmator pageinfo-pageaction" data-path="<%= targetPage.getPath() %>" data-edit=true><%= unpublishLabel %></button>
                <button class="coral-Button coral-Button--primary cq-siteadmin-admin-actions-quickpublish-activator" data-path="<%= targetPage.getPath() %>" data-edit=true><%= publishLabel %></button>
            </li>
            <%
                } // end of !isLaunchResource
            %>
        </ul>

        <ul class="coral-Popover-content coral-List coral-List--minimal pageinfo-pageactions">
            <%
                if(canModify) {
                    ResourceResolver resolver = slingRequest.getResourceResolver();
                    try {
                        List<String> principalIds = com.icfi.aem.componentlock.util.AuthorizableUtil.getPrincipalIds(resolver);
                        ComponentLockRepository lockRepository = resolver.adaptTo(ComponentLockRepository.class);
                        String resourceType = targetPage.getContentResource().getResourceType();
                        canModify = lockRepository.getComponentPermissionsInherited(resourceType, principalIds) != LockPermission.DENY;
                    } catch (RepositoryException e) {
                        e.printStackTrace(); //TODO
                    }
                }
                if(canModify) {
            %>
            <li class="coral-List-item pageinfo-editproperties properties-activator pageinfo-pageaction" <%= propertiesActivatorAttrs.build() %>>
                <i class="coral-Icon coral-Icon--wrench coral-Icon--sizeS" title="<%= propertiesLabel %>"></i><%= propertiesLabel %>
            </li>
            <% } %>

            <% if(hasAnalytics) { %>
            <li class="coral-List-item open-contentinsight pageinfo-pageaction">
                <i class="coral-Icon coral-Icon--adobeAnalytics coral-Icon--sizeS" title="<%= analyticsLabel %>"></i><%= analyticsLabel %>
            </li>
            <% } %>

            <% if(!targetPage.isLocked() && hasLockUnlockPermission) { %>
            <li class="cq-author-lock-page coral-List-item pageinfo-pageaction">
                <i class="coral-Icon coral-Icon--lockOn coral-Icon--sizeS" title="<%= lockPageLabel %>"></i><%= lockPageLabel %>
            </li>
            <% } %>

            <% if(isBlueprint) { %>
            <li id="rolloutBtn" class="coral-List-item pageinfo-pageaction">
                <i class="coral-Icon coral-Icon--unmerge coral-Icon--sizeS" title="<%= rolloutPageLabel %>"></i><%= rolloutPageLabel %>
            </li>
            <% } %>

        </ul>
    </div>

</div><%!

    private boolean hasPermission(AccessControlManager acm, Page page, String privilege) {
        try {
            if (acm != null) {
                Privilege p = acm.privilegeFromName(privilege);
                return acm.hasPrivileges(page.getPath(), new Privilege[]{p});
            }
        } catch (RepositoryException e) {
            // ignore
        }
        return false;
    }
%>