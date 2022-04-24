package tp1.discovery.util.exceptions;

@SuppressWarnings("serial")
public class NoUrisFoundException extends Exception {

	public NoUrisFoundException(String service) {
		super("No uris found for service " + service);
	}

	
}
