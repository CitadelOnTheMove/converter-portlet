<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCListController"%>
<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@include file="../init.jsp"%>


<div class="alert alert-error">
	<liferay-ui:message key="input-detail-unsupported-message"/>
</div>

<liferay-portlet:actionURL var="previousUrl" 	name="inputDetail_previousStep"/>
<liferay-portlet:actionURL var="cancelUrl" 		name="inputDetail_cancel"/>
		
<aui:button-row cssClass="citadel-navigation-menu">
	<aui:button cssClass="btn-primary" value="navigation-cancel" href="${cancelUrl}"	/>
	<aui:button cssClass="btn-primary" value="navigation-back" 	 href="${previousUrl}"	/>
	<aui:button cssClass="btn-primary" value="navigation-next" 	 disabled="true"		/>
	<aui:button cssClass="btn-primary" value="navigation-end" 	 disabled="true"	  	/>
</aui:button-row>