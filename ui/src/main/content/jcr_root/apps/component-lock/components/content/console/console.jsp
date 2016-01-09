<%@include file="/apps/component-lock/components/global.jsp"%>
<%@ page import="com.icfi.aem.componentlock.components.content.ComponentConsole" %>
<c:set var="console" value="<%=new ComponentConsole(slingRequest) %>"/>
<div id="cl-console">
    <div id="cl-console-header" class="coral-Form coral-Form--aligned">
        <section class="coral-Form-fieldset">

            <div class="coral-Form-fieldwrapper headerFieldRow">
                <label class="coral-Form-fieldlabel">User/Group</label>
                <span class="coral-Autocomplete cl-console-field" data-init="autocomplete" data-forceselection="true" id="cl-console-user-select" data-request-path="${resource.path}">
                    <span class="coral-DecoratedTextfield js-coral-Autocomplete-field">
                        <i class="coral-DecoratedTextfield-icon coral-Icon coral-Icon--sizeXS coral-Icon--search"></i>
                        <input class="coral-DecoratedTextfield-input coral-Textfield js-coral-Autocomplete-textfield" type="text" name="name1" placeholder="Search" value="">
                    </span>
                    <ul class="coral-SelectList js-coral-Autocomplete-selectList">
                        <c:forEach items="${console.authorizables}" var="authorizable">
                            <li class="coral-SelectList-item coral-SelectList-item--option" data-value="${authorizable.ID}"><i class="coral-Icon<c:if test="${authorizable.group}"> coral-Icon--users</c:if><c:if test="${!authorizable.group}"> coral-Icon--user</c:if> coral-Select-icon selectIcon"></i>${authorizable.ID}</li>
                        </c:forEach>
                    </ul>
                </span>
            </div>

            <div class="coral-Form-fieldwrapper headerFieldRow">
                <label class="coral-Form-fieldlabel">Filter (<span id="cl-console-visible">0</span> of <span id="cl-console-total">0</span>)</label>
                <span class="coral-DecoratedTextfield js-coral-Autocomplete-field cl-console-field">
                    <i class="coral-DecoratedTextfield-icon coral-Icon coral-Icon--sizeXS coral-Icon--search"></i>
                    <input class="coral-DecoratedTextfield-input coral-Textfield" type="text" id="cl-console-query" placeholder="Filter">
                </span>
                <label class="coral-Switch" id="cl-console-toggle-field">
                    <input class="coral-Switch-input" id="cl-console-toggle-default" type="checkbox">
                    <span class="coral-Switch-offLabel"> All</span><span class="coral-Switch-onLabel">Hide&nbsp;Default</span>
                </label>
                <span id="cl-console-wait-indicator" class="waitIndicator"><i class="coral-Wait"></i> <span class="loadingText">Loading...</span><span class="filteringText">Filtering...</span></span>
            </div>

        </section>
    </div>
    <div id="cl-console-table-container">
        <h2>Select a user/group to view authoring permissions</h2>
    </div>
</div>