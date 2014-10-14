package eu.citadel.liferay.portlet.converter.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ParamUtil;

import eu.citadel.converter.data.Data;
import eu.citadel.converter.data.dataset.Dataset;
import eu.citadel.converter.data.dataset.DatasetStatus;
import eu.citadel.converter.data.metadata.BasicMetadata;
import eu.citadel.converter.exceptions.ConverterException;
import eu.citadel.converter.exceptions.DatasetException;
import eu.citadel.converter.transform.Transform;
import eu.citadel.converter.transform.config.BasicTransformationConfig;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.JsonBuilder;
import eu.citadel.liferay.portlet.converter.ConverterPortlet;
import eu.citadel.liferay.portlet.dto.DatasetDto;
import eu.citadel.liferay.portlet.dto.MetadataDto;
import eu.citadel.liferay.portlet.dto.TransformationDto;

public abstract class ContrSaveFileAbstarct extends ConverterController {
	public static final String PAGE_PARAM_DOWNLOAD_LINK 			= "page_param_download_link";

	public static final String VIEW_ATTRIBUTE_MESSAGE_ERROR 		= "view_attribute_message_error";
	public static final String VIEW_ATTRIBUTE_LINK_ERROR 			= "view_attribute_link_error";
	public static final String VIEW_ATTRIBUTE_KEY_ERROR 			= "view_attribute_key_error";
	public static final String VIEW_ATTRIBUTE_DOWNLOAD_LINK 		= "view_attribute_download_link";
	

	protected abstract String getControllerName();

	protected abstract void executeDoView(RenderRequest request) throws Exception, ConverterException, IOException, DatasetException;
	
	protected abstract void onConverterError(RenderRequest request, ConverterException e);

	@Override
	public ExtViewResult doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
		try {
			executeDoView(request);
			return new ExtViewResult(getControllerName());
		} catch (ConverterException e) {
			getLog(request).error("error: "+ e.getLocalizedMessage(getLocale(request)));
			onConverterError(request, e);
			return new ExtViewResult(getControllerName());
		} catch (Exception e) {
			getLog(request).error("error: "+ e.getMessage());
			throw new PortletException(e);
		}
	}

	protected String getOriginalFileName(RenderRequest request, DatasetDto dset) throws DatasetException {
		File originalFilePath = null;
		try { 
			originalFilePath = ((Path) dset.getDataset().getInternalStateObject(DatasetStatus.STATUS_PATH)).toFile();
		} catch (DatasetException e) {
			originalFilePath = ((Path) dset.getDataset().getInternalStateObject(DatasetStatus.STATUS_TEMPPATH)).toFile();
		}
		return originalFilePath.getName();
	}

	protected Data<?, ?> getFinalDataset(PortletRequest request) throws Exception, ConverterException, IOException {
		DatasetDto 				dset = getDatasetDto(request);
		List<TransformationDto> tran = getTransformationDto(request);
		List<MetadataDto> 		meta = getMetadataDto(request);
		
		BasicMetadata basicMetaData = JsonBuilder.metadataBuild(meta, dset);
		BasicTransformationConfig basicTransformationConfig = JsonBuilder.schemaBuild(meta, tran);

		Data<Dataset, BasicMetadata> data = new Data<Dataset, BasicMetadata>(dset.getDataset(), basicMetaData);
		Transform transform = getTransform(basicTransformationConfig, data);
		Data<?, ?> dataRet = transform.getTarget();
		return dataRet;
	}

	protected abstract Transform getTransform(BasicTransformationConfig basicTransformationConfig, Data<Dataset, BasicMetadata> data);
	
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
	public ExtViewResult previousStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		return new ExtViewResult(ConverterPortlet.CONTR_EXPORT_SCHEMA);
	}

	
	
}