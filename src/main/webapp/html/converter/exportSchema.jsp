<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="eu.citadel.converter.localization.Messages"%>
<%@page import="eu.citadel.liferay.portlet.commons.ConverterConstants"%>
<%@page import="eu.citadel.liferay.portlet.dto.MetadataDto"%>
<%@page import="eu.citadel.liferay.portlet.dto.TransformationDto.formatElement"%>
<%@page import="com.liferay.portal.kernel.dao.search.RowChecker"%>
<%@page import="eu.citadel.liferay.portlet.converter.ConverterPortlet"%>
<%@page import="eu.citadel.liferay.extendedmvc.ExtMVCPortlet"%>
<%@page import="static eu.citadel.liferay.portlet.converter.controller.ContrExportSchema.*"%>
<%@page import="static eu.citadel.liferay.portlet.commons.ConverterConstants.*"%>

<%@include file="../init.jsp"%>


<div class="view-header"><liferay-ui:message key="export-schema"/></div>
<div class="view-info"><liferay-ui:message key="export-schema-info"/></div>
<liferay-portlet:actionURL var="nextUrl" name="exportSchema_nextStep"/>
<aui:layout>
	<aui:column columnWidth="100">
		<aui:form action="${nextUrl}" method="POST" name="frmCategory" >
			<aui:column columnWidth="70">
				<liferay-ui:search-container emptyResultsMessage="export-schema-no-colum" id="export-schema-search-container" >
					<liferay-ui:search-container-results results="${results}" total="${total}" />		
				  	<liferay-ui:search-container-row className="eu.citadel.liferay.portlet.dto.TransformationDto" modelVar="dto" keyProperty="id" escapedModel="true">
				     	<liferay-ui:search-container-column-text name="export-schema-property"	align="center">
				     	<% if(dto.isMandatory()) {%>
							<liferay-ui:icon src="${renderRequest.contextPath}/images/icons/mandatory.png" cssClass="required_icon" message="tooltip-icon-mandatory-true"/>
				     	<% }%>
				     	</liferay-ui:search-container-column-text>
				     	<liferay-ui:search-container-column-text name="export-schema-target-field"		value="<%= dto.getName() != null ? StringEscapeUtils.escapeHtml(Messages.getString(dto.getName(), locale)) : \"\" %>" title="<%= dto.getDescription() != null ? HtmlUtil.escape(dto.getDescription()) : \"\" %>"			/>
				     	<liferay-ui:search-container-column-text name="export-schema-content"			cssClass="contentClass">	
				     		<%if(dto.getFormat() != null){
									List<formatElement> list = dto.getFormat();
									%>
									<aui:select name="<%= SOURCE_COLUMN_PREFIX + dto.getId() %>" label="" showEmptyOption="<%= !dto.isMandatory() %>">
									<% for(formatElement el : list){ %>			 	    	
										<aui:option label="<%= Messages.getString(el.getLabel(), locale)  %>" value="<%= AUI_KEY_PLAIN_TEXT + el.getValue() %>"/>
							 		<%	} %>
									</aui:select>
						    <% } %>			     		     	
		     		     	<input type="hidden" 
		     		     		name="<portlet:namespace/><%= SOURCE_COLUMN_PREFIX + dto.getId() %>" 
		     		     		class="input-text-content" 
		     		     		alt='<liferay-ui:message key="tooltip-icon-mandatory-true"/>'
		     		     		value='<%= dto.getFormat() == null ? dto.getContentString() : ""%>'/>
				     	</liferay-ui:search-container-column-text>
				     	<liferay-ui:search-container-column-text name="export-schema-status" align="center">
				     	<% if(dto.isMandatory()) {%>
					     	<% if(dto.getFormat() == null) {%>
								<liferay-ui:icon src="${renderRequest.contextPath}/images/icons/ok.png"    cssClass="valid_icon hide"   message="tooltip-icon-status-ok"   />
								<liferay-ui:icon src="${renderRequest.contextPath}/images/icons/error.png" cssClass="invalid_icon hide" message="tooltip-icon-status-error"/>
					     	<% }else{%>
								<liferay-ui:icon src="${renderRequest.contextPath}/images/icons/ok.png"    cssClass="valid_icon"   message="tooltip-icon-status-ok"   />
					     	<% }%>
				     	<% }else{%>
								<liferay-ui:icon src="${renderRequest.contextPath}/images/icons/ok.png"    cssClass="valid_icon hide"   message="tooltip-icon-status-ok"   />
								<liferay-ui:icon src="${renderRequest.contextPath}/images/icons/warning.png" cssClass="warning_icon hide" message="tooltip-icon-status-warning"/>
				     	<% }%>
				     	</liferay-ui:search-container-column-text>
				  	</liferay-ui:search-container-row>
				  	<liferay-ui:search-iterator paginate="false"/>
				</liferay-ui:search-container>
			</aui:column>
			<aui:column columnWidth="30">
				<div id="contentTree"></div>
			</aui:column>
			<liferay-portlet:actionURL var="previousUrl" 	name="exportSchema_previousStep"/>
			<liferay-portlet:actionURL var="cancelUrl" 		name="exportSchema_cancel"/>
			
			<aui:button-row cssClass="citadel-navigation-menu">
				<aui:button cssClass="btn-primary" value="navigation-cancel" 	href="${cancelUrl}"		/>
				<aui:button cssClass="btn-primary" value="navigation-back" 	 	href="${previousUrl}"	/>
				<aui:button cssClass="btn-primary" value="navigation-next" 		name="next-button" 		/>
				<aui:button cssClass="btn-primary" value="navigation-end" 	    disabled="true"	 		/>
			</aui:button-row>
		</aui:form>
	</aui:column>
