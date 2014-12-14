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

	
	<div class="yui3-skin-sam">
	    <div id='modal'>
	    </div>
	</div>
	
	<aui:script use="draggable-tree, aui-modal">
	
	AUI().use(
	  'aui-modal',
	  function(A) {
	  	
	    var modal = new A.Modal(
	      {
	        bodyContent: '<liferay-ui:message key="choose-export-back-alert-message"/>',
	        centered: true,
	        destroyOnHide: false,
	        headerContent: '<h3><liferay-ui:message key="message-warning"/></h3>',
	        height: 200,
	        modal: true,
	        render: '#modal',
	        visible: true,
			draggable: false,
	        width: 400,
	        toolbars: {
	          body: [
	            {
	              icon: 'icon-file',
	              label: ''
	            },
	            {
	              icon: 'icon-book',
	              label: ''
	            }
	          ]
	        },
	      }
	    );
	
	    modal.addToolbar(
	      [
	        {
	          label: '<liferay-ui:message key="message-cancel"/>',
	          on: {
	            click: function() {
	              modal.hide();
	            }
	          }
	        },
	        {
	          label: '<liferay-ui:message key="message-ok"/>',
	          on: {
	            click: function() {
	                window.location.href = '${previousUrl}';
	            }
	          }
	        }
	      ]
	    );
		modal.render();
		
		A.one('#modal .toolbar-content.yui3-widget.component.toolbar').hide();
	    modal.hide();
	
		A.one('#<portlet:namespace/>btnBack').on('click', function(){
			modal.show();
		});
	
	  }
	);
	
	</aui:script>


	<aui:button-row  cssClass="citadel-navigation-menu">
		<aui:button cssClass="btn-primary" value="navigation-cancel" 	href="${cancelUrl}"	/>
		<aui:button cssClass="btn-primary" value="navigation-back" 	 	name="btnBack"/>
		<aui:button cssClass="btn-primary" value="navigation-next" 		type="submit" 	  	/>
		<aui:button cssClass="btn-primary" value="navigation-end" 	disabled="true"	  	/>
	</aui:button-row>
</aui:form>
