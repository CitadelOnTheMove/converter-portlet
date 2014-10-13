package eu.citadel.liferay.portlet.converter.general;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import eu.citadel.converter.exceptions.CKANException;
import eu.citadel.converter.io.CKANUtils;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.ConverterConstants;
import eu.citadel.liferay.portlet.commons.ConverterUtils;

public abstract class ContrCkanExportAbstract extends ConverterController {
	public static final String CONTR_PARAM_URL 				= "contr_param_url";
	public static final String CONTR_PARAM_API 				= "contr_param_api";
	public static final String CONTR_PARAM_VERS 			= "contr_param_vers";
	public static final String CONTR_PARAM_TYPE				= "contr_param_type";
	public static final String CONTR_PARAM_PACKAGE_NAME		= "contr_param_package_name";
	public static final String CONTR_PARAM_NAME				= "contr_param_name";
	public static final String CONTR_PARAM_DESC 			= "contr_param_desc";
	
	public static final String PAGE_ATTRIBUTE_URL 			= "page_attribute_url";
	public static final String PAGE_ATTRIBUTE_API 			= "page_attribute_api";
	public static final String PAGE_ATTRIBUTE_VERS 			= "page_attribute_vers";
	public static final String PAGE_ATTRIBUTE_TYPE			= "page_attribute_type";
	public static final String PAGE_ATTRIBUTE_PACKAGE_NAME	= "page_attribute_package_name";
	public static final String PAGE_ATTRIBUTE_NAME			= "page_attribute_name";
	public static final String PAGE_ATTRIBUTE_DESC 			= "page_attribute_desc";
	
	public static final String TYPE_FILE_STORE				= "FS";
	public static final String TYPE_DATA_STORE				= "DS";

//	public  static final String DEFAULT_CKAN_DATASET_ID 	= "citadel-test";

	protected abstract String getControllerName();

	@Override
	public ExtViewResult doView(RenderRequest request, RenderResponse renderResponse) throws IOException, PortletException {		
		String url 			= ParamUtil.getString(request, CONTR_PARAM_URL);
		String api 			= ParamUtil.getString(request, CONTR_PARAM_API);
		String ver 			= ParamUtil.getString(request, CONTR_PARAM_VERS);

		Map<String, String> propMap = ConverterUtils.getPropertiesMap(getClass(), getPropertiesFilePath());
		if(Validator.isNull(url)) url = propMap.get(ConverterConstants.PROPERTY_CKAN_DEFAULT_URL);
		if(Validator.isNull(api)) api = propMap.get(ConverterConstants.PROPERTY_CKAN_DEFAULT_API);
		if(Validator.isNull(ver)) ver = propMap.get(ConverterConstants.PROPERTY_CKAN_DEFAULT_VER);
		
		String type 		= ParamUtil.getString(request, CONTR_PARAM_TYPE);
		String packName 	= ParamUtil.getString(request, CONTR_PARAM_PACKAGE_NAME);
		String name 		= ParamUtil.getString(request, CONTR_PARAM_NAME);
		String desc 		= ParamUtil.getString(request, CONTR_PARAM_DESC);

		if(Validator.isNull(type)) type = TYPE_FILE_STORE;
		if(Validator.isNull(packName)) packName =  propMap.get(ConverterConstants.PROPERTY_CKAN_DEFAULT_PACKAGE_NAME);

		

		request.setAttribute(PAGE_ATTRIBUTE_URL				, url);
		request.setAttribute(PAGE_ATTRIBUTE_API				, api);
		request.setAttribute(PAGE_ATTRIBUTE_VERS			, ver);
		request.setAttribute(PAGE_ATTRIBUTE_TYPE			, type);
		request.setAttribute(PAGE_ATTRIBUTE_PACKAGE_NAME	, packName);
		request.setAttribute(PAGE_ATTRIBUTE_NAME			, name);
		request.setAttribute(PAGE_ATTRIBUTE_DESC			, desc);
		
		return new ExtViewResult(getControllerName());
	}

