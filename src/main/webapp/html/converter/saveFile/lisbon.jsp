<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@page import="static eu.citadel.liferay.portlet.converter.controller.ContrSaveFileLisbon.*"%>
<%@include file="../../init.jsp"%>

<liferay-portlet:actionURL var="previousUrl" 	name="saveFileLisbon_previousStep"/>
<liferay-portlet:actionURL var="nextUrl" 		name="saveFileLisbon_nextStep"/>
<liferay-portlet:actionURL var="cancelUrl" 		name="saveFileLisbon_cancel"/>

<%
String downloadLink 	= (String) renderRequest.getAttribute(VIEW_ATTRIBUTE_DOWNLOAD_LINK);
String err 				= (String) renderRequest.getAttribute(VIEW_ATTRIBUTE_MESSAGE_ERROR);
String errKey			= (String) renderRequest.getAttribute(VIEW_ATTRIBUTE_KEY_ERROR);
String errLink			= (String) renderRequest.getAttribute(VIEW_ATTRIBUTE_LINK_ERROR);
List<List<String>> res	= (List<List<String>>) renderRequest.getAttribute(VIEW_ATTRIBUTE_PREVIEW_LIST);
if(Validator.isNull(err)){
%>	
	<liferay-portlet:resourceURL id="saveFileLisbon_download" var="downloadUrl">
		<liferay-portlet:param name="<%= PAGE_PARAM_DOWNLOAD_LINK %>" value="<%= downloadLink %>"/>
	</liferay-portlet:resourceURL>

	<div class="view-header"><liferay-ui:message key="save-file-preview-message"/></div>
	<div class="view-info"><liferay-ui:message key="save-file-lisbon-info"/></div>
	
	<liferay-ui:search-container emptyResultsMessage="save-file-no-row">
		<liferay-ui:search-container-results results="<%= res %>" total="-1" />		
	  	<liferay-ui:search-container-row className="java.util.List" modelVar="list" >
			<citadel:search-container-column-list list="${list}" header="${view_attribute_header_list}"/>
	  	</liferay-ui:search-container-row>
	  	<liferay-ui:search-iterator paginate="false"/>
	</liferay-ui:search-container>
		
	
	<aui:button-row>
		<liferay-ui:message key="save-file-if-you-can-see-your-data-properly-you-can"/>
		<aui:button cssClass="btn-primary" value="save-file-download-file" 	    href="<%= downloadUrl %>"		/>
	</aui:button-row>
<%
}else{
%>
	<div class="alert alert-error"><a style="cursor: pointer;" onclick="<%= "window.open('" + errLink + "');" %>"><%= errKey %></a>:<%= err.replace(". ", ".<br/>") %></div>
<%
}
%>

<aui:button-row cssClass="citadel-navigation-menu">
	<aui:button cssClass="btn-primary" value="navigation-cancel" 	href="${cancelUrl}"		/>
	<aui:button cssClass="btn-primary" value="navigation-back" 	 	href="${previousUrl}"	/>
	<aui:button cssClass="btn-primary" value="navigation-next" 		href="${nextUrl}"	/>
	<aui:button cssClass="btn-primary" value="navigation-end" 	    disabled="true"	/>
</aui:button-row>