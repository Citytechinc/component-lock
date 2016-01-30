<%@include file="/apps/component-lock/components/global.jsp"%>
<%@ page import="com.icfi.aem.componentlock.components.content.ComponentTable" %>
<c:set var="table" value="<%=new ComponentTable(slingRequest) %>"/>

<form action="${table.postPath}" method="post" data-async-submit="true">
    <input type="hidden" name="/USER" value="${table.userId}">
    <table class="coral-Table cl-console-permissions-table">
        <thead>
        <tr class="coral-Table-row">
            <th class="coral-Table-headerCell resourceType">Resource Type</th>
            <th class="coral-Table-headerCell component">Component</th>
            <th class="coral-Table-headerCell permissions">Permissions</th>
        </tr>
        </thead>
        <tbody>
        <tr class="coral-Table-row rootRow">
            <td class="coral-Table-cell indent-wrap">${table.rootComponent.resourceTypeWrapping}</td>
            <td class="coral-Table-cell"></td>
            <td class="coral-Table-cell">
                <div class="coral-Selector cl-console-selector">
                    <label class="coral-Selector-option allowOption">
                        <input class="coral-Selector-input" type="radio" name="/ROOT" value="ALLOW" ${cl:isChecked("ALLOW", table.rootComponent.lockPermission)}>
                            <span class="coral-Selector-description" title="ALLOW">
                              <i class="coral-Icon coral-Icon--check coral-Selector-icon"></i>
                                Allow
                            </span>
                    </label>
                    <label class="coral-Selector-option denyOption">
                        <input class="coral-Selector-input" type="radio" name="/ROOT" value="DENY" ${cl:isChecked("DENY", table.rootComponent.lockPermission)}>
                            <span class="coral-Selector-description" title="DENY">
                              <i class="coral-Icon coral-Icon--close coral-Selector-icon"></i>
                                Deny
                            </span>
                    </label>
                    <label class="coral-Selector-option">
                        <input class="coral-Selector-input" type="radio" name="/ROOT" value=""  ${cl:isChecked("DEFAULT", table.rootComponent.lockPermission)}>
                            <span class="coral-Selector-description" title="DEFAULT">
                              <i class="coral-Icon coral-Icon--arrowUp coral-Selector-icon"></i>
                                Inherit
                            </span>
                    </label>
                </div>
            </td>
        </tr>
        <c:forEach items="${table.components}" var="component">
            <tr class="coral-Table-row <c:if test="${!component.component}">ancestorRow</c:if>" data-query-text="${component.resourceType} ${component.componentName}">
                <td class="coral-Table-cell indent-wrap">${component.resourceTypeWrapping}</td>
                <td class="coral-Table-cell ">${component.componentName}</td>
                <td class="coral-Table-cell">
                    <div class="coral-Selector cl-console-selector">
                        <label class="coral-Selector-option allowOption">
                            <input class="coral-Selector-input" type="radio" name="${component.resourceType}" value="ALLOW" ${cl:isChecked("ALLOW", component.lockPermission)}>
                            <span class="coral-Selector-description" title="ALLOW">
                              <i class="coral-Icon coral-Icon--check coral-Selector-icon"></i>
                                Allow
                            </span>
                        </label>
                        <label class="coral-Selector-option denyOption">
                            <input class="coral-Selector-input" type="radio" name="${component.resourceType}" value="DENY" ${cl:isChecked("DENY", component.lockPermission)}>
                            <span class="coral-Selector-description" title="DENY">
                              <i class="coral-Icon coral-Icon--close coral-Selector-icon"></i>
                                Deny
                            </span>
                        </label>
                        <label class="coral-Selector-option">
                            <input class="coral-Selector-input" type="radio" name="${component.resourceType}" value="" ${cl:isChecked("DEFAULT", component.lockPermission)}>
                            <span class="coral-Selector-description" title="DEFAULT">
                              <i class="coral-Icon coral-Icon--arrowUp coral-Selector-icon"></i>
                                Inherit
                            </span>
                        </label>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <input type="submit" class="coral-Button coral-Button--primary">
</form>