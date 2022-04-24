package tp1.clients.factory;

import java.net.MalformedURLException;
import java.net.URI;

import tp1.api.clients.DirectoryClient;
import tp1.clients.rest.RestDirectoryClient;

import tp1.clients.soap.SoapDirectoryClient;


public class DirectoryClientFactory {

	RestDirectoryClient restDirectoryClient;
	SoapDirectoryClient soapDirectoryClient;
	
	public DirectoryClientFactory() {
		
		restDirectoryClient = new RestDirectoryClient();
		soapDirectoryClient = new SoapDirectoryClient();
		
	}

	public DirectoryClient getClient(URI uri) throws MalformedURLException {

		//var serverURI = ; // use discovery to find a uri of the Users service;

		if (uri.toString().endsWith("rest")) {
			restDirectoryClient.redifineURI(uri);
			return restDirectoryClient;
		}
		else{
			soapDirectoryClient.redifineURI(uri);
			return soapDirectoryClient;
		}
	}
}
