package eu.citadel.liferay.portlet.converter.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import eu.citadel.converter.data.dataset.CsvDataset;
import eu.citadel.converter.data.dataset.Dataset;
import eu.citadel.converter.data.dataset.DatasetType;
import eu.citadel.converter.data.dataset.ExcelDataset;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.ConverterConstants;
import eu.citadel.liferay.portlet.commons.ConverterUtils;
import eu.citadel.liferay.portlet.converter.ConverterPortlet;
import eu.citadel.liferay.portlet.converter.general.ConverterController;
import eu.citadel.liferay.portlet.dto.DatasetDto;

/**
 * @author ttrapanese
 */
/*Step 1*/
public class ContrSourceDataset extends ConverterController {
	//View path
	private static final String JSP_MAIN_PATH 				= "/html/converter/sourceDataset.jsp";
	
	
	@Override
	public ExtViewResult doView(RenderRequest request, RenderResponse renderResponse) throws IOException, PortletException {		
		cleanSession(request); //It's the first step so the session should be empty
		HttpServletRequest servRequest = 		PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
		String userIdStr = servRequest.getParameter(ConverterConstants.CITADEL_USER_ID_PARAM);
		long userId = -1; ((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)).getLanguageId();
		if(!Validator.isNull(userIdStr)) {
			userId = Long.parseLong(userIdStr);
		}
		setUserId(request, userId);
		setResult(request, ConverterUtils.getUploadedFileList(request));

		return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
	}
	
	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return JSP_MAIN_PATH;
	}

	private File saveFile(ActionRequest request, ActionResponse actionResponse) throws IOException  {
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);

		String submissionFileName = uploadRequest.getFileName("file");
		getLog(request).debug("upload file: " + submissionFileName);

		// uploaded file you can see it in /tomcat/temp
		File submissionFile = uploadRequest.getFile("file");
		if(!submissionFile.exists()) return null;
		//Check dataType
		Path path = submissionFile.toPath();
		String dataType = DatasetType.detect(path);
		if(!DatasetType.TYPE_EXCEL.equals(dataType) && !DatasetType.TYPE_CSV.equals(dataType)) {
			getLog(request).warn("uploaded file type unsupported: " + submissionFileName);
			submissionFile.delete();
			setErrorMessage(request, "select-source-invalid-file-format");
			return null;
		}
		
		File destFile = ConverterUtils.uploadOriginalDocument(request, submissionFile, submissionFileName);
				
		submissionFile.delete();
		getLog(request).debug("save file: " + submissionFileName);
		
		return destFile;
	} 
	
	public ExtViewResult submitFile(ActionRequest request, ActionResponse response)  {
		try {
			saveFile(request, response);
		} catch (IOException e) {
			getLog(request).error(e);
		}
		return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
	} 

	public ExtViewResult submitFileAndConvert(ActionRequest request, ActionResponse response)  {
		File file;
		try {
			file = saveFile(request, response);
		} catch (IOException e) {
			setErrorMessage(request, e.getLocalizedMessage());
			return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
		}
		if(file == null) { 
			setErrorMessage(request, "select-source-invalid-url-format");
			return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
		}
		Path path = file.toPath();

		String dataType = DatasetType.detect(path);
		
		//Create dataType
		getLog(request).debug("selected mimeType: " + dataType);
		Dataset ds = null;
		if (dataType.equals(DatasetType.TYPE_EXCEL)) {
			ds = new ExcelDataset(path);
		} else if (dataType.equals(DatasetType.TYPE_CSV)) {
			ds = new CsvDataset(path);
		}
		
		DatasetDto dsDto = new DatasetDto();
		dsDto.setFile(file.getAbsolutePath());
		dsDto.setFileEntry(file);
		dsDto.setDataset(ds);
		setDatasetDto(request, dsDto);			
		
		return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL);
	}
	

	public ExtViewResult submitUrlAndConvert(ActionRequest request, ActionResponse response)  {
		String urlStr = ParamUtil.getString(request, "fileUrl");
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			getLog(request).debug("Invalid url: " + urlStr);
			return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
		}

		String dataType =  DatasetType.detect(url);
		if(!DatasetType.TYPE_EXCEL.equals(dataType) && !DatasetType.TYPE_CSV.equals(dataType)) {
			getLog(request).warn("Selected url is invalid or point to invalid file");
			setErrorMessage(request, "select-source-invalid-url-format");
			return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
		}
		
		//Create dataType
		getLog(request).debug("input url: " + url.toString());
		getLog(request).debug("selected mimeType: " + dataType);
		Dataset ds = null;
		if (dataType.equals(DatasetType.TYPE_EXCEL)) {
			ds = new ExcelDataset(url);
		} else if (dataType.equals(DatasetType.TYPE_CSV)) {
			ds = new CsvDataset(url);
		}
		
		DatasetDto dsDto = new DatasetDto();
		dsDto.setDataset(ds);
		dsDto.setUrl(url);
		setDatasetDto(request, dsDto);			
		
		return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL);
	}
	
}
