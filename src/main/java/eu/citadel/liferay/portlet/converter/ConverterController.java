package eu.citadel.liferay.portlet.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import eu.citadel.converter.data.datatype.BasicDatatype;
import eu.citadel.liferay.extendedmvc.ExtMVCListController;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.ConverterConstants;
import eu.citadel.liferay.portlet.dto.DatasetDto;
import eu.citadel.liferay.portlet.dto.MetadataDto;
import eu.citadel.liferay.portlet.dto.TransformationDto;

/**
 * @author ttrapanese
 */
public class ConverterController extends ExtMVCListController {
	protected DatasetDto getDatasetDto(PortletRequest request){
		DatasetDto ret = (DatasetDto) request.getPortletSession().getAttribute(ConverterConstants.SESSION_ATTR_DATASET);
		if(ret == null) return new DatasetDto();
		return ret;
	}	
	
	protected List<MetadataDto> getMetadataDto(PortletRequest request){
		@SuppressWarnings("unchecked")
		List<MetadataDto> ret = (List<MetadataDto>) request.getPortletSession().getAttribute(ConverterConstants.SESSION_ATTR_METADATA_LIST);
		if(ret == null) return new ArrayList<MetadataDto>();
		return new ArrayList<MetadataDto>(ret);
	}	
	
	protected List<TransformationDto> getTransformationDto(PortletRequest request){
		@SuppressWarnings("unchecked")
		List<TransformationDto> ret = (List<TransformationDto>) request.getPortletSession().getAttribute(ConverterConstants.SESSION_ATTR_TRANSFORMATION_LIST);
		if(ret == null) return new ArrayList<TransformationDto>();
		return new ArrayList<TransformationDto>(ret);
	}	

	protected String getResultString(PortletRequest request){
		String ret = (String) request.getPortletSession().getAttribute(ConverterConstants.SESSION_ATTR_RESULT_STRING);
		if(Validator.isNull(ret)) return "";
		return ret;
	}	
	
	protected BasicDatatype getSelectedBasicDatatype(PortletRequest request){
		BasicDatatype ret = (BasicDatatype) request.getPortletSession().getAttribute(ConverterConstants.SESSION_ATTR_EXPORT_TYPE);
		if(ret == null){
			return BasicDatatype.getCitadelJson();
		}
		return ret;
	}	

	protected void setDatasetDto(PortletRequest request, DatasetDto dto){
		request.getPortletSession().setAttribute(ConverterConstants.SESSION_ATTR_DATASET, dto);
	}	
	
	protected void setMetadataDto(PortletRequest request, List<MetadataDto> dtoList){
		request.getPortletSession().setAttribute(ConverterConstants.SESSION_ATTR_METADATA_LIST, dtoList);
	}	
	
	protected void setTransformationDto(PortletRequest request, List<TransformationDto> dtoList){
		request.getPortletSession().setAttribute(ConverterConstants.SESSION_ATTR_TRANSFORMATION_LIST, dtoList);
	}	

	protected void setResultString(PortletRequest request, String result){
		request.getPortletSession().setAttribute(ConverterConstants.SESSION_ATTR_RESULT_STRING, result);
	}	
	
	protected void setSelectedBasicDatatype(PortletRequest request, BasicDatatype basicDatatype){
		request.getPortletSession().setAttribute(ConverterConstants.SESSION_ATTR_EXPORT_TYPE, basicDatatype);
	}	

	protected void cleanSession(PortletRequest request){
		request.getPortletSession().removeAttribute(ConverterConstants.SESSION_ATTR_DATASET);
		request.getPortletSession().removeAttribute(ConverterConstants.SESSION_ATTR_METADATA_LIST);
		request.getPortletSession().removeAttribute(ConverterConstants.SESSION_ATTR_TRANSFORMATION_LIST);
		request.getPortletSession().removeAttribute(ConverterConstants.SESSION_ATTR_RESULT_STRING);
	}	

	
	
	public ExtViewResult nextStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		return null;
	}

	public ExtViewResult previousStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		return null;
	}

	public ExtViewResult cancel(ActionRequest actionRequest, ActionResponse actionResponse) {
		cleanSession(actionRequest);
		return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
	}

	public ExtViewResult finish(ActionRequest actionRequest, ActionResponse actionResponse) {
		cleanSession(actionRequest);
		return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
	}

	protected Locale getLocale(PortletRequest request) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		if(themeDisplay == null) return LocaleUtil.getDefault();
		return themeDisplay.getLocale();
	}
}
