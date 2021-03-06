<%@page import="eu.citadel.liferay.portlet.commons.ConverterUtils"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="static eu.citadel.liferay.portlet.converter.controller.ContrCkanExportLisbon.*" %>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@include file="../../init.jsp"%>

<div class="view-header"><liferay-ui:message key="ckan-export"/></div>
<div class="view-info"><liferay-ui:message key="ckan-export-info"/></div>

<%-- <liferay-ui:error key="error-during-operation" message="error-during-operation"/> --%>
<liferay-ui:success key="success" message="success"/>
<%
	String url 			= (String) request.getAttribute(PAGE_ATTRIBUTE_URL);
	String api 			= (String) request.getAttribute(PAGE_ATTRIBUTE_API);
	String ver 			= (String) request.getAttribute(PAGE_ATTRIBUTE_VERS);
%>

<liferay-portlet:actionURL var="createUrl" 		name="ckanExportLisbon_create" />
<liferay-portlet:actionURL var="insertUrl" 		name="ckanExportLisbon_insert" />

<script type="text/javascript" charset="utf-8">
function submitForm(action){
  if(action==0){
	     AUI().one('#<portlet:namespace/>ckan-form').set('action',"<%= createUrl %>");
  }else {
		 AUI().one('#<portlet:namespace/>ckan-form').set('action',"<%= insertUrl %>");
  }
  AUI().one('#<portlet:namespace/>ckan-form').submit();
}
</script>

<aui:form name="ckan-form" method="post" >
	<aui:fieldset>
		<aui:input name="<%= CONTR_PARAM_URL %>" 	label="ckan-url"	inlineField="true" value="<%= url %>" field="<%= PAGE_ATTRIBUTE_URL %>"/>
		<aui:input name="<%= CONTR_PARAM_API %>" 	label="ckan-api"	inlineField="true" value="<%= api %>" />
		<aui:input name="<%= CONTR_PARAM_VERS %>" 	label="ckan-vers"	inlineField="true" value="<%= ver %>" />
	</aui:fieldset>

<%-- 	<liferay-ui:tabs names="ckan-tab-create,ckan-tab-insert" refresh="false"> --%>
	<liferay-ui:tabs names="ckan-tab-create" refresh="false">
		<liferay-ui:section>
			<jsp:include page="lisbonTabCreate.jsp" />
		</liferay-ui:section>
<%-- 		<liferay-ui:section> --%>
<%-- 			<jsp:include page="lisbonTabInsert.jsp" /> --%>
<%-- 		</liferay-ui:section> --%>
	</liferay-ui:tabs>
</aui:form>

<liferay-portlet:actionURL var="finishUrl" 		name="ckanExportLisbon_finish"		/>
<liferay-portlet:actionURL var="previousUrl" 	name="ckanExportLisbon_previousStep"	/>
<liferay-portlet:actionURL var="cancelUrl" 		name="ckanExportLisbon_cancel"		/>

<aui:button-row cssClass="citadel-navigation-menu">
	<aui:button cssClass="btn-primary" value="navigation-cancel" 	href="${cancelUrl}"		/>
	<aui:button cssClass="btn-primary" value="navigation-back" 	 	href="${previousUrl}"	/>
	<aui:button cssClass="btn-primary" value="navigation-next" 		disabled="true"			/>
	<aui:button cssClass="btn-primary" value="navigation-end" 	    href="${finishUrl}"		/>
</aui:button-row>
