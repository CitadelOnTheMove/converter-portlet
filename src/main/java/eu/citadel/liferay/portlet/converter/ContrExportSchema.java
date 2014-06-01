package eu.citadel.liferay.portlet.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import eu.citadel.converter.data.datatype.BasicDatatype;
import eu.citadel.converter.data.metadata.BasicMetadata;
import eu.citadel.converter.exceptions.ConverterException;
import eu.citadel.converter.schema.obj.BasicSchemaObjAbstractValue;
import eu.citadel.converter.schema.obj.BasicSchemaObjAttributes;
import eu.citadel.converter.schema.obj.BasicSchemaObjElements;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueInteger;
import eu.citadel.converter.utils.Matching;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.ConverterUtils;
import eu.citadel.liferay.portlet.commons.JsonBuilder;
import eu.citadel.liferay.portlet.dto.MetadataDto;
import eu.citadel.liferay.portlet.dto.TransformationDto;

/**
 * @author ttrapanese
 */
/*Step 6*/
public class ContrExportSchema extends ConverterController {
	@SuppressWarnings("unused")
	private static Log _log = ConverterPortlet.getLogger();
	//View path
	private static final String JSP_MAIN_PATH 						= "/html/converter/exportSchema.jsp";

	public static final String VIEW_ATTRIBUTE_SCHEMA_VALUE_LIST 	= "view_attribute_type_list";

	public static final String VIEW_PARAM_SOURCE_COLUMN_LIST		= "view_param_source_column_list";
	public static final String SOURCE_COLUMN_PREFIX					= "sourceColumn-";
		
	@Override
	public ExtViewResult doView(RenderRequest request, RenderResponse renderResponse) throws IOException, PortletException {
		ArrayList<TransformationDto> ret = new ArrayList<TransformationDto>();
		BasicDatatype selectedExport = getSelectedBasicDatatype(request);
//		DEBUG: BasicDatatype selectedExport = BasicDatatype.getCitadelJson();
		BasicSchemaObjElements values = selectedExport.getValues();
		for (Entry<BasicSchemaObjAbstractValue<?>, BasicSchemaObjAttributes> entry : values.entrySet()) {
			TransformationDto newT = new TransformationDto(entry, request.getLocale()); 
			if(newT.getId() != null)
				ret.add(newT);
		}	
		setResult(request, ret);
		setTransformationDto(request, ret);
		autoMatch(request);
		
		List<MetadataDto> list = getMetadataDto(request);
		request.setAttribute(VIEW_PARAM_SOURCE_COLUMN_LIST, ConverterUtils.getTargetMetadata(list));
//		DEBUG: renderRequest.setAttribute(VIEW_PARAM_SOURCE_COLUMN_LIST, BasicMetadataUtils.getList(BasicMetadataUtils.CATEGORY));
		return new ExtViewResult(ConverterPortlet.CONTR_EXPORT_SCHEMA);
	}
	
	private void autoMatch(PortletRequest request){
		BasicMetadata metadata = JsonBuilder.metadataBuild(getMetadataDto(request), getDatasetDto(request));
		try {
			Map<Integer, List<Integer>> normMatch = new HashMap<Integer, List<Integer>>();
			Map<BasicSchemaObjAbstractValue<?>, BasicSchemaObjAbstractValue<?>> match = Matching.getSingleMatch(getSelectedBasicDatatype(request), metadata);

			for (Entry<BasicSchemaObjAbstractValue<?>, BasicSchemaObjAbstractValue<?>> entry : match.entrySet()) {
				int transId = ((BasicSchemaObjValueInteger)entry.getKey()).getValue();
				int colId   = ((BasicSchemaObjValueInteger)entry.getValue()).getValue();
				if(normMatch.containsKey(transId)){
					normMatch.get(transId).add(colId);
				}else{
					List<Integer> arr = new LinkedList<Integer>();
					arr.add(colId);
					normMatch.put(transId, arr);
				}

			}
			List<TransformationDto> list = getTransformationDto(request);		
			for (TransformationDto el : list) {
				if(normMatch.containsKey(el.getId())){
					for (int cont : normMatch.get(el.getId())) {
						el.addContent(String.valueOf(cont));
					}
				}
			}
		} catch (ConverterException e) {
			// Do nothing
		}
	}
	
	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return JSP_MAIN_PATH;
	}

	@Override
	public ExtViewResult nextStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		List<TransformationDto> list = getTransformationDto(actionRequest);		
		for (TransformationDto el : list) {
			String content = ParamUtil.getString(actionRequest, SOURCE_COLUMN_PREFIX + el.getId());
			el.setContent(new LinkedList<String>());
			String[] contList = content.split("#");
			for (String c : contList) {
				if(Validator.isNotNull(c))
					el.addContent(c);
			}
		}
		return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE);
	} 

	@Override
	public ExtViewResult previousStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		//TEMPORANEO per il momento salto export schema
		return new ExtViewResult(ConverterPortlet.CONTR_SEMANTIC_MATCH);
//		return new ExtViewResult(ConverterPortlet.CONTR_CHOOSE_EXPORT);
	}	
}
