<%@page import="eu.citadel.converter.io.index.CitadelCityInfo"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@page import="static eu.citadel.liferay.portlet.converter.controller.ContrSaveFileCitadel.*"%>
<%@include file="../../init.jsp"%>

<liferay-portlet:actionURL var="previousUrl" 	name="saveFileCitadel_previousStep"/>
<liferay-portlet:actionURL var="nextUrl" 		name="saveFileCitadel_nextStep"/>
<liferay-portlet:actionURL var="cancelUrl" 		name="saveFileCitadel_cancel"/>
<liferay-portlet:actionURL var="publishUrl" 	name="saveFileCitadel_publish"/>

<%
String downloadLink 	= (String)  renderRequest.getAttribute(VIEW_ATTRIBUTE_DOWNLOAD_LINK);
String err 				= (String)  renderRequest.getAttribute(VIEW_ATTRIBUTE_MESSAGE_ERROR);
String errKey			= (String)  renderRequest.getAttribute(VIEW_ATTRIBUTE_KEY_ERROR);
String errLink			= (String)  renderRequest.getAttribute(VIEW_ATTRIBUTE_LINK_ERROR);
boolean enablePublish 	= (Boolean) renderRequest.getAttribute(VIEW_ATTRIBUTE_ENABLE_PUBLISH);
if(Validator.isNull(err)){
%>	
	<liferay-ui:success key="success" message="success"/>

	<liferay-portlet:resourceURL id="saveFileCitadel_download" var="downloadUrl">
		<liferay-portlet:param name="<%= PAGE_PARAM_DOWNLOAD_LINK %>" value="<%= downloadLink %>"/>
	</liferay-portlet:resourceURL>

	<div class="view-header"><liferay-ui:message key="save-file-preview-message"/></div>
	<div class="view-info"><liferay-ui:message key="save-file-citadel-info"/></div>
	<pre class="code"><%= renderRequest.getAttribute(VIEW_ATTRIBUTE_OBJECT_PREVIEW) %></pre>
	<aui:button-row>
		<%String linkPrev = HtmlUtil.escapeHREF("http://demos.citadelonthemove.eu/app-generator/index.php?preview=true&converterdatasetID=" + downloadLink);%>
		<aui:button type="button" cssClass="btn-primary" value="save-file-preview-btn" onClick="<%= \"window.open('\" + linkPrev + \"');\" %>"	/>
		<liferay-ui:message key="save-file-if-you-can-see-your-data-properly-you-can"/>
		<aui:button cssClass="btn-primary" value="save-file-download-file" 	    href="<%= downloadUrl %>"		/>
	</aui:button-row>
	<aui:button-row>
		<liferay-ui:message key="save-file-and-then"/>
		<aui:button type="button" cssClass="btn-primary" value="save-file-publish"	name="btnPublish" disabled="<%= !enablePublish %>"/>
		<%String link = HtmlUtil.escapeHREF("http://www.citadelonthemove.eu/en-us/opendata/uploadmydataset.aspx");%>
		(<liferay-ui:message key="save-file-publish-sub"/><a href="<%= link %>"><liferay-ui:message key="save-file-publish-sub-here"/></a>)
	</aui:button-row>
	

<div class="yui3-skin-sam">
    <div id="modal"></div>
</div>

<%
	if(enablePublish) {
		@SuppressWarnings("unchecked")
		Map<String, String>  languageMap 	= (Map<String, String>)   request.getAttribute(VIEW_ATTRIBUTE_LANGUAGE_MAP);
		@SuppressWarnings("unchecked")
		Map<Integer, String> typeMap 		= (Map<Integer, String>)  request.getAttribute(VIEW_ATTRIBUTE_TYPE_MAP);
		@SuppressWarnings("unchecked")
		List<CitadelCityInfo> locationList 	= (List<CitadelCityInfo>) request.getAttribute(VIEW_ATTRIBUTE_LOCATION_LIST);
		@SuppressWarnings("unchecked")
		List<String> licenceList 			= (List<String>) request.getAttribute(VIEW_ATTRIBUTE_LICENCE_LIST);
%>
		
		<div class="hidden">
		    <div id="modalPublish">
				<div class="view-info"><liferay-ui:message key="save-file-citadel-popup-info"/></div>
		    	<aui:form action="<%= publishUrl %>" method="POST">
					<aui:input label="save-file-title" 			type="text"	name="<%= CONTR_PARAM_TITLE 		%>"	inlineField="true">
						<aui:validator name="required"/>
					</aui:input>
					<aui:input label="save-file-description"	type="text"	name="<%= CONTR_PARAM_DESCRIPTION 	%>"	inlineField="true"/>
					<aui:select label="save-file-location" 					name="<%= CONTR_PARAM_LOCATION		%>" inlineField="true">
					<% for(CitadelCityInfo loc : locationList) { %>
						<aui:option label="<%= loc.getName() %>" value="<%= String.valueOf(loc.getId()) %>"/>
					<% } %>
					</aui:select>
					<aui:select label="save-file-type" 						name="<%= CONTR_PARAM_TYPE 			%>"	inlineField="true">
					<% for(Entry<Integer, String> loc : typeMap.entrySet()) { %>
						<aui:option label="<%= \"save-file-type-\" + loc.getValue() %>" value="<%= String.valueOf(loc.getKey()) %>"/>
					<% } %>
					</aui:select>
					<aui:select label="save-file-language" 		type="text"	name="<%= CONTR_PARAM_LANGUAGE %>" 		inlineField="true">
					<% for(Entry<String, String> lang : languageMap.entrySet()) { %>
						<aui:option label="<%= lang.getValue() %>" value="<%= lang.getKey() %>"/>
					<% } %>
					</aui:select>
		
					<aui:input label="save-file-publisher" 		type="text"	name="<%= CONTR_PARAM_PUBLISHER 	%>"	inlineField="true">
						<aui:validator name="required"/>
					</aui:input>
					<aui:input label="save-file-source" 		type="text"	name="<%= CONTR_PARAM_SOURCE 		%>"	inlineField="true">
						<aui:validator name="required"/>
					</aui:input>
					<aui:select label="save-file-license" 		type="text"	name="<%= CONTR_PARAM_LICENSE %>" 		inlineField="true">
					<% for(String lic : licenceList) { %>
						<aui:option label="<%= lic %>" value="<%= lic %>"/>
					<% } %>
					</aui:select>
					<aui:button-row>
						<aui:button type="button" primary="true" value="save-file-publish-cancel"	name="btnCancelPublish"/>
						<aui:button type="submit" primary="true" value="save-file-publish-submit"	name="btnSubmitPublish"/>
					</aui:button-row>
		    	</aui:form>
		    </div>
		</div>
		
		<aui:script use="aui-modal">
			  var modalPublish = new A.Modal(
		      {
		        bodyContent: A.one('#modalPublish'),
		        centered: true,
		        destroyOnHide: false,
		        headerContent: '<liferay-ui:message key="save-file-citadel-publish"/>',
		        height: 450,
		        modal: true,
		        render: '#modal',
		        visible: true,
				draggable: false,
		        width: 500,
		        }
		    );
		   	modalPublish.render();
		
			//css bug fix
			A.one('#modal .toolbar-content.yui3-widget.component.toolbar').hide();
		
		    modalPublish.hide();
		    
		    AUI().one('#<portlet:namespace/>btnCancelPublish').on('click', function(){
				modalPublish.hide();
				return false;
		    });
		    
		    AUI().one('#<portlet:namespace/>btnPublish').on('click', function(){
				modalPublish.show();
				return false;
		    });
		</aui:script>
	
<%
	}
}else{
%>
	<div class="alert alert-error"><a style="cursor: pointer;" onclick="<%= "window.open('" + errLink + "');" %>"><%= errKey %></a>:<%= err.replace(". ", ".<br/>") %></div>
<%
}
%>

<aui:button-row cssClass="citadel-navigation-menu">
	<aui:button cssClass="btn-primary" value="navigation-cancel" 	href="${cancelUrl}"		/>
	<aui:button cssClass="btn-primary" value="navigation-back" 	 	href="${previousUrl}"	/>
	<aui:button cssClass="btn-primary" value="navigation-next" 		href="<%= Validator.isNull(err) ? nextUrl : \"\"%>" disabled="<%= !Validator.isNull(err) %>"	/>
	<aui:button cssClass="btn-primary" value="navigation-end" 	    disabled="true"	/>
</aui:button-row>