package eu.citadel.liferay.portlet.converter.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.util.ParamUtil;

import eu.citadel.converter.data.dataset.CsvDataset;
import eu.citadel.converter.data.dataset.CsvDatasetContentBuilder;
import eu.citadel.converter.data.dataset.CsvType;
import eu.citadel.converter.data.dataset.DatasetStatus;
import eu.citadel.converter.data.dataset.DatasetType;
import eu.citadel.converter.data.dataset.ExcelDataset;
import eu.citadel.converter.data.dataset.ExcelDatasetContentBuilder;
import eu.citadel.converter.data.dataset.ExcelType;
import eu.citadel.converter.data.metadata.BasicMetadataUtils;
import eu.citadel.converter.exceptions.DatasetException;
import eu.citadel.converter.exceptions.ExcelDatasetException;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.converter.ConverterPortlet;
import eu.citadel.liferay.portlet.converter.general.ConverterController;
import eu.citadel.liferay.portlet.dto.DatasetDto;

/**
 * @author ttrapanese
 */
/*Step 3*/
public class ContrInputDetail extends ConverterController {
	//VIEW PATH
	private static final String JSP_CSV_PATH 				= "/html/converter/inputDetailCSV.jsp";
	private static final String JSP_XLS_PATH 				= "/html/converter/inputDetailXLS.jsp";
	private static final String JSP_UNSUPPORTED_PATH 		= "/html/converter/inputDetailUnsupported.jsp";
	
	//VIEW KEY
	public static final String VIEW_XLS_KEY 				= "view_xls_key";
	public static final String VIEW_CSV_KEY 				= "view_csv_key";
	public static final String VIEW_UNSUPPORTET_KEY 		= "view_unsupportet_key";

	//TO PAGE ATTRIBUTE	
	public static String PAGE_ATTRIBUTE_HEADER_LIST 		= "page_attribute_header_list";
	//FILTER SETTINGS
	public static String PAGE_ATTRIBUTE_ITEM_NUMBER_LIST	= "page_attribute_item_number_list";
	public static String PAGE_ATTRIBUTE_SHEET_MAP 			= "page_attribute_sheet_list";
	public static String PAGE_ATTRIBUTE_DELIMITER_MAP		= "page_attribute_delimiter_map";
	
	public static String CONTR_PARAM_FIRST_ROW_HEADER		= "contr_param_first_row_header";
	public static String CONTR_PARAM_ITEM_NUMBER			= "contr_param_item_number";
	public static String CONTR_PARAM_SHEET		 			= "contr_param_sheet";
	public static String CONTR_PARAM_DELIMITER	 			= "contr_param_delimiter";
	
	//FROM PAGE PARAMS
	public static String PAGE_ATTRIBUTE_FIRST_ROW_HEADER_VAL= "page_attribute_first_row_header_val";
	public static String PAGE_ATTRIBUTE_ITEM_NUMBER_VAL		= "page_attribute_item_number_val";
	public static String PAGE_ATTRIBUTE_SHEET_VAL		 	= "page_attribute_sheet_val";
	public static String PAGE_ATTRIBUTE_DELIMITER_VAL	 	= "page_attribute_delimiter_val";
	
	//DEFAULT VALUE
	public static Boolean DEFAULT_FIRST_ROW_HEADER			= true;
	public static Integer DEFAULT_ITEM_NUMBER				= 5;
	
