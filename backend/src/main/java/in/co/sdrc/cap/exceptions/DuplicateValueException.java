package in.co.sdrc.cap.exceptions;

public class DuplicateValueException extends RuntimeException {

	private static final long serialVersionUID = 6421618822528929997L;

	public DuplicateValueException() {
		super();
	}

	public DuplicateValueException(String args) {
		super(args);
	}

	public DuplicateValueException(Throwable args) {
		super(args);
	}
}

