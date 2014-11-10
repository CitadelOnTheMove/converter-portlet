<%@page import="eu.citadel.converter.data.metadata.BasicMetadataUtils"%>
<%@page import="eu.citadel.converter.localization.Messages"%>
<%@page import="eu.citadel.liferay.portlet.converter.controller.ContrSemanticMatch"%>
<%@page import="eu.citadel.liferay.portlet.converter.controller.ContrInputDetail"%>
<%@page import="eu.citadel.liferay.portlet.converter.controller.ContrChooseDataset"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCListController"%>
<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@page import="static eu.citadel.liferay.portlet.converter.controller.ContrInputDetail.*"%>
<%@include file="../init.jsp"%>

<%
	@SuppressWarnings("unchecked")
 	Map<Integer, String> sheetList 	= (Map<Integer, String>)  renderRequest.getAttribute(PAGE_ATTRIBUTE_SHEET_MAP);

	Boolean firstRow 	= (Boolean) renderRequest.getAttribute(PAGE_ATTRIBUTE_FIRST_ROW_HEADER_VAL	);
	int 	itemNumber	= (Integer) renderRequest.getAttribute(PAGE_ATTRIBUTE_ITEM_NUMBER_VAL		);
	int		sheet		= (Integer) renderRequest.getAttribute(PAGE_ATTRIBUTE_SHEET_VAL				);
	String 	selectedFile= (String)  renderRequest.getParameter(ContrChooseDataset.CONTR_PARAM_SELECTED_FILES);
%>
<liferay-portlet:renderURL var="filterUrl">
	<liferay-portlet:param name="<%= ExtMVCPortlet.MVC_REQUEST_PARAM %>" value="<%= ConverterPortlet.CONTR_INPUT_DETAIL %>"/>
	<liferay-portlet:param name="<%= ContrChooseDataset.CONTR_PARAM_SELECTED_FILES %>" value="<%= selectedFile %>"/>
</liferay-portlet:renderURL>
<div class="view-header"><liferay-ui:message key="input-detail"/></div>
<div class="view-info"><liferay-ui:message key="input-detail-excel-info"/></div>
<aui:layout>
<aui:column columnWidth="100">
	<aui:form action="${filterUrl}" method="POST">
			<aui:column >
				<aui:column>					
					<%
						Map<String, String> map = BasicMetadataUtils.getMap(BasicMetadataUtils.FIRST_ROW);
						String key = map.keySet().toArray(new String[1])[1];
					%>
					<div><%=Messages.getString(map.get(key), locale)%></div>
					<liferay-ui:input-checkbox param="<%= CONTR_PARAM_FIRST_ROW_HEADER %>" defaultValue="<%= firstRow %>" />
				</aui:column>
				<aui:column>
					<aui:select name="<%= CONTR_PARAM_SHEET %>" label="input-detail-excel-sheet-to-preview">
					<% 
						Set<Integer> keymap = sheetList.keySet();
						for(Integer key : keymap){
							%>
								<aui:option label="<%= sheetList.get(key) %>" value="<%= String.valueOf(key) %>" selected="<%= sheet == key %>"/>
							<%
						}							
					%>
					</aui:select>
				</aui:column>
				<aui:column>			
					<aui:select name="<%= CONTR_PARAM_ITEM_NUMBER %>" label="input-detail-excel-number-of-preview-row">
						<aui:option label="3"  value="3"  selected="<%=  3 == itemNumber %>"/>
						<aui:option label="5"  value="5"  selected="<%=  5 == itemNumber %>"/>
						<aui:option label="10" value="10" selected="<%= 10 == itemNumber %>"/>
					</aui:select>
				</aui:column>
			</aui:column>
			<aui:column >
				<aui:button type="submit" value="input-detail-filter"/>
			</aui:column>
	</aui:form>

	<aui:column columnWidth="100">
		<liferay-ui:search-container emptyResultsMessage="input-detail-excel-no-row-to-display" >
			<liferay-ui:search-container-results results="${results}" total="${total}" />		
		  	<liferay-ui:search-container-row className="java.util.List" modelVar="list" >
				<citadel:search-container-column-list list="${list}" header="${page_attribute_header_list}" lengthLimit="<%= ConverterConstants.MAX_TEXT_LENGTH %>"/>
		  	</liferay-ui:search-container-row>
		  	<liferay-ui:search-iterator paginate="false"/>
		</liferay-ui:search-container>
		
		
		
		<liferay-portlet:actionURL var="nextUrl" name="inputDetail_nextStep">
			<liferay-portlet:param name="<%= ExtMVCPortlet.MVC_REQUEST_PARAM %>" 				value="<%= ConverterPortlet.CONTR_SEMANTIC_MATCH 	%>"/>
			<liferay-portlet:param name="<%= ContrInputDetail.CONTR_PARAM_FIRST_ROW_HEADER %>" 	value="<%= String.valueOf(firstRow) 				%>"/>
			<liferay-portlet:param name="<%= ContrInputDetail.CONTR_PARAM_ITEM_NUMBER %>" 		value="<%= String.valueOf(itemNumber) 				%>"/>
			<liferay-portlet:param name="<%= ContrInputDetail.CONTR_PARAM_SHEET %>"		 		value="<%= String.valueOf(sheet) 					%>"/>
			<liferay-portlet:param name="<%= ContrChooseDataset.CONTR_PARAM_SELECTED_FILES %>"	value="<%= selectedFile								%>"/>
		</liferay-portlet:actionURL>
		<liferay-portlet:actionURL var="previousUrl" 	name="inputDetail_previousStep"/>
		<liferay-portlet:actionURL var="cancelUrl" 		name="inputDetail_cancel"/>

		<aui:button-row cssClass="citadel-navigation-menu">
			<aui:button cssClass="btn-primary" value="navigation-cancel" href="${cancelUrl}"	/>
			<aui:button cssClass="btn-primary" value="navigation-back" 	 href="${previousUrl}"	/>
			<aui:button cssClass="btn-primary" value="navigation-next" 	 href="${nextUrl}"		/>
			<aui:button cssClass="btn-primary" value="navigation-end" 	 disabled="true"	  	/>
		</aui:button-row>

	</aui:column>
</aui:column>
</aui:layout>
