package eu.citadel.liferay.portlet.converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.util.PortalUtil;

import eu.citadel.converter.data.dataset.CsvDataset;
import eu.citadel.converter.data.dataset.Dataset;
import eu.citadel.converter.data.dataset.DatasetType;
import eu.citadel.converter.data.dataset.ExcelDataset;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.ConverterUtils;
import eu.citadel.liferay.portlet.dto.DatasetDto;

/**
 * @author ttrapanese
 */
/*Step 1*/
public class ContrSourceDataset extends ConverterController {
	private static Log _log = ConverterPortlet.getLogger();
	//View path
	private static final String JSP_MAIN_PATH 				= "/html/converter/sourceDataset.jsp";
	
	
	@Override
	public ExtViewResult doView(RenderRequest request, RenderResponse renderResponse) throws IOException, PortletException {		
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
		_log.debug("session: " + request.getPortletSession().getId() +" upload file: " + submissionFileName);

		// uploaded file you can see it in /tomcat/temp
		File submissionFile = uploadRequest.getFile("file");
		if(!submissionFile.exists()) return null;
		//Check dataType
		Path path = submissionFile.toPath();
		String dataType = DatasetType.detect(path);
		if(!dataType.equals(DatasetType.TYPE_EXCEL) && !dataType.equals(DatasetType.TYPE_CSV)) {
			_log.warn("session: " + request.getPortletSession().getId() +" upoaded file type unsupported: " + submissionFileName);
			submissionFile.delete();
			return null;
		}
		
		File destFile = ConverterUtils.uploadOriginalDocument(request, submissionFile, submissionFileName);
				
		submissionFile.delete();
		_log.debug("session: " + request.getPortletSession().getId() +" save file: " + submissionFileName);
		
		return destFile;
	} 
	
	public ExtViewResult submitFile(ActionRequest request, ActionResponse response)  {
		try {
			saveFile(request, response);
		} catch (IOException e) {
			_log.error(e);
		}
		return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
	} 

	public ExtViewResult submitFileAndConvert(ActionRequest request, ActionResponse response)  {
		File file;
		try {
			file = saveFile(request, response);
		} catch (IOException e) {
			return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
		}
		if(file == null)
			return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
		Path path = file.toPath();

		String dataType = DatasetType.detect(path);
		//Create dataType
		_log.debug("session: " + request.getPortletSession().getId() +" selected mimeType: " + dataType);
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
}
