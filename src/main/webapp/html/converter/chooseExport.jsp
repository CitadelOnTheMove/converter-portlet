<%@page import="com.liferay.util.bridges.mvc.MVCPortlet"%>
<%@page import="eu.citadel.converter.data.datatype.BasicDatatype"%>
<%@page import="static eu.citadel.liferay.portlet.converter.controller.ContrChooseExport.*"%>
<%@page import="static eu.citadel.liferay.extendedmvc.ExtMVCPortlet.*"%>
<%@page import="static eu.citadel.liferay.portlet.converter.ConverterPortlet.*"%>
<%@include file="../init.jsp"%>


<div class="view-header"><liferay-ui:message key="choose-export"/></div>
<div class="view-info"><liferay-ui:message key="choose-export-info"/></div>
<liferay-portlet:actionURL var="nextUrl" name="chooseExport_nextStep"/>
<aui:form action="${nextUrl}" method="POST" name="frmCheckBox">
	<aui:column columnWidth="100">
		<aui:column>
			<aui:select name="<%= CONTR_PARAM_SELECTED_TYPE %>" label="choose-export-select-format">
				<% 
					BasicDatatype selectedValue = (BasicDatatype)			renderRequest.getAttribute(VIEW_ATTRIBUTE_SELECTED_TYPE); 
					@SuppressWarnings("unchecked")
					List<BasicDatatype> typeList= (List<BasicDatatype>) 	renderRequest.getAttribute(VIEW_ATTRIBUTE_TYPE_LIST); 
					for(BasicDatatype s : typeList){
						
				%>
					<aui:option label="<%= s.getName(locale) %>" value="<%= typeList.indexOf(s) %>" selected="<%= s.equals(selectedValue != null ? selectedValue.getName() : \"\") %>"/>
				<% } %>
			</aui:select>
		</aui:column>
	</aui:column>
	<liferay-portlet:actionURL var="previousUrl" 	name="chooseExport_previousStep"/>
	<liferay-portlet:actionURL var="cancelUrl" 		name="chooseExport_cancel"/>

	<aui:button-row  cssClass="citadel-navigation-menu">
		<aui:button cssClass="btn-primary" value="navigation-cancel" 	href="${cancelUrl}"	/>
		<aui:button cssClass="btn-primary" value="navigation-back" 	 	href="${previousUrl}"	/>
		<aui:button cssClass="btn-primary" value="navigation-next" 		type="submit" 	  	/>
		<aui:button cssClass="btn-primary" value="navigation-end" 	disabled="true"	  	/>
	</aui:button-row>
</aui:form>
