package eu.citadel.liferay.portlet.converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import eu.citadel.converter.data.dataset.CsvDatasetContentBuilder;
import eu.citadel.converter.data.dataset.CsvType;
import eu.citadel.converter.data.dataset.DatasetType;
import eu.citadel.converter.data.dataset.ExcelDatasetContentBuilder;
import eu.citadel.converter.data.dataset.ExcelType;
import eu.citadel.converter.data.datatype.BasicDatatype;
import eu.citadel.converter.data.metadata.BasicMetadataUtils;
import eu.citadel.converter.exceptions.ExcelDatasetException;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.dto.DatasetDto;
import eu.citadel.liferay.portlet.dto.MetadataDto;

/**
 * @author ttrapanese
 */
/*Step 4*/
public class ContrSemanticMatch extends ConverterController {
	private static Log _log = ConverterPortlet.getLogger();
	private static final String JSP_MAIN_PATH 				= "/html/converter/semanticMatch.jsp";

	public static final String CONTR_PARAM_SELECTED_FILES 	= "contr_param_selected_files";

	public static final String VIEW_PARAM_CATEGORY_MAP 		= "view_param_category_list";
	public static final String VIEW_PARAM_CONTEXT_MAP 		= "view_param_context_list";
	
	public static final String CATEGORY_PARAM_PREFIX 		= "category-";
	public static final String CONTEXT_PARAM_PREFIX 		= "context-";

	public static final String VIEW_UNSUPPORTET_KEY 		= "view_unsupportet_key";

	
	@Override
	public ExtViewResult doView(RenderRequest request, RenderResponse renderResponse) throws IOException, PortletException {
		DatasetDto ds = getDatasetDto(request);
		List<MetadataDto> dtoList;
		try {
			dtoList = getList(request, ds);
		} catch (ExcelDatasetException e) {
			_log.error("session: " + request.getPortletSession().getId() +" error: " + e.getLocalizedMessage(getLocale(request)));
			return new ExtViewResult(ConverterPortlet.CONTR_SEMANTIC_MATCH);
		}
		setMetadataDto(request, dtoList);

		setResult(request, dtoList);
		setTotal(request, -1);
	
		request.setAttribute(VIEW_PARAM_CATEGORY_MAP, BasicMetadataUtils.getMap(BasicMetadataUtils.CATEGORY));
		request.setAttribute(VIEW_PARAM_CONTEXT_MAP , BasicMetadataUtils.getMap(BasicMetadataUtils.CONTEXT));
		return new ExtViewResult(ConverterPortlet.CONTR_SEMANTIC_MATCH);
	}

	private List<MetadataDto> getList(PortletRequest request, DatasetDto ds) throws ExcelDatasetException, IOException {
		List<MetadataDto> ret = new ArrayList<MetadataDto>();
		if(ds.getDataset().getType().equals(DatasetType.TYPE_EXCEL)) {
			ExcelDatasetContentBuilder cb = new ExcelDatasetContentBuilder();
			cb.setPath(new File(ds.getFile()).toPath());
			cb.setLines(ds.getItemNumber());
			ExcelType excelType = new ExcelType(ds.getSheetNumber());
			cb.setExcelType(excelType);

			List<List<Object>> list;
			list = cb.build();
			List<Object> lstFirst   = list.get(0);
			List<Object> lstSecond  = list.get(1);
			for (int i = 0; i < lstFirst.size(); i++) {
				Object header = null, example = null;
				try{
					if(ds.isFirstRowHeader()){
						header  = lstFirst.get(i);
						example =  lstSecond.get(i);
					}else{
						header  =  String.valueOf(i);
						example =  lstFirst.get(i);
					}
				}catch(IndexOutOfBoundsException e){			
					_log.debug("session: " + request.getPortletSession().getId() +" error: " + e.getMessage());
				}
				if(header == null) header = "";
				if(example == null) example = "";
				ret.add(new MetadataDto(i, header.toString(), example.toString()));
			}
		} else if (ds.getDataset().getType().equals(DatasetType.TYPE_CSV)) {		
			CsvDatasetContentBuilder cb = new CsvDatasetContentBuilder();
			cb.setPath(new File(ds.getFile()).toPath());
			cb.setLines(ds.getItemNumber());
			CsvType csvType = null;
			if (ds.getDelimiter().equals(",")) {
				csvType = CsvType.CSV_DQUOTE_COMMA_RN;
			}else{
				csvType = CsvType.CSV_DQUOTE_SEMICOLON_RN;
			}
			cb.setCsvType(csvType);

			List<List<String>> list = cb.build();
			List<String> lstFirst   = list.get(0);
			List<String> lstSecond  = list.get(1);
			for (int i = 0; i < lstFirst.size(); i++) {
				if(ds.isFirstRowHeader()){
					ret.add(new MetadataDto(i, lstFirst.get(i), lstSecond.get(i)));
				}else{
					ret.add(new MetadataDto(i, String.valueOf(i), lstFirst.get(i)));
				}
			}
		}
		return ret;
	}
	
	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return JSP_MAIN_PATH;
	}
	@Override
	public ExtViewResult nextStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		List<MetadataDto> list = getMetadataDto(actionRequest);
		for (MetadataDto el : list) {
			String category = ParamUtil.getString(actionRequest, CATEGORY_PARAM_PREFIX + el.getId());
			if(Validator.isNotNull(category))
				category = category.substring(1); //Delete initail # from javascript 
			String context  = ParamUtil.getString(actionRequest, CONTEXT_PARAM_PREFIX  + el.getId());
			el.setCategory(category); 
			String[] contList = context.split("#");
			for (String c : contList) {
				if(Validator.isNotNull(c))
					el.addContext(c);
			}
		}
		
		//TEMPORANEO per il momento salto choose export
		setSelectedBasicDatatype(actionRequest, BasicDatatype.getAvailableBasicDatatype().get(0));
		_log.debug("session: " + actionRequest.getPortletSession().getId() +" selected export type: " + 0);
		return new ExtViewResult(ConverterPortlet.CONTR_EXPORT_SCHEMA);
		
		
		
//		return new ExtViewResult(ConverterPortlet.CONTR_CHOOSE_EXPORT);
	}

	@Override
	public ExtViewResult previousStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL);
	}
}
