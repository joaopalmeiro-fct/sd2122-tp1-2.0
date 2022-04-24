package tp1.clients.factory;

import java.net.MalformedURLException;
import java.net.URI;


import tp1.api.clients.FilesClient;
import tp1.clients.rest.RestFilesClient;
import tp1.clients.soap.SoapFilesClient;



public class FilesClientFactory {
	RestFilesClient restFilesClient;
	SoapFilesClient soapFilesClient;

public FilesClientFactory () {
	restFilesClient = new RestFilesClient();
	soapFilesClient = new SoapFilesClient();
}

public FilesClient getClient(URI uri) throws MalformedURLException {

	if (uri.toString().endsWith("rest")) {
		restFilesClient.redifineURI(uri);
		return restFilesClient;
	}
	else { 
		soapFilesClient.redifineURI(uri);
		return soapFilesClient;
	}
}
}
