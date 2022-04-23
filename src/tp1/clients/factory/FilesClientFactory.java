package tp1.clients.factory;

import java.io.IOException;
import java.net.URI;

import tp1.api.clients.FilesClient;
import tp1.clients.rest.RestFilesClient;
import tp1.clients.soap.SoapFilesClient;



public class FilesClientFactory {
	RestFilesClient restFilesClient;
	SoapFilesClient soapFilesClient;

public FilesClientFactory () throws IOException {
	restFilesClient = new RestFilesClient();
	soapFilesClient = new SoapFilesClient();
}

public FilesClient getClient(URI uri) throws IOException {

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
