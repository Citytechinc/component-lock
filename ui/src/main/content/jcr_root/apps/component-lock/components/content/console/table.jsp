<%@include file="/apps/component-lock/components/global.jsp"%>
<%@ page import="com.icfi.aem.componentlock.components.content.ComponentTable" %>
<c:set var="table" value="<%=new ComponentTable(slingRequest) %>"/>

<form action="${table.postPath}" method="post" data-async-submit="true">
    <table class="coral-Table">
        <thead>
        <tr class="coral-Table-row">
            <th class="coral-Table-headerCell">Resource Type</th>
            <th class="coral-Table-headerCell">Component</th>
            <th class="coral-Table-headerCell">Permissions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${table.components}" var="component">
            <tr class="coral-Table-row">
                <td class="coral-Table-cell js-query">${component.resourceType}</td>
                <td class="coral-Table-cell js-query">${component.componentName}</td>
                <td class="coral-Table-cell">
                    <div class="coral-Selector cl-console-selector">
                        <label class="coral-Selector-option">
                            <input class="coral-Selector-input" type="radio" name="${component.resourceType}/clPermission" value="ALLOW" ${cl:isChecked("ALLOW", component.lockPermission)}>
                            <span class="coral-Selector-description" title="ALLOW">
                              <i class="coral-Icon coral-Icon--check coral-Selector-icon"></i>
                                Allow
                            </span>
                        </label>
                        <label class="coral-Selector-option">
                            <input class="coral-Selector-input" type="radio" name="${component.resourceType}/clPermission" value="DENY" ${cl:isChecked("DENY", component.lockPermission)}>
                            <span class="coral-Selector-description" title="DENY">
                              <i class="coral-Icon coral-Icon--close coral-Selector-icon"></i>
                                Deny
                            </span>
                        </label>
                        <label class="coral-Selector-option">
                            <input class="coral-Selector-input" type="radio" name="${component.resourceType}/clPermission" value="DEFAULT" ${cl:isChecked("DEFAULT", component.lockPermission)}>
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
    <input type="hidden" name="jcr:primaryType" value="nt:unstructured">
    <input type="submit" class="coral-Button coral-Button--primary">
</form>