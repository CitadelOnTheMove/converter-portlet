<%@page import="eu.citadel.liferay.portlet.commons.ConverterUtils"%>
<%@page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@include file="../init.jsp"%>


<div class="view-header"><liferay-ui:message key="select-source-dataset"/></div>
<div class="view-info"><liferay-ui:message key="select-source-info"/></div>

<portlet:actionURL var="submitFileAction" name="sourceData_submitFile"/>
<portlet:actionURL var="submitFileAndConvertAction" name="sourceData_submitFileAndConvert"/>
<portlet:actionURL var="submitUrlAndConvertAction" name="sourceData_submitUrlAndConvert"/>
<!-- 	TEMPORANEO --> 
<script type="text/javascript" charset="utf-8">
function <portlet:namespace/>submitForm(action){
  if(action==0){
	     AUI().one('#<portlet:namespace/>form').set('action',"<%= submitFileAction %>");
	     AUI().one('#<portlet:namespace/>form').set('enctype',"multipart/form-data");
  }else if(action==1){
		 AUI().one('#<portlet:namespace/>form').set('action',"<%= submitFileAndConvertAction %>");
	     AUI().one('#<portlet:namespace/>form').set('enctype',"multipart/form-data");
  }else {
		 AUI().one('#<portlet:namespace/>form').set('action',"<%= submitUrlAndConvertAction %>");
  }
  AUI().one('#<portlet:namespace/>form').submit();
}
</script>



<form id="<portlet:namespace/>form" method="post" >
	<aui:fieldset>
		<aui:column columnWidth="100">
			<aui:column>
					<input type="file" id="file" name='<portlet:namespace />file' size="50" />
					<aui:button cssClass="btn-primary" value="select-source-submit" onClick="<%= renderResponse.getNamespace() + \"submitForm(0)\"%>"  />
					<!-- 	TEMPORANEO -->
					<aui:button cssClass="btn-primary" value="select-and-convert" 	onClick="<%= renderResponse.getNamespace() + \"submitForm(1)\"%>"/>
			</aui:column>
			<aui:column>
					<liferay-ui:message key="select-source-or-insert-url"/>
					<input type="text" id="fileUrl" name='<portlet:namespace />fileUrl' size="50" />
					<aui:button cssClass="btn-primary" value="select-url-and-convert" 	onClick="<%= renderResponse.getNamespace() + \"submitForm(2)\"%>"/>
			</aui:column>
		</aui:column>
	</aui:fieldset>
</form>


<liferay-ui:search-container emptyResultsMessage="select-source-no-files-selected" delta="5" >
	<liferay-ui:search-container-results results="${results}" total="${total}" />		
  <liferay-ui:search-container-row className="java.io.File" modelVar="file" keyProperty="name" >
     <liferay-ui:search-container-column-text name="select-source-file-name"	value="<%= file.getName() %>" 						/>
     <liferay-ui:search-container-column-text name="select-source-extension"	value="<%= ConverterUtils.getFileExt(file) %>"	align="center"	/>
     <liferay-ui:search-container-column-text name="select-source-file-size"	value="<%= ConverterUtils.readableFileSize(file.length()) %>"	align="center"	/>
  </liferay-ui:search-container-row>
  <liferay-ui:search-iterator paginate="false" />
</liferay-ui:search-container>

<liferay-portlet:renderURL var="nextUrl">
	<liferay-portlet:param name="<%= ExtMVCPortlet.MVC_REQUEST_PARAM %>" value="<%= ConverterPortlet.CONTR_CHOOSE_DATA %>"/>
</liferay-portlet:renderURL>
<%
	@SuppressWarnings("unchecked")
	List<File> fileList = ((List<File>)request.getAttribute(SearchContainer.DEFAULT_RESULTS_VAR));
%>
<portlet:actionURL var="submitFileAction" name="sourceData_cancel"/>
<aui:button-row cssClass="citadel-navigation-menu">
	<aui:button cssClass="btn-primary" value="navigation-cancel" disabled="true"	  	/>
	<aui:button cssClass="btn-primary" value="navigation-back" 	 disabled="true"	  	/>
	<aui:button cssClass="btn-primary" value="navigation-next" 	 href="${nextUrl}" disabled="<%= fileList == null || fileList.size() == 0 %>"/>
	<aui:button cssClass="btn-primary" value="navigation-end" 	 disabled="true"	  	/>
</aui:button-row>

