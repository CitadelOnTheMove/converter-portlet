package eu.citadel.liferay.extendedmvc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * @author ttrapanese
 */
public abstract class ExtMVCPortlet extends MVCPortlet {
	private static Log _log = LogFactoryUtil.getLog(ExtMVCPortlet.class);

	public static final String MVC_REQUEST_PARAM 		= "extended_mvc_request_parameter";

	public abstract ExtMVCFactory getFactory();
	
	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException, PortletException {
		String resourceId = request.getResourceID();
		_log.debug("session: " + request.getPortletSession().getId() + ": serveResource start: " + resourceId);
		String action 		= null;
		String controller_key	= null;

		if (resourceId != null) {
			int position = resourceId.indexOf('_');
			if (position != -1) {
				controller_key = resourceId.substring(0, position);
				action = resourceId.substring(position + 1);
			}
		}
		if (controller_key == null) {
			// use default value
			controller_key = getCurrentView(request).getControllerKey();
			action = "serveResource";
		}

		try {
			ExtMVCController controller = getFactory().getController(controller_key, request, response);
			executeDoServeResource(controller, action, request, response);
		} catch (ExtControllerException e) {
			_log.error(e.getMessage());
			_log.error("Error when executing serveResource method, resource Id: " + resourceId);
			super.serveResource(request, response);
		} catch (ExtControllerMethodException | ExtControllerInvalidMethodException e) {
			_log.error("Error when executing serveResource method, resource Id: " + resourceId);
			super.serveResource(request, response);
		}
		_log.debug("session: " + request.getPortletSession().getId() + ": serveResource end: " + resourceId);
	}

	private void executeDoServeResource(ExtMVCController controller, String action, ResourceRequest request, ResourceResponse response) throws ExtControllerMethodException, ExtControllerInvalidMethodException {
		try {
			Method method = controller.getClass().getMethod(action, new Class[] { ResourceRequest.class, ResourceResponse.class });
			Object args[] = new Object[] { request, response };
			method.invoke(controller, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			_log.error(e);
			throw new ExtControllerMethodException(e);
		} catch (NoSuchMethodException | SecurityException e) {
			_log.error("Invalid method name");
			throw new ExtControllerInvalidMethodException(e);
		}
	}

	@Override
	public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
		_log.debug("session: " + request.getPortletSession().getId() + ": doView start");
		try{
			ExtMVCFactory factory = getFactory();

			ExtViewResult view = getCurrentView(request);
			
			ExtMVCController controllerInstance = factory.getController(view.getControllerKey(), request, response);
			view = executeDoView(controllerInstance, request, response);
			
			if(Validator.isNull(view.getControllerKey())) {
				view.setControllerKey(ExtMVCFactory.DEFAULT_CONTROLLER_KEY);
			}
			
			
			controllerInstance = factory.getController(view.getControllerKey(), request, response);
			this.viewTemplate = controllerInstance.getViewPath(view.getViewKey(), request, response);
			this.include(viewTemplate, request, response);
		} catch (ExtControllerException e) {
			_log.error(e.getMessage());
			_log.error("back to default doView");
			super.doView(request, response);
		} catch (ExtControllerMethodException | ExtControllerInvalidMethodException e) {
			_log.error("Error when executing do view method" );
			super.doView(request, response);
		}
		_log.debug("session: " + request.getPortletSession().getId() + ": doView end");
	}

	private ExtViewResult splitControllerView(String controller_view){
		if(controller_view != null){
			ExtViewResult ret 	= new ExtViewResult();
			
			String controller = null;
			String view = null;
			int position = controller_view.indexOf('_');
			//se c'è _ la stringa è nella forma controller_view
			if (position != -1) {
				controller 	= controller_view.substring(0, position);
				view = controller_view.substring(position + 1);
			}else{
				//se no assumo che sia definito solo il controller che si occuperà della view di default
				controller = controller_view;
			}
			ret.setControllerKey(controller);
			ret.setViewKey(view);
			return ret;
		}
		return null;
	}	
	
