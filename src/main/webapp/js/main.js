var my_tree;

AUI().add('draggable-tree', function(A) {
 // some component's variables
 
var CONTEXT = 'context',
 TREE_SELECTOR = 'treeSelector',
 DROP_NODE_SELECTOR = 'dropNodeSelector',
 INPUT_TEXT_SELECTOR = 'inputTextSelector',
 TREE_CHILD = 'treeChild',
 ONLY_ONE_DROP = 'onlyOneDrop',
 INSERT_ELEMENT = 'insertElement',
 GET_NODE_TEXT = 'getNodeText',
 VALID_NODE = 'validNode',
 ON_DRAG_END = 'onDragEnd',
 ON_REFRESH_END = 'onRefreshEnd';
 // component DraggableTree will be created at namespace
 // Liferay.Portlet

 Liferay.Portlet.DraggableTree = A.Component.create({
	  // component name
	  NAME : 'draggable-tree',
	 
	  EXTENDS : A.Component,
	 
	  // Component's attributes, something similar to constructor
	  ATTRS : {
		  context : {},
		  treeSelector : {},
		  dropNodeSelector : {},
		  inputTextSelector : {},
		  treeChild : {},
		  onlyOneDrop : {},
		  insertElement : {},
		  getNodeText : {},
		  validNode : {},
		  onDragEnd : {},
		  onRefreshEnd : {}
	  },
	 
	  // Base component's method which extends
	  prototype : {
	   bindUI : function() {
	    var instance = this;
	    var tree = instance._init(this);	
	    my_tree = tree;
	   },
	   _updateInputText :  function(that, container){
		   var inputText = container.one(that.get(INPUT_TEXT_SELECTOR) );
		   inputText.val('');
		   container.all('.'+that.get(CONTEXT)+'contextLabel').each(function(node) {
			   var nodeText = node.attr(that.get(CONTEXT) + '_id');
			   if(that.get(GET_NODE_TEXT) != null){
				   nodeText = that.get(GET_NODE_TEXT)(inputText, nodeText, node);
			   }
			   inputText.val(inputText.val() + '#' + nodeText);

		   });
		   if(that.get(ON_REFRESH_END) != null)
			   that.get(ON_REFRESH_END)(container);
	   },
	   _getDefaultIsValidNode : function(that, dropNode, id, allElemNum, elemNum){
		   var valid = (elemNum == 0);
		   return valid && (( that.get(ONLY_ONE_DROP) && allElemNum == 0) || (! that.get(ONLY_ONE_DROP)));
	   },
	   _getDefaultNewElement : function(that, container, id, label){
		   var newPortlet = A.Node.create('<span class="'+that.get(CONTEXT)+'contextDiv"><span class="'+that.get(CONTEXT)+'contextLabel '+that.get(CONTEXT)+'contextLabel_'
				   + id
				   + '" ' + that.get(CONTEXT) + '_id="' + id + '" >'
				   + label
				   + '</span><span class="'+that.get(CONTEXT)+'contextRemove">x</span></span>');

		   newPortlet.one('.'+that.get(CONTEXT)+'contextRemove').on('click', function(e){
			   newPortlet.remove();
			   that._updateInputText(that, container);
		   });
		   return newPortlet;
	   },

	   _getNewElement : function(that, container, id, label){
		   if(that.get(INSERT_ELEMENT) == null){
			   return that._getDefaultNewElement(that, container, id, label);
		   }else{
			   return that.get(INSERT_ELEMENT)(that, that.get(CONTEXT),container, id, label);
		   }
	   },
	   
	   // Init method
	   _init : function(instanceDT) {
		   var tree = new A.TreeView({
				boundingBox : instanceDT.get(TREE_SELECTOR),
				children : instanceDT.get(TREE_CHILD),
			}).render();

		   var proxyNode = A.Node.create('<div class="sortable-layout-proxy"></div>');
		   var DDM = A.DD.DDM;

		   // Create new constructor for Portlet adding widget
		   var PortletItem = function() {
			   PortletItem.superclass.constructor.apply(this,	arguments);
		   };

		   PortletItem.NAME = 'PortletItem';
		   PortletItem.ATTRS = {
				   dd : {
					   value : false
				   },
				   delegateConfig : {
					   value : {
						   nodes : instanceDT.get(TREE_SELECTOR)+' .tree-node-leaf',
						   target : false
					   }
				   },
				   itemContainer : {
					   value : instanceDT.get(TREE_SELECTOR)+' .tree-node-content'
				   }	    	    		
   	    		
   	    		

		   };
		   new A.SortableLayout({
			   dragNodes : instanceDT.get(TREE_SELECTOR) + ' .tree-node-leaf',
			   dropNodes : instanceDT.get(DROP_NODE_SELECTOR),
			   placeholder: A.Node.create('<div/>')
		   });

				    
		   // Extend widget to clone itself when dragged
		   var elementNode = '';
		   var elementId = '';
		   A.extend(PortletItem, A.SortableLayout, {
			   _getAppendNode : function() {
				   var instance = this;
				   elementId = DDM.activeDrag.get('node').get('parentNode').attr('id');
				   instance.appendNode = DDM.activeDrag.get('node').clone();
				   elementNode = instance.appendNode.one('.tree-label');
				   return instance.appendNode;
			   },
			   _defPlaceholderAlign : function(event) {
				   var instance = this;
				   var activeDrop = instance.activeDrop;

				   if (activeDrop) {
//					   var node = activeDrop.get('node');
//					   node.addClass('foo-test');
					   instance.lastAlignDrop = activeDrop;
				   }
			   },
		   });
		
		   var portletList = new PortletItem();
		
		   portletList.on(
				   'drag:end',
				   function(event) {
					   var instance = this;
					   var activeDrop = instance.lastAlignDrop || instance.activeDrop;
		 
					   var tdCategory = portletList.appendNode.get('parentNode');
					   var catTagList = tdCategory.all('.'+instanceDT.get(CONTEXT)+'contextLabel_'+elementNode.text());
					   var elemNum = catTagList.size();
		
					   var allElemNum = tdCategory.all('.'+instanceDT.get(CONTEXT)+'contextLabel').size();
		
					   var newPortlet = instanceDT._getNewElement(instanceDT, tdCategory, elementId, elementNode.text());
					
					   // this condition is for a bug that i don't know where came from 
					   var valid = activeDrop.get('node').ancestors(instanceDT.get(DROP_NODE_SELECTOR)).size() > 0;

					   //le altre per evitare i duplicati 
					   if(instanceDT.get(VALID_NODE) != null){
						   valid = valid && instanceDT.get(VALID_NODE)(instanceDT, activeDrop.get('node'), elementId, allElemNum, elemNum);
					   }else{
						   //default valid condition
						   valid = valid && instanceDT._getDefaultIsValidNode(instanceDT, activeDrop.get('node'), elementId, allElemNum, elemNum);
					   }
					   
					   if(valid){
						   if (portletList.appendNode && portletList.appendNode.inDoc()) {
							   portletList.appendNode.replace(newPortlet);
						   }
						   
						   instanceDT._updateInputText(instanceDT, tdCategory);
						   if(instanceDT.get(ON_DRAG_END) != null)
							   instanceDT.get(ON_DRAG_END)(activeDrop.get('node'), elementId);
					   }else{
						   portletList.appendNode.remove();
					   }
				   }
		   );
		   
		   
		   
		    A.later(1000, null, function() {
			    A.all(instanceDT.get(INPUT_TEXT_SELECTOR)).each(function(node){
			       	var text = A.one(node);
			    	var arr = text.val().split('#');
			    	if(arr != null){
			    		for (var i=0; i<arr.length; i++){
			    	    	if(arr[i] != "" && arr[i].indexOf("{plain_text}") == -1){
			    	    		var container = text.get('parentNode');
			    	    		
			    	    		console.log('li#'+arr[i]+' .tree-label');
//			    	    		var label='';
//			    	    		tree.getChildren().forEach(function(e){
//			    	    			e.getChildren().forEach(
//			    	    					function(e){
//			    	    						if(e.getAttrs().id == arr[i])
//			    	    							label = e.getAttrs().label;
//			    	    						console.log(e.getAttrs().label);
//		    	    						});
//		    	    			});
//			    	    		
			    	    		
			    	    		
			    	    		
			    	    		var label = A.one('li#'+arr[i]+' .tree-label').text();
			    	    		var newEl = instanceDT._getNewElement(instanceDT, container, arr[i], label);
			    	    		container.append(newEl);
			    	    	}
			    		};
			    	}
			    });
		   
		    });
		   
		   
		   
		   
		   return tree;
	   },
	  }});
 }, '1.0', {
 // component's dependency
 requires : [ 'aui-tree-view', 'datatype-xml', 'dataschema-xml',	
              'liferay-portlet-url', 'aui-io-request', 'aui-node', 
              'aui-tabs', 'aui-sortable-layout' ]
 
});