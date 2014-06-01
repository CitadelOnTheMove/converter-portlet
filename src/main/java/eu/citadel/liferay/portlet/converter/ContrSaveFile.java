package eu.citadel.liferay.portlet.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ParamUtil;

import eu.citadel.converter.data.Data;
import eu.citadel.converter.data.dataset.Dataset;
import eu.citadel.converter.data.dataset.JsonDataset;
import eu.citadel.converter.data.datatype.BasicDatatype;
import eu.citadel.converter.data.metadata.BasicMetadata;
import eu.citadel.converter.exceptions.ConverterException;
import eu.citadel.converter.transform.CitadelJsonTransform;
import eu.citadel.converter.transform.Transform;
import eu.citadel.converter.transform.config.BasicTransformationConfig;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.ConverterUtils;
import eu.citadel.liferay.portlet.commons.JsonBuilder;
import eu.citadel.liferay.portlet.dto.DatasetDto;
import eu.citadel.liferay.portlet.dto.MetadataDto;
import eu.citadel.liferay.portlet.dto.TransformationDto;

/**
 * @author ttrapanese
 */
/*Step 7*/
public class ContrSaveFile extends ConverterController {
	private static Log _log = ConverterPortlet.getLogger();
	
	private static final int JSON_PREVIEW_SIZE = 5;

	private static final String JSP_MAIN_PATH 					= "/html/converter/saveFile.jsp";

	public static final String PAGE_PARAM_DOWNLOAD_LINK			= "page_param_download_link";

	public static final String VIEW_ATTRIBUTE_OBJECT_PREVIEW	= "view_attribute_object_preview";
	public static final String VIEW_ATTRIBUTE_DOWNLOAD_LINK		= "view_attribute_download_link";
	public static final String VIEW_ATTRIBUTE_MESSAGE_ERROR		= "view_attribute_message_error";

	@Override
	public ExtViewResult doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
		String resultOutput = "";
		DatasetDto 				dset = getDatasetDto(request);
		List<TransformationDto> tran = getTransformationDto(request);
		List<MetadataDto> 		meta = getMetadataDto(request);
		File file = null;
		try {
			BasicMetadata basicMetaData = JsonBuilder.metadataBuild(meta, dset);
			BasicTransformationConfig basicTransformationConfig = JsonBuilder.schemaBuild(meta, tran);

			Data<Dataset, BasicMetadata> data = new Data<Dataset, BasicMetadata>(dset.getDataset(), basicMetaData);
			Transform transform = new CitadelJsonTransform(data, BasicDatatype.getCitadelJson(), basicTransformationConfig);
			Data<?, ?> dataRet = transform.getTarget();
			JsonDataset citadelDS = (JsonDataset) dataRet.getDataset();
			resultOutput = citadelDS.getContent();
			setResultString(request, resultOutput);
			file = stringToFile(request.getRequestedSessionId(), resultOutput);

			File fileEntry = ConverterUtils.uploadJson(request, file, dset.getFileEntry().getName() + ".json");
			if (fileEntry == null) {
				_log.error("session: " + request.getPortletSession().getId() +" error: Impossibile salvare il file");
			}
			String downloadLink = Base64.encode(fileEntry.getAbsolutePath().getBytes());
			_log.debug("Originale");
			System.out.println(resultOutput);
			_log.debug(resultOutput);
			resultOutput = ConverterUtils.reduceJson(resultOutput, JSON_PREVIEW_SIZE);
			_log.debug("Ridotto");
			_log.debug(resultOutput);
			System.out.println(resultOutput);
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(resultOutput);
			
			String prettyjson = gson.toJson(je);		
			
			request.setAttribute(VIEW_ATTRIBUTE_OBJECT_PREVIEW, prettyjson);
			request.setAttribute(VIEW_ATTRIBUTE_DOWNLOAD_LINK, downloadLink);
			file.delete();
			return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE);
		} catch (ConverterException e) {
			_log.error("session: " + request.getPortletSession().getId() +" error: "+ e.getLocalizedMessage(getLocale(request)));
			request.setAttribute(VIEW_ATTRIBUTE_MESSAGE_ERROR, e.getMessage());
			return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE);
		} catch (Exception e) {
			_log.error("session: " + request.getPortletSession().getId() +" error: "+ e.getMessage());
			if(file != null) file.delete();
			throw new PortletException(e);
		}
	}
	private File stringToFile(String session, String string) throws IOException{
	    // Create temp file.
	    File temp = File.createTempFile(session, ".json_tmp");

	    // Write to temp file
	    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
	    out.write(string);
	    out.close();
		return temp;
	}
	public void download(ResourceRequest request, ResourceResponse response) throws IOException, PortletException {
		String dwl = ParamUtil.getString(request, PAGE_PARAM_DOWNLOAD_LINK);
		String path = new String(Base64.decode(dwl));
		File file = new File(path);
		OutputStream outStream = response.getPortletOutputStream();
		if (!file.exists() || !file.canRead()) {
			return;
		} else {
			FileInputStream inStream = new FileInputStream(file);
			response.setProperty("Content-disposition", "attachment; filename=\"" + file.getName() + "\"");
			byte[] buffer = new byte[1024];
			while (true) {
				int bytes = inStream.read(buffer);
				if (bytes <= 0) {
					break;
				}
				outStream.write(buffer, 0, bytes);
			}
			inStream.close();
		}
		outStream.flush();
		outStream.close();		
		
	}
	
	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return JSP_MAIN_PATH;
	}
	
	@Override
	public ExtViewResult previousStep(ActionRequest actionRequest,	ActionResponse actionResponse) {
		return new ExtViewResult(ConverterPortlet.CONTR_EXPORT_SCHEMA);
	}
	
}
