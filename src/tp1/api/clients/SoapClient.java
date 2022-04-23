package tp1.api.clients;

import java.io.IOException;
import java.net.URI;
import java.util.function.Supplier;

import javax.xml.namespace.QName;

import com.sun.xml.ws.client.BindingProviderProperties;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;

public class SoapClient {

	protected static final int RETRY_SLEEP = 1;
	protected static final int MAX_RETRIES = 3;

	protected static final int READ_TIMEOUT = 10000;
	protected static final int CONNECT_TIMEOUT = 10000;
	
	protected QName qname;		
	protected Service service;
	
	public SoapClient (String namespace, String name, String serverUrl) throws IOException {
		qname = new QName(namespace, name);
		service = Service.create( URI.create(serverUrl + "?wsdl").toURL(), qname);		
	}
	
	public void redifineURI (String serverUrl) throws IOException {
		service = Service.create( URI.create(serverUrl + "?wsdl").toURL(), qname);	
	}
	
	protected <T> T reTry(Supplier<T> func) {
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				return func.get();
			} catch (WebServiceException x) {
				sleep(RETRY_SLEEP);
			} catch (Exception x) {
				x.printStackTrace();
				break;
			}
		return null;
	}

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException x) { // nothing to do...
		}
	}
	
	static void setClientTimeouts(BindingProvider port ) {
		port.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
		port.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, READ_TIMEOUT);		
	}
}
