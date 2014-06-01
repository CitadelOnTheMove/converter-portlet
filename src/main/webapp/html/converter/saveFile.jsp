<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@page import="static eu.citadel.liferay.portlet.converter.ContrSaveFile.*"%>
<%@include file="../init.jsp"%>


<liferay-portlet:actionURL var="finishUrl" 		name="saveFile_finish"/>
<liferay-portlet:actionURL var="previousUrl" 	name="saveFile_previousStep"/>
<liferay-portlet:actionURL var="cancelUrl" 		name="saveFile_cancel"/>


<%
String downloadLink = (String) renderRequest.getAttribute(VIEW_ATTRIBUTE_DOWNLOAD_LINK);
String err 			= (String) renderRequest.getAttribute(VIEW_ATTRIBUTE_MESSAGE_ERROR);
if(Validator.isNull(err)){
%>	
	<liferay-portlet:resourceURL id="saveFile_download" var="downloadUrl">
		<liferay-portlet:param name="<%= PAGE_PARAM_DOWNLOAD_LINK %>" value="<%= downloadLink %>"/>
	</liferay-portlet:resourceURL>

	<div class="view-header"><liferay-ui:message key="save-file-preview-message"/></div>
	<div class="view-info"><liferay-ui:message key="save-file-info"/></div>
	<pre class="code"><%= renderRequest.getAttribute(VIEW_ATTRIBUTE_OBJECT_PREVIEW) %></pre>
	<aui:button-row>
		<aui:button cssClass="btn-primary" value="save-file-download-file" 	    href="<%= downloadUrl %>"		/>
		<liferay-ui:message key="save-file-and-then"/>
		<%String link = HtmlUtil.escapeHREF("http://www.citadelonthemove.eu/en-us/opendata/uploadmydataset.aspx");%>
		<aui:button type="button" cssClass="btn-primary" value="save-file-publish" onClick="<%= \"window.open('\" + link + \"');\" %>"	/>
		<liferay-ui:message key="save-file-or-see-a-preview"/>
		<%String linkPrev = HtmlUtil.escapeHREF("http://demos.citadelonthemove.eu/app-generator/index.php?preview=true&converterdatasetID=" + downloadLink);%>
		<aui:button type="button" cssClass="btn-primary" value="save-file-preview-btn" onClick="<%= \"window.open('\" + linkPrev + \"');\" %>"	/>
	</aui:button-row>
<%
}else{
%>
	<div class="alert alert-error"><%= err.replace(". ", ".<br/>") %></div>
<%
}
%>

<aui:button-row cssClass="citadel-navigation-menu">
	<aui:button cssClass="btn-primary" value="navigation-cancel" 	href="${cancelUrl}"		/>
	<aui:button cssClass="btn-primary" value="navigation-back" 	 	href="${previousUrl}"	/>
	<aui:button cssClass="btn-primary" value="navigation-next" 		disabled="true"			/>
	<aui:button cssClass="btn-primary" value="navigation-end" 	    href="${finishUrl}"	disabled="<%= Validator.isNotNull(err) %>"	/>
</aui:button-row>