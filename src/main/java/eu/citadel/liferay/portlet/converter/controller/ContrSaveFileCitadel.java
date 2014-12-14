package eu.citadel.liferay.portlet.converter.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import eu.citadel.converter.data.Data;
import eu.citadel.converter.data.dataset.Dataset;
import eu.citadel.converter.data.dataset.JsonDataset;
import eu.citadel.converter.data.datatype.BasicDatatype;
import eu.citadel.converter.data.metadata.BasicMetadata;
import eu.citadel.converter.exceptions.ConverterException;
import eu.citadel.converter.exceptions.DatasetException;
import eu.citadel.converter.io.index.CitadelCityInfo;
import eu.citadel.converter.io.index.CitadelIndexConfig;
import eu.citadel.converter.io.index.CitadelIndexConstants;
import eu.citadel.converter.io.index.CitadelIndexUtil;
import eu.citadel.converter.transform.CitadelJsonTransform;
import eu.citadel.converter.transform.config.BasicTransformationConfig;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.ConverterUtils;
import eu.citadel.liferay.portlet.converter.ConverterPortlet;
import eu.citadel.liferay.portlet.converter.general.ContrSaveFileAbstarct;
import eu.citadel.liferay.portlet.dto.DatasetDto;

//NOTE: The method public void download(ResourceRequest request, ResourceResponse response) 
//in this class is used during interaction with citadel
//so don't change either the class mapping (saveFile) in ContrPortlet or the method signature

/**
 * @author ttrapanese
 */
/*Step 7*/
public class ContrSaveFileCitadel extends ContrSaveFileAbstarct {
	private static final String CITY_INFO_URL 						= "http://demos.citadelonthemove.eu/app-generator/cityInfo.php";
	private static final String SAVE_FILE_URL					 	= "http://www.citadelonthemove.eu/DesktopModules/DatasetLibrary/API/Service/SaveFile";
	private static final String SAVE_INDEX_URL 						= "http://www.citadelonthemove.eu/DesktopModules/DatasetLibrary/API/Service/SaveToIndex";

	public static final String CONTR_PARAM_TITLE 					= "contr_param_title";
	public static final String CONTR_PARAM_DESCRIPTION 				= "contr_param_description";
	public static final String CONTR_PARAM_LOCATION 				= "contr_param_location";
	public static final String CONTR_PARAM_TYPE 					= "contr_param_type";
	public static final String CONTR_PARAM_PUBLISHER 				= "contr_param_publisher";
	public static final String CONTR_PARAM_SOURCE 					= "contr_param_source";
	public static final String CONTR_PARAM_LICENSE 					= "contr_param_license";
	public static final String CONTR_PARAM_LANGUAGE 				= "contr_param_language";

	public static final String VIEW_ATTRIBUTE_LICENCE_LIST			= "view_attribute_licence_list";
	public static final String VIEW_ATTRIBUTE_LOCATION_LIST			= "view_attribute_location_list";
	public static final String VIEW_ATTRIBUTE_TYPE_MAP 				= "view_attribute_type_map";
	public static final String VIEW_ATTRIBUTE_LANGUAGE_MAP 			= "view_attribute_language_map";
	public static final String VIEW_ATTRIBUTE_ENABLE_PUBLISH		= "view_attribute_enable_publish";

	public static final List<String> LICENCE_LIST 					= Arrays.asList(new String[]{"CC BY 4.0", "CC-0"});

	private static final String CHARSET_UTF_8 						= "UTF-8";
	private static final String LINK_ERROR 							= "https://github.com/CitadelOnTheMove/converter-lib/wiki/Troubleshooting";
	private static final int JSON_PREVIEW_SIZE 						= 5;
	
	public static final String VIEW_ATTRIBUTE_OBJECT_PREVIEW		= "view_attribute_object_preview";


