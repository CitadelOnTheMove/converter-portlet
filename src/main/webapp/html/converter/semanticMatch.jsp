<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="eu.citadel.converter.localization.Messages"%>
<%@page import="com.liferay.portal.kernel.messaging.Message"%>
<%@page import="com.liferay.portal.kernel.dao.search.RowChecker"%>
<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@page import="static eu.citadel.liferay.portlet.converter.controller.ContrSemanticMatch.*"%>
<%@include file="../init.jsp"%>


<div class="view-header"><liferay-ui:message key="semantic-match"/></div>
	<div class="view-info"><liferay-ui:message key="semantic-match-info"/></div>

<liferay-portlet:actionURL var="nextUrl" name="semanticMatch_nextStep"/>
<aui:layout>
	<aui:column columnWidth="100">
		<aui:form action="${nextUrl}" method="POST" name="frmCategory">
			<aui:column columnWidth="70">
				<liferay-ui:search-container emptyResultsMessage="semantic-match-no-colum" >
					<liferay-ui:search-container-results results="${results}" total="${total}" />		
				  	<liferay-ui:search-container-row className="eu.citadel.liferay.portlet.dto.MetadataDto" modelVar="dto" keyProperty="id" >
				     	<liferay-ui:search-container-column-text name="semantic-match-source-column"	value="<%= StringUtils.abbreviate(dto.getName()   , ConverterConstants.MAX_TEXT_LENGTH) %>" />
				     	<liferay-ui:search-container-column-text name="semantic-match-example"			value="<%= dto.getExample() != null ? StringUtils.abbreviate(dto.getExample(), ConverterConstants.MAX_TEXT_LENGTH) : \"\" %>"	/>
				     	<liferay-ui:search-container-column-text name="semantic-match-category"		cssClass="<%= \"categoryClass category_\" +dto.getId()	%>">
					     	<input type="hidden" name="<portlet:namespace/><%= CATEGORY_PARAM_PREFIX + dto.getId() %>" class="input-text-category"/>
				     	</liferay-ui:search-container-column-text>
<!--  TODO: Riattivare quando supportato -->
<%-- 				     	<liferay-ui:search-container-column-text name="semantic-match-context"		cssClass="<%= \"contextClass context_\" +dto.getId()	%>"> --%>
<%-- 					     	<input type="hidden" name="<portlet:namespace/><%=CONTEXT_PARAM_PREFIX  + dto.getId()%>" class="input-text-context"/> --%>
<%-- 				     	</liferay-ui:search-container-column-text> --%>
				  	</liferay-ui:search-container-row>
				  	<liferay-ui:search-iterator paginate="false"/>
				</liferay-ui:search-container>
			</aui:column>
			<aui:column columnWidth="30">
				<aui:column columnWidth="100">
					<div id="categoryTree"></div>
				</aui:column>
				<aui:column columnWidth="100">
					<div id="contextTree"></div>
				</aui:column>
			</aui:column>
			<liferay-portlet:actionURL var="previousUrl" 	name="semanticMatch_previousStep"/>
			<liferay-portlet:actionURL var="cancelUrl" 		name="semanticMatch_cancel"/>

			<aui:button-row cssClass="citadel-navigation-menu">
				<aui:button cssClass="btn-primary" value="navigation-cancel" 	href="${cancelUrl}"	/>
				<aui:button cssClass="btn-primary" value="navigation-back" 	 	href="${previousUrl}"	/>
				<aui:button cssClass="btn-primary" value="navigation-next" 		name="submitBtn" 	 	/>
				<aui:button cssClass="btn-primary" value="navigation-end" 	    disabled="true"	 	/>
			</aui:button-row>
		</aui:form>
	</aui:column>
</aui:layout>

<div class="yui3-skin-sam">
    <div id="modal"></div>
</div>
<aui:script use="draggable-tree, aui-modal">