</aui:layout>

<div class="yui3-skin-sam">
    <div id="modal"></div>
</div>

<aui:script use="event-valuechange,draggable-tree,aui-modal">
var MapCategory = {};
<% 
@SuppressWarnings("unchecked")
List<MetadataDto> dtoList = (List<MetadataDto>) request.getAttribute(VIEW_PARAM_SOURCE_COLUMN_LIST);
for(MetadataDto c : dtoList){ 
%>
MapCategory['<%= c.getName() %>'] = '<%= StringEscapeUtils.unescapeHtml(c.getCategoryString(locale)) %>';
<% } %>


function appendTitle(){
	AUI().all('.tree-label').each(function(){ 
		var catKey = this.text(); 
	    if (MapCategory.hasOwnProperty(catKey)) {
			this.attr('title', MapCategory[catKey]);
	    }
	})
}

function checkValidity(row){
	  var validNode    = row.one('.valid_icon');
	  var invalidNode  = row.one('.invalid_icon');
	  var warningNode  = row.one('.warning_icon');
	  var inputText	   = row.one('.input-text-content');
	  if(inputText != null && validNode != null && (invalidNode != null || warningNode != null)){
		  var inputTextVal = inputText.val();
		  if(inputTextVal == ""){
			validNode.hide();
			if(invalidNode != null)	invalidNode.show();
			if(warningNode != null)	warningNode.show();
		}else{
			validNode.show();
			if(invalidNode != null)	invalidNode.hide();
			if(warningNode != null)	warningNode.hide();
		}			  
	  }
}


