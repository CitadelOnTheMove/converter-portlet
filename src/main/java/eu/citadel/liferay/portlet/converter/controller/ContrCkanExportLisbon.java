package eu.citadel.liferay.portlet.converter.controller;

import java.io.IOException;
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
import com.liferay.portal.kernel.util.Validator;

import eu.citadel.converter.data.Data;
import eu.citadel.converter.data.dataset.CsvDataset;
import eu.citadel.converter.data.dataset.Dataset;
import eu.citadel.converter.data.datatype.BasicDatatype;
import eu.citadel.converter.data.metadata.BasicMetadata;
import eu.citadel.converter.transform.MyNLisbonCaseCsvTransform;
import eu.citadel.converter.transform.Transform;
import eu.citadel.converter.transform.config.BasicTransformationConfig;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.ConverterConstants;
import eu.citadel.liferay.portlet.commons.ConverterUtils;
import eu.citadel.liferay.portlet.commons.JsonBuilder;
import eu.citadel.liferay.portlet.converter.ConverterPortlet;
import eu.citadel.liferay.portlet.converter.general.ContrCkanExportAbstract;
import eu.citadel.liferay.portlet.dto.DatasetDto;
import eu.citadel.liferay.portlet.dto.MetadataDto;
import eu.citadel.liferay.portlet.dto.TransformationDto;

/**
 * @author ttrapanese
 */
/*Step 8*/
public class ContrCkanExportLisbon extends ContrCkanExportAbstract {
	public static final String CONTR_PARAM_RESOURCE_NAME 	= "contr_param_resource_name";
	public static final String PAGE_ATTRIBUTE_RESOURCE_NAME = "page_attribute_resource_name";

	private static final String PROPERTIES_FILE				= "ckan/lisbon.properties";

	//View path
	private static final String JSP_MAIN_PATH 				= "/html/converter/ckanExport/lisbon.jsp";
	
	
	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return JSP_MAIN_PATH;
	}

	@Override
	public ExtViewResult previousStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE_LISBON);
	}

	@Override
	protected String getControllerName() {
		return ConverterPortlet.CONTR_CKAN_EXPORT_LISBON;
	}
	
	@Override
	public ExtViewResult doView(RenderRequest request, RenderResponse renderResponse) throws IOException, PortletException {
		String resName 		= ParamUtil.getString(request, CONTR_PARAM_RESOURCE_NAME);
		Map<String, String> propsMap = ConverterUtils.getPropertiesMap(getClass(), getPropertiesFilePath());
		if(Validator.isNull(resName)) resName = propsMap.get(ConverterConstants.PROPERTY_CKAN_DEFAULT_RESOURCE_NAME);
		request.setAttribute(PAGE_ATTRIBUTE_RESOURCE_NAME	, resName);
		return super.doView(request, renderResponse);
	}
	
	public ExtViewResult insert(ActionRequest request, ActionResponse response) throws IOException, PortletException {
		getLog(request).debug("Tentato hacking");
		setErrorMessage(request, "ACCESS DANIED");
		return new ExtViewResult(getControllerName());
//		String url 			= ParamUtil.getString(request, CONTR_PARAM_URL);
//		String api 			= ParamUtil.getString(request, CONTR_PARAM_API);
//		String ver 			= ParamUtil.getString(request, CONTR_PARAM_VERS);
//
//		String resName 		= ParamUtil.getString(request, CONTR_PARAM_RESOURCE_NAME);
//		List<List<String>> originalRows = null;
//		try {
//			originalRows = getRow(request);
//		} catch (Exception e) {
//			getLog(request).error(e);
//			setErrorMessage(request, e.getLocalizedMessage());
//			return new ExtViewResult(getControllerName());
//		}
//		List<String> keys = originalRows.get(0);
//		List<Map<String, Object>> rowToInsert = new ArrayList<Map<String, Object>>();
//		for (List<String> list : originalRows.subList(1, originalRows.size())) {
//			Map<String, Object> row = new HashMap<String, Object>();
//			for (int i = 0; i < list.size(); i++) {
//				row.put(keys.get(i), list.get(i));
//			}
//			rowToInsert.add(row);
//		}
//		try {
//			CKANUtils.datastoreResourceInsert(new URL(url), ver, api, resName, rowToInsert);
//		} catch (CKANException e) {
//			getLog(request).error(e);
//			setErrorMessage(request, e.getLocalizedMessage(getLocale(request)));
//		}
//		getLog(request).debug("Insert " +  keys.size() + " rows into CKAN at " + url + " with data ApiKey: " + api + " ver: " + ver);
//
//		return new ExtViewResult(getControllerName());
	}


	protected List<List<String>> getRow(PortletRequest request) throws  Exception {
		DatasetDto 				dset = getDatasetDto(request);
		List<TransformationDto> tran = getTransformationDto(request);
		List<MetadataDto> 		meta = getMetadataDto(request);
		
		BasicMetadata basicMetaData = JsonBuilder.metadataBuild(meta, dset);
		BasicTransformationConfig basicTransformationConfig = JsonBuilder.schemaBuild(meta, tran);

		Data<Dataset, BasicMetadata> data = new Data<Dataset, BasicMetadata>(dset.getDataset(), basicMetaData);
		Transform transform = new MyNLisbonCaseCsvTransform(data, BasicDatatype.getMyNeighborhoodLisbonCaseCsv(), basicTransformationConfig);
		Data<?, ?> dataRet = transform.getTarget();
		return ((CsvDataset) dataRet.getDataset()).getContent();
	}

	@Override
	public String getPropertiesFilePath() {
		return PROPERTIES_FILE;
	}

}
