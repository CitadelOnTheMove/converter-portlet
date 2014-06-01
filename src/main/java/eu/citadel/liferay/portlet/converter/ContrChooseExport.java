package eu.citadel.liferay.portlet.converter;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.util.ParamUtil;

import eu.citadel.converter.data.datatype.BasicDatatype;
import eu.citadel.liferay.extendedmvc.ExtViewResult;

/**
 * @author ttrapanese
 */
/*Step 5 - Disabled*/
public class ContrChooseExport extends ConverterController {
	private static Log _log = ConverterPortlet.getLogger();
	//View path
	private static final String JSP_MAIN_PATH 				= "/html/converter/chooseExport.jsp";

	public static final String VIEW_ATTRIBUTE_TYPE_LIST 	= "view_attribute_type_list";

	public static final String VIEW_ATTRIBUTE_SELECTED_TYPE = "view_attribute_selected_type";
	public static final String CONTR_PARAM_SELECTED_TYPE	= "contr_param_selected_type";

	@Override
	public ExtViewResult doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		List<BasicDatatype> supportedTypeList =  BasicDatatype.getAvailableBasicDatatype();

		renderRequest.setAttribute(VIEW_ATTRIBUTE_TYPE_LIST,  supportedTypeList);
		renderRequest.setAttribute(VIEW_ATTRIBUTE_SELECTED_TYPE, getSelectedBasicDatatype(renderRequest));
		return new ExtViewResult(ConverterPortlet.CONTR_CHOOSE_EXPORT);
	}
	
	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return JSP_MAIN_PATH;
	}
	
	@Override
	public ExtViewResult previousStep(ActionRequest actionRequest, ActionResponse actionResponse) {
		return new ExtViewResult(ConverterPortlet.CONTR_SEMANTIC_MATCH);
	}
	
	@Override
	public ExtViewResult nextStep(ActionRequest request, ActionResponse actionResponse) {
		int exportType = ParamUtil.getInteger(request, CONTR_PARAM_SELECTED_TYPE);
		setSelectedBasicDatatype(request, BasicDatatype.getAvailableBasicDatatype().get(exportType));
		_log.debug("session: " + request.getPortletSession().getId() +" selected export type: " + exportType);
		return new ExtViewResult(ConverterPortlet.CONTR_EXPORT_SCHEMA);
	}
}
