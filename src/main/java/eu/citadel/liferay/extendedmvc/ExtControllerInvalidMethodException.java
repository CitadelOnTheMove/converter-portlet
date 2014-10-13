package eu.citadel.liferay.extendedmvc;

/**
 * @author ttrapanese
 */
public class ExtControllerInvalidMethodException extends Exception {
	private static final long serialVersionUID = -3235345363455657475L;

	public ExtControllerInvalidMethodException() {
		super();
	}

	public ExtControllerInvalidMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExtControllerInvalidMethodException(String message) {
		super(message);
	}

	public ExtControllerInvalidMethodException(Throwable cause) {
		super(cause);
	}

}