AUI().ready( function(A){
	var treeLeft = 0;
	AUI().on("scroll",function(e){
		var frmTop = document.getElementById('<portlet:namespace/>frmCategory').getBoundingClientRect().top;
		if (frmTop < 30) {			
			if(A.one('#contentTree').getStyle('position') == 'inherit'){
			 	treeLeft = document.getElementById('contentTree').getBoundingClientRect().left;
			}

			A.one('#contentTree').setStyles({
		        maxHeight: (AUI().one('#<portlet:namespace/>frmCategory').height() + frmTop - 30 - 45) + 'px' <!-- -45 is a correction value -->
			});

			A.one('#contentTree').setStyles({
			    position: 'fixed',
			    left: treeLeft +'px',
			    top: '30px'
			});
		} else {
			A.one('#contentTree').setStyles({
				maxHeight: A.one('#contentTree') + 'px'
			});
			A.one('#contentTree').setStyles({
			    position: 'inherit',
			    right: 'inherit',
			    top: 'inherit'
			});
		};
	});



	    var modal = new A.Modal(
      {
        bodyContent: '<liferay-ui:message key="export-schema-missing-fields"/>',
        centered: true,
        destroyOnHide: false,
        headerContent: '<h3><liferay-ui:message key="message-error"/></h3>',
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
          label: '<liferay-ui:message key="message-ok"/>',
          on: {
            click: function() {
            	modal.hide();
            }
          }
        }
      ]
    );
	modal.render();
	//css bug fix
	A.one('#modal .toolbar-content.yui3-widget.component.toolbar').hide();
    modal.hide();
	


	A.one('#<portlet:namespace/>next-button').on('click', function(){
		var errNum = A.all('.invalid_icon').size()-A.all('.invalid_icon:hidden').size();
		if(errNum == 0){
			A.one('#<portlet:namespace/>frmCategory').submit();
		}else{
			A.one('#modal .yui3-widget-bd.modal-body').text(errNum + ' <liferay-ui:message key="export-schema-missing-fields"/>');
			modal.show();
		}
	});
	A.all('#<portlet:namespace/>export-schema-search-container tr').each(function(){
		checkValidity(A.one(this));
	});
	A.all('.contentClass select').on('change', function(e){
		var selVal   = e.currentTarget.val();
		var input = e.currentTarget.get('parentNode').get('parentNode').one('.input-text-content');
		input.val(selVal);
		checkValidity(e.currentTarget.get('parentNode').get('parentNode').get('parentNode'));
	});
	A.all('.contentClass select').each(function(e){
		var selVal   = this.val();
		var input = this.get('parentNode').get('parentNode').one('.input-text-content');
		input.val(selVal);
	});

});

var contentTreeChild = [ {
	children : [ {
		children : [ 
			<% 
			for(MetadataDto c : dtoList){ 
			%>
			{
				id : '<%= c.getId() %>',
				label : '<%= StringUtils.abbreviate(Messages.getString(c.getNameAndCategory(), locale), ConverterConstants.MAX_TEXT_LENGTH) %>',
				leaf : true,
			} ,
			<% } %>
	 	],
		expanded : true,
		label : '<liferay-ui:message key="export-schema-source-column"/>',
		leaf : false}, {
		children : [ 
			{
				id : 'custom-text',
				label : '<liferay-ui:message key="export-schema-custom-text"/>',
				leaf : true,
			} ,
	 	],
		expanded : true,
		label : '<liferay-ui:message key="export-schema-extra"/>',
		leaf : false,
	} ],
 
	expanded : true,
	label : '<liferay-ui:message key="export-schema-content"/>'
} ];

  new Liferay.Portlet.DraggableTree({
	  context : '<%= SOURCE_COLUMN_PREFIX %>',
	  treeSelector : '#contentTree',
	   dropNodeSelector : '.contentClass',
	  inputTextSelector : '.input-text-content',
	  treeChild : contentTreeChild,
	  onlyOneDrop : false,
	  insertElement : function(that, context, container, id, label){
		  if(id=='custom-text'){
		  		if(label == "Custom text") label = "";
				var newPortlet = AUI().Node.create('<span class="'+context+'contextDiv"><input type="text" id="'+context+'_id" class="'+context+'contextLabel inputPlainText '+context+'contextLabel_' + id + '" ' + context + '_id="' + id + '"' + ' value="' + label  + '" /><span class="'+context+'contextRemove">x</span></span>');
				newPortlet.one('.'+context+'contextRemove').on('click', function(e){
					   newPortlet.remove();
					   that._updateInputText(that, container);
				});
				newPortlet.one('.inputPlainText').on('keyup', function(e){
					that._updateInputText(that, container);
				});
				return newPortlet;
		  } else {
				return that._getDefaultNewElement(that, container, id, label);
		  }
	  	},
		getNodeText :function(inputText, id, node){
			  if(id=='custom-text'){
				  return "<%= AUI_KEY_PLAIN_TEXT %>" + node.val();
			  }else{
				  return id;
			  }
		},
		  validNode : function(that, dropNode, id, allElemNum, elemNum) {
		  	  if(dropNode.one('select') != null) return false;
			  if(id=='custom-text'){
				  return true;
			  } else {
				  return that._getDefaultIsValidNode(that, dropNode, id, allElemNum, elemNum);
			  }
		  },
		  onRefreshEnd : function(container){
			  //Update Status icon when refresh hidden input
			  var row		   = container.get('parentNode');
			  checkValidity(row);
		  	}
  }).render();
   setTimeout(function () {
		appendTitle();
	}, 3000);

</aui:script>