	public static Integer DEFAULT_SHEET						= 0;
	public static String  DEFAULT_DELIMITER					= CsvType.DEL_SEMICOLON;

	
	@Override
	public ExtViewResult doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		DatasetDto ds = getDatasetDto(renderRequest);
		if(ds.getDataset().getType().equals(DatasetType.TYPE_EXCEL)) {
			return doXLSdView(ds, renderRequest, renderResponse);
		} else if (ds.getDataset().getType().equals(DatasetType.TYPE_CSV)) {
			return doCSVdView(ds, renderRequest, renderResponse);
		}
		return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL, VIEW_UNSUPPORTET_KEY);
	}
	
	private ExtViewResult doXLSdView(DatasetDto ds, RenderRequest request, RenderResponse renderResponse) throws IOException, PortletException {
		getLog(request).debug("doXLSdView start");
		//Get settings from page
		Boolean firstRowHeader 	= ParamUtil.getBoolean	(request, CONTR_PARAM_FIRST_ROW_HEADER	, DEFAULT_FIRST_ROW_HEADER);
		Integer itemNumber 		= ParamUtil.getInteger	(request, CONTR_PARAM_ITEM_NUMBER		, DEFAULT_ITEM_NUMBER);
		Integer sheet 			= ParamUtil.getInteger	(request, CONTR_PARAM_SHEET				, DEFAULT_SHEET);

		getLog(request).debug("xls firstRowHeader: " 	+ String.valueOf(firstRowHeader));
		getLog(request).debug("xls itemNumber: "	 	+ String.valueOf(itemNumber));
		getLog(request).debug("xls sheet: " 			+ String.valueOf(sheet));

		Map<Integer, String> sheetMap;
		ExcelDataset dataset = null;

		ExcelType excelType = new ExcelType(sheet);
		((ExcelDataset)getDatasetDto(request).getDataset()).setExcelType(excelType);
		
		try {
			if(ds.getFileEntry() != null) { 
				dataset = new ExcelDataset(ds.getFileEntry().toPath());
			} else {
				dataset = new ExcelDataset(ds.getUrl());
			}
			dataset.setExcelType(excelType);
			dataset.buildContent();
			sheetMap = dataset.getSheetMap();
		} catch (ExcelDatasetException e) {
			getLog(request).error("error: " + e.getLocalizedMessage(getLocale(request)));
			getLog(request).debug("doXLSdView end");
			return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL, VIEW_UNSUPPORTET_KEY);
		}

		
		ExcelDatasetContentBuilder cb = new ExcelDatasetContentBuilder();
		try { 
			cb.setPath((Path) dataset.getInternalStateObject(DatasetStatus.STATUS_PATH));
		} catch (DatasetException e) {
			try {
				cb.setPath((Path) dataset.getInternalStateObject(DatasetStatus.STATUS_TEMPPATH));
			} catch (DatasetException e1) {
				getLog(request).error("error: " + e1.getLocalizedMessage(getLocale(request)));
				getLog(request).debug("doXLSdView end");
				return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL, VIEW_UNSUPPORTET_KEY);
			}
		}
		
		cb.setLines(itemNumber + (firstRowHeader ? 1 : 0));
		cb.setExcelType(excelType);
		List<List<Object>> list = null;
		try {
			list = cb.build();
		} catch (ExcelDatasetException e) {
			getLog(request).error("error: " + e.getLocalizedMessage(getLocale(request)));
			getLog(request).debug("doXLSdView end");
			return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL, VIEW_UNSUPPORTET_KEY);
		}

		if(firstRowHeader && list.size() > 0){
			request.setAttribute(PAGE_ATTRIBUTE_HEADER_LIST, list.get(0));
			setResult(request, list.subList(1, list.size()));
		}else{
			setResult(request, list);
		}
		//sent filter value to page
		request.setAttribute(PAGE_ATTRIBUTE_FIRST_ROW_HEADER_VAL	, firstRowHeader);
		request.setAttribute(PAGE_ATTRIBUTE_ITEM_NUMBER_VAL			, itemNumber );
		request.setAttribute(PAGE_ATTRIBUTE_SHEET_VAL				, sheet);

		request.setAttribute(PAGE_ATTRIBUTE_SHEET_MAP				, sheetMap);
		getLog(request).debug("doXLSdView end");
		return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL, VIEW_XLS_KEY);
	}

	private ExtViewResult doCSVdView(DatasetDto ds, RenderRequest request, RenderResponse renderResponse) throws IOException, PortletException {
		getLog(request).debug("doCSVdView start");
		//Get settings from page
		Boolean firstRowHeader 	= ParamUtil.getBoolean	(request, CONTR_PARAM_FIRST_ROW_HEADER	, DEFAULT_FIRST_ROW_HEADER);
		Integer itemNumber 		= ParamUtil.getInteger	(request, CONTR_PARAM_ITEM_NUMBER		, DEFAULT_ITEM_NUMBER);
		String  delimiter		= ParamUtil.getString	(request, CONTR_PARAM_DELIMITER			, DEFAULT_DELIMITER);
		getLog(request).debug("csv firstRowHeader: " 	+ String.valueOf(firstRowHeader));
		getLog(request).debug("csv itemNumber: "	 	+ String.valueOf(itemNumber));
		getLog(request).debug("csv delimiter: " 		+ delimiter);

		CsvDataset dataset = null;
		if(ds.getFileEntry() != null) { 
			dataset = new CsvDataset(ds.getFileEntry().toPath());
		} else {
			dataset = new CsvDataset(ds.getUrl());
		}

		CsvType csvType = new CsvType(CsvType.QUOTE_DQUOTE, delimiter, CsvType.EOL_RN);
		((CsvDataset)getDatasetDto(request).getDataset()).setCsvType(csvType);

		dataset.setCsvType(csvType);
		dataset.buildContent();
		
		CsvDatasetContentBuilder cb = new CsvDatasetContentBuilder();
		try { 
			cb.setPath((Path) dataset.getInternalStateObject(DatasetStatus.STATUS_PATH));
		} catch (DatasetException e) {
			try {
				cb.setPath((Path) dataset.getInternalStateObject(DatasetStatus.STATUS_TEMPPATH));
			} catch (DatasetException e1) {
				getLog(request).error("error: " + e1.getLocalizedMessage(getLocale(request)));
				getLog(request).debug("doCSVdView end");
				return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL, VIEW_UNSUPPORTET_KEY);
			}
		}
		cb.setLines(itemNumber + (firstRowHeader ? 1 : 0));
		cb.setCsvType(csvType);

		List<List<String>> list = cb.build();

		if(firstRowHeader && list.size() > 0){
			request.setAttribute(PAGE_ATTRIBUTE_HEADER_LIST, list.get(0));
			if(list.size() > (itemNumber +1)){
				setResult(request, list.subList(1, itemNumber + 1));
			} else {
				setResult(request, list.subList(1, list.size()));
			}
		}else{
			setResult(request, list);
		}
		//sent filter value to page
		request.setAttribute(PAGE_ATTRIBUTE_FIRST_ROW_HEADER_VAL	, firstRowHeader);
		request.setAttribute(PAGE_ATTRIBUTE_ITEM_NUMBER_VAL			, itemNumber);
		request.setAttribute(PAGE_ATTRIBUTE_DELIMITER_VAL			, delimiter);

		request.setAttribute(PAGE_ATTRIBUTE_DELIMITER_MAP		, BasicMetadataUtils.getMap(BasicMetadataUtils.CSV_DELIMITER));
		getLog(request).debug("doCSVView end");
		return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL, VIEW_CSV_KEY);
	}

	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		if(viewKey == null) return JSP_UNSUPPORTED_PATH;
		switch (viewKey) {
		case VIEW_XLS_KEY:
			return JSP_XLS_PATH;
		case VIEW_CSV_KEY:
			return JSP_CSV_PATH;
		default:
			return JSP_UNSUPPORTED_PATH;
		}
	}
	@Override
	public ExtViewResult nextStep(ActionRequest request, ActionResponse response) {
		Boolean firstRowHeader 		= ParamUtil.getBoolean	(request, CONTR_PARAM_FIRST_ROW_HEADER	, DEFAULT_FIRST_ROW_HEADER);
		Integer itemNumber 			= ParamUtil.getInteger	(request, CONTR_PARAM_ITEM_NUMBER		, DEFAULT_ITEM_NUMBER);
		Integer sheet 				= ParamUtil.getInteger	(request, CONTR_PARAM_SHEET				, DEFAULT_SHEET);
		String  delimiter			= ParamUtil.getString	(request, CONTR_PARAM_DELIMITER			, DEFAULT_DELIMITER);
	
		DatasetDto ds 				= getDatasetDto(request);
		ds.setDelimiter				(delimiter);
		ds.setNewline				(CsvType.EOL_RN);
		ds.setQuote					(CsvType.QUOTE_DQUOTE);
		ds.setDelimiter				(delimiter);
		ds.setFirstRowHeader		(firstRowHeader);
		ds.setSheetNumber			(sheet);
		ds.setItemNumber			(itemNumber);
		setDatasetDto(request, ds);
		return new ExtViewResult(ConverterPortlet.CONTR_SEMANTIC_MATCH);
	}

	@Override
	public ExtViewResult previousStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		return new ExtViewResult(ConverterPortlet.CONTR_CHOOSE_DATA);
	}	
}
