<%@include file="/apps/component-lock/components/global.jsp"%>
<%@ page import="com.icfi.aem.componentlock.components.content.ComponentConsole" %>
<c:set var="console" value="<%=new ComponentConsole(slingRequest) %>"/>
<div id="cl-console">
    <div id="cl-console-header" class="coral-Form coral-Form--aligned">
        <section class="coral-Form-fieldset">

            <div class="coral-Form-fieldwrapper">
                <label class="coral-Form-fieldlabel">User/Group</label>
                <div class="coral-Autocomplete" data-init="autocomplete" data-forceselection="true" id="cl-console-user-select" data-request-path="${resource.path}">
                    <span class="coral-DecoratedTextfield js-coral-Autocomplete-field">
                        <i class="coral-DecoratedTextfield-icon coral-Icon coral-Icon--sizeXS coral-Icon--search"></i>
                        <input class="coral-DecoratedTextfield-input coral-Textfield js-coral-Autocomplete-textfield" type="text" name="name1" placeholder="Search" value="">
                    </span>
                    <ul class="coral-SelectList js-coral-Autocomplete-selectList">
                        <c:forEach items="${console.authorizables}" var="authorizable">
                            <li class="coral-SelectList-item coral-SelectList-item--option" data-value="${authorizable.ID}">${authorizable.ID}</li>
                        </c:forEach>
                    </ul>
                </div>
            </div>

            <div class="coral-Form-fieldwrapper">
                <label class="coral-Form-fieldlabel">Filter</label>
                <span class="coral-DecoratedTextfield js-coral-Autocomplete-field">
                    <i class="coral-DecoratedTextfield-icon coral-Icon coral-Icon--sizeXS coral-Icon--search"></i>
                    <input class="coral-DecoratedTextfield-input coral-Textfield" type="text" id="cl-console-query" placeholder="Filter">
                </span>
            </div>
        </section>
    </div>
    <div id="cl-console-table-container"></div>
</div>