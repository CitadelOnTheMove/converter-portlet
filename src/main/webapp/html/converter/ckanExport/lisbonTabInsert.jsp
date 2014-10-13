<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@ page import="static eu.citadel.liferay.portlet.converter.controller.ContrCkanExportLisbon.*" %>
<%@include file="../../init.jsp"%>

 <%
	String resName 		= (String) request.getAttribute(PAGE_ATTRIBUTE_RESOURCE_NAME);
 %>


<aui:fieldset>
	<aui:input name='<%= CONTR_PARAM_RESOURCE_NAME %>' label='ckan-resource-name' value="<%= resName %>">
		<aui:validator name="required" />
	</aui:input>
	<aui:button value="ckan-btn-insert"	onClick="submitForm(1)" primary="true"/>
</aui:fieldset>
