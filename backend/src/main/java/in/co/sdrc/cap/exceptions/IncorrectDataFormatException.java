package in.co.sdrc.cap.exceptions;


/**
 * @author subham
 *
 */
public class IncorrectDataFormatException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3658612382369827035L;

	public IncorrectDataFormatException() {
		super();
	}

	public IncorrectDataFormatException(String args) {
		super(args);
	}

	public IncorrectDataFormatException(Throwable args) {
		super(args);
	}
}
