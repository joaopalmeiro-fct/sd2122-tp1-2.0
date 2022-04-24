package tp1.api.service.soap;

public class SoapException extends Exception {

	
	public SoapException() {
		super("");
	}

	public SoapException(String errorMessage ) {
		super(errorMessage);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3691625029002800907L;

}
