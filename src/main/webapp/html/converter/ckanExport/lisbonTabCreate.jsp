<%@ page import="eu.citadel.liferay.portlet.converter.ConverterPortlet" %>
<%@ page import="static eu.citadel.liferay.portlet.converter.controller.ContrCkanExportLisbon.*" %>


<%@ page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet" %>
<%@ include file="../../init.jsp" %>

 <%
	String type 		= (String) request.getAttribute(PAGE_ATTRIBUTE_TYPE);
	
	String packName 	= (String) request.getAttribute(PAGE_ATTRIBUTE_PACKAGE_NAME);
	String name 		= (String) request.getAttribute(PAGE_ATTRIBUTE_NAME);
	String desc 		= (String) request.getAttribute(PAGE_ATTRIBUTE_DESC);
 %>

<aui:fieldset>
	<aui:field-wrapper name="ckan-store-type" cssClass="aui-hidden">
		<aui:input inlineLabel="right" inlineField="true" name="<%= CONTR_PARAM_TYPE %>" type="radio" value="<%= TYPE_FILE_STORE %>" label="ckan-create-file-store" checked="<%= TYPE_FILE_STORE.equals(type) %>"/>
		<aui:input inlineLabel="right" inlineField="true" name="<%= CONTR_PARAM_TYPE %>" type="radio" value="<%= TYPE_DATA_STORE %>" label="ckan-create-data-store" checked="<%= TYPE_DATA_STORE.equals(type) %>" disabled="true"/>
	</aui:field-wrapper>

	<aui:input name='<%= CONTR_PARAM_PACKAGE_NAME %>' 	label='ckan-package-name' 	value="<%= packName %>" >
		<aui:validator name="required" />
	</aui:input>
	<aui:input name='<%= CONTR_PARAM_NAME %>' 			label='ckan-name' 			value="<%= name %>" >
		<aui:validator name="required" />
	</aui:input>
	<aui:input name='<%= CONTR_PARAM_DESC %>' 			label='ckan-desc' 			value="<%= desc %>" />
	<aui:button value="ckan-btn-create"	onClick="submitForm(0)" primary="true"/>
</aui:fieldset>