AUI().use(
  'aui-modal',
  function(A) {
	var treeLeft = 0;
	AUI().on("scroll", "resize",function(e){
		var frmTop = document.getElementById('<portlet:namespace/>frmCategory').getBoundingClientRect().top;
		if (frmTop < 30) {			
			if(A.one('#categoryTree').getStyle('position') == 'inherit'){
			 	treeLeft = document.getElementById('categoryTree').getBoundingClientRect().left;
			}

			A.one('#categoryTree').setStyles({
		        maxHeight: (AUI().one('#<portlet:namespace/>frmCategory').height() + frmTop - 30 - 45) + 'px' <!-- -45 is a correction value -->
			});

			A.one('#categoryTree').setStyles({
			    position: 'fixed',
			    left: treeLeft +'px',
			    top: '30px'
			});
		} else {
			A.one('#categoryTree').setStyles({
				maxHeight: A.one('#categoryTree') + 'px'
			});
			A.one('#categoryTree').setStyles({
			    position: 'inherit',
			    right: 'inherit',
			    top: 'inherit'
			});
		};
	});
  	
    var modal = new A.Modal(
      {
        bodyContent: '<liferay-ui:message key="semantic-match-alert-message"/>',
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
            	A.one('#<portlet:namespace/>frmCategory').submit();
            }
          }
        }
      ]
    );
	modal.render();
	//css bug fix
	A.one('#modal .toolbar-content.yui3-widget.component.toolbar').hide();
    modal.hide();

	A.one('#<portlet:namespace/>submitBtn').on('click', function(){
		var countNull=0;
		A.all('.input-text-category').each(
			function(){
				if(A.one(this).val()==""){
					countNull++;
				}
			}
		);
		if(countNull > 0 ){
			A.one('#modal .yui3-widget-bd.modal-body').text(countNull + " <%= StringEscapeUtils.unescapeHtml(LanguageUtil.get(pageContext, "semantic-match-alert-message")) %>");
			modal.show();
		}else{
        	A.one('#<portlet:namespace/>frmCategory').submit();
		}
		
	});

  }
);

var categoryTreeChild = [ {
	children : [ 
	<%
		@SuppressWarnings("unchecked")
		Map<String, String> catMap = (Map<String, String>) request.getAttribute(VIEW_PARAM_CATEGORY_MAP);
		for(Entry<String, String> entry : catMap.entrySet()){
%>
		{
			id : '<%= entry.getKey() %>',
			label : '<%= StringUtils.abbreviate(Messages.getString(entry.getValue(), locale), ConverterConstants.MAX_TEXT_LENGTH) %>',
			leaf : true,
		} ,
	<%
	}
%>
	 ],
	expanded : true,
	label : '<liferay-ui:message key="semantic-match-category"/>'
} ];
<!-- TODO: Riattivare quando supportato
var contextTreeChild = [ {
	children : [ 
	<%
		@SuppressWarnings("unchecked")
		Map<String, String> contMap = (Map<String, String>) request.getAttribute(VIEW_PARAM_CONTEXT_MAP);
		for(Entry<String, String> entry : contMap.entrySet()){
%>
		{
			id : '<%= entry.getKey() %>',
			label : '<%= StringUtils.abbreviate(Messages.getString(entry.getValue(), locale), ConverterConstants.MAX_TEXT_LENGTH) %>',
			leaf : true,
		} ,
	<%
	}
%>
	 ],
	expanded : true,
	label : '<liferay-ui:message key="semantic-match-context"/>'
} ];
 -->
  new Liferay.Portlet.DraggableTree({
	  context : '<%=CATEGORY_PARAM_PREFIX%>',
	  treeSelector : '#categoryTree',
	  dropNodeSelector : '.categoryClass',
	  inputTextSelector : '.input-text-category',
	  treeChild : categoryTreeChild,
	  onlyOneDrop : true,
  }).render();
<!-- TODO: Riattivare quando supportato
  new Liferay.Portlet.DraggableTree({
	  context : '<%=CONTEXT_PARAM_PREFIX%>',
	  treeSelector : '#contextTree',
	  dropNodeSelector : '.contextClass',
	  inputTextSelector : '.input-text-context',
	  treeChild : contextTreeChild,
	  onlyOneDrop : false
  }).render();
-->
</aui:script>




