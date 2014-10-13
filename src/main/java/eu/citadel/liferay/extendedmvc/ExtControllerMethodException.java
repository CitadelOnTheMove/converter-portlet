package eu.citadel.liferay.extendedmvc;

/**
 * @author ttrapanese
 */
public class ExtControllerMethodException extends Exception {
	private static final long serialVersionUID = -3235345363455657475L;

	public ExtControllerMethodException() {
		super();
	}

	public ExtControllerMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExtControllerMethodException(String message) {
		super(message);
	}

	public ExtControllerMethodException(Throwable cause) {
		super(cause);
	}

}
