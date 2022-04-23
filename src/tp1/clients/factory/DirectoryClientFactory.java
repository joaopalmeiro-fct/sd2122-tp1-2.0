package tp1.clients.factory;

import java.io.IOException;
import java.net.URI;

import tp1.api.clients.DirectoryClient;
import tp1.api.clients.UsersClient;
import tp1.clients.rest.RestDirectoryClient;
import tp1.clients.rest.RestUsersClient;
import tp1.clients.soap.SoapDirectoryClient;
import tp1.clients.soap.SoapUsersClient;

public class DirectoryClientFactory {

	RestDirectoryClient restDirectoryClient;
	SoapDirectoryClient soapDirectoryClient;
	
	public DirectoryClientFactory () throws IOException {
		restDirectoryClient = new RestDirectoryClient();
		soapDirectoryClient = new SoapDirectoryClient();
	}

	public DirectoryClient getClient(URI uri) throws IOException {

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
