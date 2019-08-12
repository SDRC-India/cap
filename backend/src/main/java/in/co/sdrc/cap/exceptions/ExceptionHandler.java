package in.co.sdrc.cap.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.sdrc.usermgmt.core.util.ApiError;
import org.sdrc.usermgmt.core.util.RestExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author Subham Ashish(subham@sdrc.co.in)
 */

@Component
public class ExceptionHandler extends RestExceptionHandler {

	@org.springframework.web.bind.annotation.ExceptionHandler(InvalidFileException.class)
	protected ResponseEntity<Object> handleInvalidFileException(InvalidFileException ex, HttpServletRequest request) {
		String error = ex.getMessage();
		return buildResponseEntity(new ApiError(HttpStatus.NOT_ACCEPTABLE, error, ex));
	}
	
	@org.springframework.web.bind.annotation.ExceptionHandler(IncorrectDataFormatException.class)
	protected ResponseEntity<Object> handleIncorrectDataFormatException(IncorrectDataFormatException ex, HttpServletRequest request) {
		String error = ex.getMessage();
		return buildResponseEntity(new ApiError(HttpStatus.NOT_ACCEPTABLE, error, ex));
	}
	
	@org.springframework.web.bind.annotation.ExceptionHandler(SubmissionException.class)
	protected ResponseEntity<Object> handleSubmissiontException(SubmissionException ex, HttpServletRequest request) {
		String error = ex.getMessage();
		return buildResponseEntity(new ApiError(HttpStatus.NOT_ACCEPTABLE, error, ex));
	}
	
	@org.springframework.web.bind.annotation.ExceptionHandler(DuplicateValueException.class)
	protected ResponseEntity<Object> handleDuplicateValueException(SubmissionException ex, HttpServletRequest request) {
		String error = ex.getMessage();
		return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
	}
}
