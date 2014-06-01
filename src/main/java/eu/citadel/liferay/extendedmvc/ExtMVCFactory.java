package eu.citadel.liferay.extendedmvc;
 
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * @author ttrapanese
 */
public class ExtMVCFactory {
	public static final String DEFAULT_CONTROLLER_KEY = "default";

	private Map<String, Class<? extends ExtMVCController>> controllerMap = null;
	
	public ExtMVCFactory() {
		controllerMap = new LinkedHashMap<String, Class<? extends ExtMVCController>>();
	}
	
	public String getDefaultControllerName() {
		if (controllerMap.containsKey(DEFAULT_CONTROLLER_KEY))
			return DEFAULT_CONTROLLER_KEY;
		else
			return null;
	}

	public Map<String, Class<? extends ExtMVCController>> getControllerMap() {
		return Collections.unmodifiableMap(controllerMap);
	}

	protected void setControllerMap(Map<String, Class<? extends ExtMVCController>> viewMap) {
		this.controllerMap = viewMap;
	}

	public ExtMVCController getController(String viewName, PortletRequest request, PortletResponse response) throws ExtControllerException {
		Class<? extends ExtMVCController> viewClass = getControllerClass(viewName);
		return prepareController(viewClass);
	}

	protected Class<? extends ExtMVCController> getControllerClass(String viewName) {
		return controllerMap.get(viewName);
	}

	protected ExtMVCController prepareController(Class<? extends ExtMVCController> viewClass) throws ExtControllerException {
		ExtMVCController view = null;
		try {
			view = viewClass.newInstance();
		} catch (Exception e) {
			throw new ExtControllerException(e);
		}
		return view;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void putController(String key, Class<? extends ExtMVCController> value) {
		controllerMap.put(key, value);
	}
}
