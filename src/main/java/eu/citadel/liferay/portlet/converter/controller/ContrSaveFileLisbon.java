package eu.citadel.liferay.portlet.converter.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;

import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.HtmlUtil;

import eu.citadel.converter.data.Data;
import eu.citadel.converter.data.dataset.CsvDataset;
import eu.citadel.converter.data.dataset.Dataset;
import eu.citadel.converter.data.datatype.BasicDatatype;
import eu.citadel.converter.data.metadata.BasicMetadata;
import eu.citadel.converter.exceptions.ConverterException;
import eu.citadel.converter.exceptions.DatasetException;
import eu.citadel.converter.transform.MyNLisbonCaseCsvTransform;
import eu.citadel.converter.transform.Transform;
import eu.citadel.converter.transform.config.BasicTransformationConfig;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.ConverterUtils;
import eu.citadel.liferay.portlet.converter.ConverterPortlet;
import eu.citadel.liferay.portlet.converter.general.ContrSaveFileAbstarct;
import eu.citadel.liferay.portlet.dto.DatasetDto;

/**
 * @author ttrapanese
 */
/*Step 7*/
public class ContrSaveFileLisbon extends ContrSaveFileAbstarct {
	static final String JSP_MAIN_PATH 						= "/html/converter/saveFile/lisbon.jsp";
	public static final String VIEW_ATTRIBUTE_PREVIEW_LIST	= "view_attribute_preview_list";
	public static final String VIEW_ATTRIBUTE_HEADER_LIST	= "view_attribute_header_list";
	
	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return JSP_MAIN_PATH;
	}

	protected void onConverterError(RenderRequest request, ConverterException e) {
		String key  = "error";
		String msg  = e.getMessage();
		String link = ""; //TODO: c'è un link???
		
		if(e.getTranslationKey().contains(" ")) {
			link += "#validation";
			key   = "validation";
		} else if(e.getTranslationKey() != null)  {
			link += "#" + e.getTranslationKey();
			key   = e.getTranslationKey();
		};
		request.setAttribute(VIEW_ATTRIBUTE_LINK_ERROR, HtmlUtil.escapeHREF(link));
		request.setAttribute(VIEW_ATTRIBUTE_MESSAGE_ERROR, msg);
		request.setAttribute(VIEW_ATTRIBUTE_KEY_ERROR, key);
	}

	@Override
	protected void executeDoView(RenderRequest request) throws Exception, ConverterException, IOException, DatasetException {
		List<List<String>> resultOutput = null;
		DatasetDto 				dset = getDatasetDto(request);

		CsvDataset lisbonDS = (CsvDataset) getFinalDataset(request).getDataset();
		resultOutput = lisbonDS.getContent();
//		setResultString(request, resultOutput.toString());

		File fileEntry = ConverterUtils.uploadFile(request, lisbonDS, getOriginalFileName(request, dset) + ".csv");
		if (fileEntry == null) {
			getLog(request).error("error: Impossibile salvare il file");
		}
		setResultFilePath(request, fileEntry.getAbsolutePath());
		String downloadLink = Base64.encode(fileEntry.getAbsolutePath().getBytes());
		
		request.setAttribute(VIEW_ATTRIBUTE_PREVIEW_LIST, resultOutput.subList(1, Math.min(11, resultOutput.size())));		
		request.setAttribute(VIEW_ATTRIBUTE_HEADER_LIST, resultOutput.get(0));
		request.setAttribute(VIEW_ATTRIBUTE_DOWNLOAD_LINK, downloadLink);
	}

	@Override
	public ExtViewResult nextStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		return new ExtViewResult(ConverterPortlet.CONTR_CKAN_EXPORT_LISBON);
	}

	@Override
	protected String getControllerName() {
		return ConverterPortlet.CONTR_SAVE_FILE_LISBON;
	}

	@Override
	protected Transform getTransform(BasicTransformationConfig basicTransformationConfig, Data<Dataset, BasicMetadata> data) {
		return new MyNLisbonCaseCsvTransform(data, BasicDatatype.getMyNeighborhoodLisbonCaseCsv(), basicTransformationConfig);
	}
	
}