	protected ExtViewResult getCurrentView(PortletRequest request) {
		ExtViewResult ret;
		String controller_view 		= request.getParameter(MVC_REQUEST_PARAM);
		ret = splitControllerView(controller_view);

		if(ret == null){
			ret = new ExtViewResult(ExtMVCFactory.DEFAULT_CONTROLLER_KEY);
		}
		return ret;
	}
	

	@Override
	public void processAction(ActionRequest request, ActionResponse response) throws IOException, PortletException {
		_log.debug("session: " + request.getPortletSession().getId() + ": processAction start");
		ExtMVCFactory factory = getFactory();

		// estrai il nome della action
		String actionName = ParamUtil.getString(request, ActionRequest.ACTION_NAME, null);

		String action 		= null;
		String controller_key	= null;

		if (actionName != null) {
			int position = actionName.indexOf('_');
			if (position != -1) {
				controller_key = actionName.substring(0, position);
				action = actionName.substring(position + 1);
			}
		}
		if (controller_key == null) {
			// use default value
			controller_key = getCurrentView(request).getControllerKey();
			action = "processAction";
		}

		try {
			ExtMVCController controller = getFactory().getController(controller_key, request, response);
			ExtViewResult result = executeDoAction(controller, action, request, response);

			controller = factory.getController(result.getControllerKey(), request, response);
			this.viewTemplate = controller.getViewPath(result.getViewKey(), request, response);

			response.setRenderParameter(MVC_REQUEST_PARAM, result.getControllerKey());
		} catch (ExtControllerException e) {
			_log.error(e.getMessage());
			_log.error("Error when executing processAction method: " + actionName );
			super.processAction(request, response);
		} catch (ExtControllerMethodException | ExtControllerInvalidMethodException e) {
			_log.error("Error when executing processAction method: " + actionName );
			super.processAction(request, response);
		}

		_log.debug("session: " + request.getPortletSession().getId() + ": processAction end");
	}
	
	protected ExtViewResult executeDoView(ExtMVCController controller, RenderRequest request, RenderResponse response) throws ExtControllerMethodException, ExtControllerInvalidMethodException {
		ExtViewResult returnValue = null;
		try {
			_log.debug("session: " + request.getPortletSession().getId() + " doView start execution of controller: " + controller.getClass());
			Method method = controller.getClass().getMethod("doView", new Class[] { RenderRequest.class, RenderResponse.class });
			Object args[] = new Object[] { request, response };
			returnValue = (ExtViewResult) method.invoke(controller, args);
			_log.debug("session: " + request.getPortletSession().getId() + " doView end execution of controller: " + controller.getClass());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			_log.error(e);
			throw new ExtControllerMethodException(e);
		} catch (NoSuchMethodException | SecurityException e) {
			_log.error("Invalid method name");
			throw new ExtControllerInvalidMethodException(e);
		}
		return returnValue;
	}

	protected ExtViewResult executeDoAction(ExtMVCController controller, String action, ActionRequest request, ActionResponse response) throws ExtControllerMethodException, ExtControllerInvalidMethodException {
		ExtViewResult returnValue = null;
		try {
			_log.debug("session: " + request.getPortletSession().getId() + " action: " + action + " start execution of controller: " + controller.getClass());
			Method method = controller.getClass().getMethod(action, new Class[] { ActionRequest.class, ActionResponse.class });
			Object args[] = new Object[] { request, response };
			returnValue = (ExtViewResult) method.invoke(controller, args);
			_log.debug("session: " + request.getPortletSession().getId() + " action: " + action + " end execution of controller: " + controller.getClass());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			_log.error(e);
			throw new ExtControllerMethodException(e);
		} catch (NoSuchMethodException | SecurityException e) {
			_log.error("Invalid method name");
			throw new ExtControllerInvalidMethodException(e);
		}
		return returnValue;
	}
}