	static final String JSP_MAIN_PATH 								= "/html/converter/saveFile/citadel.jsp";

	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return JSP_MAIN_PATH;
	}

	protected void onConverterError(RenderRequest request, ConverterException e) {
		String key  = "error";
		String msg  = e.getMessage();
		String link = LINK_ERROR;
		
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
		String resultOutput = "";
		DatasetDto 				dset = getDatasetDto(request);
		File file = null;

		JsonDataset citadelDS = (JsonDataset) getFinalDataset(request).getDataset();
		resultOutput = citadelDS.getContent();
		setResultString(request, resultOutput);
		file = stringToFile(request.getRequestedSessionId(), resultOutput);

		File fileEntry = ConverterUtils.uploadFile(request, file, getOriginalFileName(request, dset) + ".json");
		if (fileEntry == null) {
			getLog(request).error("error: Unable to save file");
		}
		setResultFilePath(request, fileEntry.getAbsolutePath());
		String downloadLink = Base64.encode(fileEntry.getAbsolutePath().getBytes());
		resultOutput = ConverterUtils.reduceJson(resultOutput, JSON_PREVIEW_SIZE);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(resultOutput);
		
		String prettyjson = gson.toJson(je);		
		

		request.setAttribute(VIEW_ATTRIBUTE_OBJECT_PREVIEW	, prettyjson);
		request.setAttribute(VIEW_ATTRIBUTE_DOWNLOAD_LINK	, downloadLink);
		request.setAttribute(VIEW_ATTRIBUTE_LOCATION_LIST	, CitadelIndexUtil.getCitadelCityInfo(CITY_INFO_URL));
		request.setAttribute(VIEW_ATTRIBUTE_LICENCE_LIST	, LICENCE_LIST);
		request.setAttribute(VIEW_ATTRIBUTE_TYPE_MAP		, CitadelIndexConstants.TYPE_MAP);
		request.setAttribute(VIEW_ATTRIBUTE_LANGUAGE_MAP	, CitadelIndexConstants.LANGUAGE_MAP);

		if(Validator.isNull(getUserId(request)) || getUserId(request) <= 0) {
			setWarningMessage(request, "save-file-citadel-invalid-id");
			request.setAttribute(VIEW_ATTRIBUTE_ENABLE_PUBLISH	, false);
		} else {
			request.setAttribute(VIEW_ATTRIBUTE_ENABLE_PUBLISH	, true);
		}
		
		file.delete();
	}

	private File stringToFile(String session, String string) throws IOException {
	    // Create temp file.
	    File temp = File.createTempFile(session, ".json_tmp");
	
	    // Write to temp file
	    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
	    out.write(string);
	    out.close();
		return temp;
	}
	
	public ExtViewResult publish(ActionRequest request, ActionResponse response) {
		String title 		= ParamUtil.getString (request, CONTR_PARAM_TITLE);
		String description	= ParamUtil.getString (request, CONTR_PARAM_DESCRIPTION);
		long locationId		= ParamUtil.getLong   (request, CONTR_PARAM_LOCATION);
		int type 			= ParamUtil.getInteger(request, CONTR_PARAM_TYPE);
		String publisher	= ParamUtil.getString (request, CONTR_PARAM_PUBLISHER);
		String source 		= ParamUtil.getString (request, CONTR_PARAM_SOURCE);
		String license 		= ParamUtil.getString (request, CONTR_PARAM_LICENSE);
		String language 	= ParamUtil.getString (request, CONTR_PARAM_LANGUAGE);

		if(Validator.isNull(getUserId(request)) || getUserId(request) <= 0) {
			getLog(request).error("Invalid id");
			setErrorMessage(request, "save-file-citadel-invalid-id");
			return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE_CITADEL);
		} 
		
		if(Validator.isNull(title)) {
			getLog(request).error("Invalid title");
			setErrorMessage(request, "Invalid title");
			return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE_CITADEL);
		}
		if(Validator.isNull(publisher)) {
			getLog(request).error("Invalid publisher");
			setErrorMessage(request, "Invalid publisher");
			return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE_CITADEL);
		}
		if(Validator.isNull(source)) {
			getLog(request).error("Invalid source");
			setErrorMessage(request, "Invalid source");
			return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE_CITADEL);
		}
		if(!LICENCE_LIST.contains(license)) {
			getLog(request).error("Invalid license");
			setErrorMessage(request, "Invalid license");
			return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE_CITADEL);
		}
		try {
			CitadelCityInfo selectedCity = null;
			List<CitadelCityInfo> listCity = null;
			listCity = CitadelIndexUtil.getCitadelCityInfo(CITY_INFO_URL);
			for (CitadelCityInfo info : listCity) {
				if( info.getId() == locationId ) {
					selectedCity = info;
					break;
				}
			}
			if(selectedCity == null) {
				getLog(request).error("Invalid location");
				setErrorMessage(request, "Invalid location");
				return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE_CITADEL);
			}
			CitadelIndexConfig config = new CitadelIndexConfig();
			config.setCharset					(CHARSET_UTF_8);
			config.setSaveFileUrl				(SAVE_FILE_URL);
			config.setSaveIndexUrl				(SAVE_INDEX_URL);
			config.setDatasetFile				(FileUtil.getShortFileName(getResultFilePath(request)));
			config.setDescription				(description);
			config.setTitle						(title);
			config.setLanguage					(language);
			config.setLocation					(selectedCity.getName());
			config.setLatitude					(selectedCity.getLat());
			config.setLongitude					(selectedCity.getLon());
			config.setLicence					(license);
			config.setPublisher					(publisher);
			config.setReleaseDate				(new Date());
			config.setType						(type);
			config.setSource					(source);
			config.setUserId					(getUserId(request));
			
			CitadelIndexUtil.uploadToCitadelIndex(config, new File(getResultFilePath(request)));
			SessionMessages.add(request, "success");
		} catch (ConverterException e) {
			getLog(request).error(e);
			setErrorMessage(request, e.getMessage());
		}		
		return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE_CITADEL);
	}
	
	@Override
	public ExtViewResult nextStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		return new ExtViewResult(ConverterPortlet.CONTR_CKAN_EXPORT_CITADEL);
	}
	
	@Override
	protected CitadelJsonTransform getTransform(BasicTransformationConfig basicTransformationConfig, Data<Dataset, BasicMetadata> data) {
		return new CitadelJsonTransform(data, BasicDatatype.getCitadelJson(), basicTransformationConfig);
	}
	
	@Override
	protected String getControllerName() {
		return ConverterPortlet.CONTR_SAVE_FILE_CITADEL;
	}

}
