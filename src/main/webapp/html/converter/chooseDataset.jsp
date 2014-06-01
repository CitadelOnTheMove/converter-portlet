<%@page import="eu.citadel.liferay.tag.MyRowChecker"%>
<%@page import="eu.citadel.liferay.portlet.commons.ConverterUtils"%>
<%@page import="com.liferay.portal.kernel.dao.search.RowChecker"%>
<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@include file="../init.jsp"%>


<div class="view-header"><liferay-ui:message key="choose-dataset-to-convert"/></div>
<div class="view-info"><liferay-ui:message key="choose-dataset-info"/></div>
<liferay-portlet:actionURL var="nextUrl" 		name="chooseData_nextStep"/>
<liferay-portlet:actionURL var="previousUrl" 	name="chooseData_previousStep"/>
<liferay-portlet:actionURL var="cancelUrl" 		name="chooseData_cancel"/>

<aui:form action="${nextUrl}" method="POST" name="frmCheckBox">
	<liferay-ui:search-container emptyResultsMessage="choose-dataset-no-files-selected" rowChecker="<%= new MyRowChecker(renderResponse) %>" >
		<liferay-ui:search-container-results results="${results}" total="${total}" />		
	  <liferay-ui:search-container-row className="java.io.File" modelVar="file" keyProperty="name" >
	     <liferay-ui:search-container-column-text name="select-source-file-name"	value="<%= file.getName() %>" 						/>
	     <liferay-ui:search-container-column-text name="select-source-extension"	value="<%= ConverterUtils.getFileExt(file) %>"	align="center"	/>
	     <liferay-ui:search-container-column-text name="select-source-file-size"	value="<%= ConverterUtils.readableFileSize(file.length()) %>"	align="center"	/>
	  </liferay-ui:search-container-row>
	  	<liferay-ui:search-iterator paginate="false" />
	</liferay-ui:search-container>
	
	<aui:button-row cssClass="citadel-navigation-menu">
		<aui:button cssClass="btn-primary" value="navigation-cancel" href="${cancelUrl}"	/>
		<aui:button cssClass="btn-primary" value="navigation-back" 	 href="${previousUrl}"	/>
		<aui:button cssClass="btn-primary" value="navigation-next" 	 disabled="true"	name="btn-next"/>
		<aui:button cssClass="btn-primary" value="navigation-end" 	 disabled="true"	  	/>
	</aui:button-row>
</aui:form>

<aui:script use="aui-base">
AUI().ready(function(A){
	A.one('#<portlet:namespace/>btn-next').on('click', function(){
		console.log('click');
		if(!A.one(this).hasClass('disabled')){
			A.one('#<portlet:namespace/>frmCheckBox').submit();
		}
	})
	A.all('#<portlet:namespace/>frmCheckBox input:checkbox').on('change', function(e){
		console.log('change');
		console.log(e);
		//////////////////////////////////////////////////////////////
		//temporally disable multi-selection until app support it
		if(A.one(e.target).attr('checked')){
			A.all('#<portlet:namespace/>frmCheckBox input:checkbox:checked').attr('checked', false);
			A.one(e.target).attr('checked',true);
		}
		//////////////////////////////////////////////////////////////
		var checkedNum = A.all('#<portlet:namespace/>frmCheckBox input:checkbox:checked').size();
		console.log('change: ' + checkedNum);

		if(checkedNum > 0)	{
			A.one('#<portlet:namespace/>btn-next').removeClass('disabled');
			A.one('#<portlet:namespace/>btn-next').attr("disabled", false);
		}else{
			A.one('#<portlet:namespace/>btn-next').addClass('disabled');
			A.one('#<portlet:namespace/>btn-next').attr("disabled", true);
		}
	})
	
})
</aui:script>
