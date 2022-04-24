package tp1.api.service.util;

import java.net.URI;

/**
 * 
 * Represents the result of an operation, either wrapping a result of the given type,
 * or an error.
 * 
 * @author smd
 *
 * @param <T> type of the result value associated with success
 */
public interface Result<T> {

	/**
	 * 
	 * @author smd
	 *
	 * Service errors:
	 * OK - no error, implies a non-null result of type T, except for for Void operations
	 * CONFLICT - something is being created but already exists
	 * NOT_FOUND - an access occurred to something that does not exist
	 * INTERNAL_ERROR - something unexpected happened
	 */
	enum ErrorCode{ 
		
		OK(200), NO_CONTENT(204), CONFLICT(409), NOT_FOUND(404), BAD_REQUEST(400), FORBIDDEN(403), INTERNAL_ERROR(500), NOT_IMPLEMENTED(501), UNKNOWN(520); 
	
		private int errorCodeNum;
		
		ErrorCode (int errorCode){
			this.errorCodeNum = errorCode;
		}
		
		int getErrorCodeNum() {
			return errorCodeNum;
		}
		
		public static ErrorCode errorCodeOfStatus(int status) {
			switch(status) {
			case 200 : return ErrorCode.OK;
			case 204 : return ErrorCode.NO_CONTENT;
			case 409 : return ErrorCode.CONFLICT;
			case 404 : return ErrorCode.NOT_FOUND;
			case 400 : return ErrorCode.BAD_REQUEST;
			case 403 : return ErrorCode.FORBIDDEN;
			case 500 : return ErrorCode.INTERNAL_ERROR;
			case 501 : return ErrorCode.NOT_IMPLEMENTED;
			default:
				return ErrorCode.UNKNOWN;
			
			}
		}
	};
	
	
	/**
	 * Tests if the result is an error.
	 */
	boolean isOK();
	
	/**
	 * obtains the payload value of this result
	 * @return the value of this result.
	 */
	T value();

	/**
	 *
	 * obtains the error code of this result
	 * @return the error code
	 * 
	 */
	ErrorCode error();
	
	/**
	 * Convenience method for returning non error results of the given type
	 * @param Class of value of the result
	 * @return the value of the result
	 */
	static <T> Result<T> ok( T result ) {
		return new OkResult<>(result);
	}

	/**
	 * Convenience method for returning non error results without a value
	 * @return non-error result
	 */
	static <T> OkResult<T> ok() {
		return new OkResult<>(null);	
	}
	
	/**
	 * Convenience method for returning non error results with or without a value and a URI
	 * @return non-error result
	 */
	static <T> Result<T> ok( T result, URI uri ) {
		return new OkResult<>(result, uri);
	}

	/**
	 * Convenience method used to return an error 
	 * @return
	 */
	static <T> ErrorResult<T> error(ErrorCode error) {
		return new ErrorResult<>(error);		
	}
	
	/**
	 * Convenience method used to error code number 
	 * @return
	 */
	int getErrorCodeNum();
	
	/**
	 * Convenience method used to return an uri 
	 * @return
	 */
	URI uri();
	
}

class OkResult<T> implements tp1.api.service.util.Result<T> {

	final URI uri;
	final T result;
	
	OkResult(T result) {
		this.result = result;
		this.uri = null;
	}
	
	OkResult(T result, URI uri) {
		this.result = result;
		this.uri = uri;
	}
	
	@Override
	public URI uri() {
		return uri;
	}
	
	@Override
	public boolean isOK() {
		return true;
	}

	@Override
	public T value() {
		return result;
	}

	@Override
	public ErrorCode error() {
		return ErrorCode.OK;
	}
	
	public String toString() {
		return "(OK, " + value() + ")";
	}

	@Override
	public int getErrorCodeNum() {
		if (result!=null)
			return ErrorCode.OK.getErrorCodeNum();
		else
			return ErrorCode.NO_CONTENT.getErrorCodeNum();
			
	}
}

class ErrorResult<T> implements tp1.api.service.util.Result<T> {

	final ErrorCode error;
	
	ErrorResult(ErrorCode error) {
		this.error = error;
	}
	
	@Override
	public boolean isOK() {
		return false;
	}

	@Override
	public T value() {
		throw new RuntimeException("Attempting to extract the value of an Error: " + error());
	}

	@Override
	public ErrorCode error() {
		return error;
	}
	
	public String toString() {
		return "(" + error() + ")";		
	}

	@Override
	public int getErrorCodeNum() {
		return error.getErrorCodeNum();
	}

	@Override
	public URI uri() {
		return null;
	}
}