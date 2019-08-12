package in.co.sdrc.cap.exceptions;


/**
 * @author Subham Ashish
 *
 */
public class SubmissionException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2416421128522691815L;

	
	public SubmissionException() {
		super();
	}
	
	public SubmissionException(String args) {
		super(args);
	}
	
	public SubmissionException(Throwable args) {
		super(args);
	}
}