	public ExtViewResult create(ActionRequest request, ActionResponse response) throws IOException, PortletException {
		String url 			= ParamUtil.getString(request, CONTR_PARAM_URL);
		String api 			= ParamUtil.getString(request, CONTR_PARAM_API);
		String ver 			= ParamUtil.getString(request, CONTR_PARAM_VERS);
		
		String type 		= ParamUtil.getString(request, CONTR_PARAM_TYPE);
		String packName 	= ParamUtil.getString(request, CONTR_PARAM_PACKAGE_NAME);
		String name 		= ParamUtil.getString(request, CONTR_PARAM_NAME);
		String desc 		= ParamUtil.getString(request, CONTR_PARAM_DESC);

		Map<String, String> propMap = ConverterUtils.getPropertiesMap(getClass(), getPropertiesFilePath());
		String format 		= propMap.get(ConverterConstants.PROPERTY_CKAN_DEFAULT_FORMAT);
		
//		validate(url, api, ver, type, packName, name, desc);

		if (TYPE_FILE_STORE.equals(type)) {
			try {
				createFileStore(url, api, ver, packName, name, desc, getResultFilePath(request), format);
			} catch (CKANException e) {
				getLog(request).error(e);
				setErrorMessage(request, e.getLocalizedMessage(getLocale(request)));
				return new ExtViewResult(getControllerName());
			} catch (Exception e) {
				getLog(request).error(e);
				setErrorMessage(request, e.getLocalizedMessage());
				return new ExtViewResult(getControllerName());
			}
		}else if(TYPE_DATA_STORE.equals(type)) {
			try {
				createDataStore(url, api, ver, packName, name, desc, getResultFilePath(request));
			} catch (CKANException e) {
				getLog(request).error(e);
				setErrorMessage(request, e.getLocalizedMessage(getLocale(request)));
				return new ExtViewResult(getControllerName());
			} catch (Exception e) {
				getLog(request).error(e);
				setErrorMessage(request, e.getLocalizedMessage());
				return new ExtViewResult(getControllerName());
			}
		}
		getLog(request).debug("File added to CKAN at " + url + " with data ApiKey: " + api + " ver: " + ver + " packageName: " + packName + " name: " + name + " description: " + desc + " format: " + format);
		SessionMessages.add(request, "success");
		
		return new ExtViewResult(getControllerName());
	}

	protected void createFileStore(String url, String api, String ver, String packName, String name, String desc, String filePath, String format) throws CKANException, MalformedURLException, IOException {

		Map<String, Object> resourceMap = new HashMap<String, Object>();

		resourceMap.put(CKANUtils.FIELD_NEW_RESOURCE_PACKAGE_ID		, packName);
		resourceMap.put(CKANUtils.FIELD_NEW_RESOURCE_NAME			, name);
		resourceMap.put(CKANUtils.FIELD_NEW_RESOURCE_DESCRIPTION	, desc);
		resourceMap.put(CKANUtils.FIELD_NEW_RESOURCE_FORMAT			, format);

		resourceMap.put(CKANUtils.FIELD_NEW_RESOURCE_UPLOAD			, Paths.get(filePath));

		CKANUtils.filestoreResourceCreate(new URL(url), ver, api, resourceMap);
		
		
	}
	
	protected void createDataStore(String url, String api, String ver, String packName, String name, String desc, String filePath) throws CKANException, MalformedURLException, IOException {

	}
	
	protected boolean validate(String url, String api, String ver, String type, String packName, String name, String desc){
		if(Validator.isNull(url) || Validator.isNull(api) || Validator.isNull(type) || Validator.isNull(packName) || Validator.isNull(name) || Validator.isNull(desc) )
			return false;
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			return false;
		}
		return true;
	}
	
	public abstract String getPropertiesFilePath();
}