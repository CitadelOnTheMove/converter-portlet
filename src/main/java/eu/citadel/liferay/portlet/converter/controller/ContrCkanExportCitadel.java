package eu.citadel.liferay.portlet.converter.controller;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.converter.ConverterPortlet;
import eu.citadel.liferay.portlet.converter.general.ContrCkanExportAbstract;

/**
 * @author ttrapanese
 */
/*Step 8*/
public class ContrCkanExportCitadel extends ContrCkanExportAbstract {
	//View path
	private static final String JSP_MAIN_PATH 				= "/html/converter/ckanExport/citadel.jsp";
	private static final String PROPERTIES_FILE				= "ckan/citadel.properties";
	

	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return JSP_MAIN_PATH;
	}

	@Override
	public ExtViewResult previousStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		return new ExtViewResult(ConverterPortlet.CONTR_SAVE_FILE_CITADEL);
	}

	@Override
	protected String getControllerName() {
		return ConverterPortlet.CONTR_CKAN_EXPORT_CITADEL;
	}

	@Override
	public String getPropertiesFilePath() {
		return PROPERTIES_FILE;
	}
}
