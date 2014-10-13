package eu.citadel.liferay.portlet.converter;

import static eu.citadel.liferay.extendedmvc.ExtMVCFactory.DEFAULT_CONTROLLER_KEY;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import eu.citadel.liferay.extendedmvc.ExtMVCFactory;
import eu.citadel.liferay.extendedmvc.ExtMVCPortlet;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.converter.controller.ContrChooseDataset;
import eu.citadel.liferay.portlet.converter.controller.ContrChooseExport;
import eu.citadel.liferay.portlet.converter.controller.ContrCkanExportCitadel;
import eu.citadel.liferay.portlet.converter.controller.ContrCkanExportLisbon;
import eu.citadel.liferay.portlet.converter.controller.ContrExportSchema;
import eu.citadel.liferay.portlet.converter.controller.ContrInputDetail;
import eu.citadel.liferay.portlet.converter.controller.ContrSaveFileCitadel;
import eu.citadel.liferay.portlet.converter.controller.ContrSaveFileLisbon;
import eu.citadel.liferay.portlet.converter.controller.ContrSemanticMatch;
import eu.citadel.liferay.portlet.converter.controller.ContrSourceDataset;

/**
 * @author ttrapanese
 */
public class ConverterPortlet extends ExtMVCPortlet {
	public static final String SESSION_LANG_PARAM 		= "session_lang_param";
	
	public static final String CONTR_SOURCE_DATA 		= "sourceData";
	public static final String CONTR_CHOOSE_DATA 		= "chooseData";
	public static final String CONTR_INPUT_DETAIL 		= "inputDetail";
	public static final String CONTR_SEMANTIC_MATCH		= "semanticMatch";
	public static final String CONTR_CHOOSE_EXPORT 		= "chooseExport";
	public static final String CONTR_EXPORT_SCHEMA  	= "exportSchema";
	public static final String CONTR_SAVE_FILE_CITADEL 	= "saveFileCitadel";
	public static final String CONTR_SAVE_FILE_LISBON 	= "saveFileLisbon";
	public static final String CONTR_CKAN_EXPORT_CITADEL= "ckanExportCitadel";
	public static final String CONTR_CKAN_EXPORT_LISBON	= "ckanExportLisbon";

	private static ExtMVCFactory factory = null;
	
	static{
		factory = new ExtMVCFactory();
		factory.putController(DEFAULT_CONTROLLER_KEY	, ContrSourceDataset.class);
		factory.putController(CONTR_SOURCE_DATA			, ContrSourceDataset.class);
		factory.putController(CONTR_CHOOSE_DATA			, ContrChooseDataset.class);
		factory.putController(CONTR_INPUT_DETAIL		, ContrInputDetail.class);
		factory.putController(CONTR_SEMANTIC_MATCH		, ContrSemanticMatch.class);
		factory.putController(CONTR_CHOOSE_EXPORT		, ContrChooseExport.class);
		factory.putController(CONTR_EXPORT_SCHEMA		, ContrExportSchema.class);
		factory.putController(CONTR_SAVE_FILE_CITADEL	, ContrSaveFileCitadel.class);
		factory.putController(CONTR_SAVE_FILE_LISBON	, ContrSaveFileLisbon.class);
		factory.putController(CONTR_CKAN_EXPORT_CITADEL	, ContrCkanExportCitadel.class);
		factory.putController(CONTR_CKAN_EXPORT_LISBON	, ContrCkanExportLisbon.class);
	}
	
	@Override
	public ExtMVCFactory getFactory() {
		return factory;
	}

	/**
	 * Check for selected language change
	 * @param request The portlet request
	 * @return True if language is changed
	 */
	private static boolean checkLanguage(PortletRequest request){
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		String lng = themeDisplay.getLanguageId();
		String oldLng = (String) request.getPortletSession().getAttribute(SESSION_LANG_PARAM);
		boolean ret = false;
		if(Validator.isNull(oldLng)) {
			updateLanguage(request);
			oldLng = (String) request.getPortletSession().getAttribute(SESSION_LANG_PARAM);
		}
		if(!lng.equals(oldLng) && Validator.isNotNull(oldLng)) {
			ret = true;
		}
		return ret;
	}

	/**
	 * Update selected language
	 * @param request
	 */
	private static void updateLanguage(PortletRequest request){
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		String lng = themeDisplay.getLanguageId();
		request.getPortletSession().setAttribute(SESSION_LANG_PARAM, lng);
	}

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws IOException, PortletException {
		if(checkLanguage(request)) return;
		super.processAction(request, response);
	}
	
	@Override
	protected ExtViewResult getCurrentView(PortletRequest request) {
		if(checkLanguage(request)) return new ExtViewResult(ExtMVCFactory.DEFAULT_CONTROLLER_KEY);;
		return super.getCurrentView(request);
	}
	
	@Override
	public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
		super.doView(request, response);
		if(checkLanguage(request)) updateLanguage(request);
	}
}
