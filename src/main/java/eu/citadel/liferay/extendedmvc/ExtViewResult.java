package eu.citadel.liferay.extendedmvc;

/**
 * @author ttrapanese
 */
public class ExtViewResult {
	private String controllerKey;
	private String viewKey;

	public ExtViewResult(String controllerKey) {
		setControllerKey(controllerKey);
	}

	public ExtViewResult(String controllerKey, String viewKey) {
		setControllerKey(controllerKey);
		setViewKey(viewKey);
	}

	public ExtViewResult() {
	}

	public String getControllerKey() {
		return controllerKey;
	}

	public void setControllerKey(String controllerKey) {
		this.controllerKey = controllerKey;
	}

	public String getViewKey() {
		return viewKey;
	}

	public void setViewKey(String viewKey) {
		this.viewKey = viewKey;
	}

}
