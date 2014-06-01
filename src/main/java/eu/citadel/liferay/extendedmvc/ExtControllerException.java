package eu.citadel.liferay.extendedmvc;

/**
 * @author ttrapanese
 */
public class ExtControllerException extends Exception {
	private static final long serialVersionUID = -1433849495191906429L;

	public ExtControllerException() {
		super();
	}

	public ExtControllerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExtControllerException(String message) {
		super(message);
	}

	public ExtControllerException(Throwable cause) {
		super(cause);
	}

}
