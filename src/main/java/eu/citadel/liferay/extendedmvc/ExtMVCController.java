package eu.citadel.liferay.extendedmvc;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * @author ttrapanese
 */
public class ExtMVCController {

	public ExtViewResult doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		return new ExtViewResult();
	}

	public ExtViewResult processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException, PortletException {
		return new ExtViewResult();
	}

	public ExtViewResult serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, PortletException {
		return new ExtViewResult();
	}

	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return null;
	}
}
