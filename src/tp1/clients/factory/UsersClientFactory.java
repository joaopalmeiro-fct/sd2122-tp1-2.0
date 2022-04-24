package tp1.clients.factory;

import java.net.MalformedURLException;
import java.net.URI;


import tp1.api.clients.UsersClient;

import tp1.clients.rest.RestUsersClient;
import tp1.clients.soap.SoapUsersClient;

public class UsersClientFactory {
	
	RestUsersClient restUsersClient;
	SoapUsersClient soapUsersClient;
	
	public UsersClientFactory () {
		restUsersClient = new RestUsersClient();
		soapUsersClient = new SoapUsersClient();
	}

	public UsersClient getClient(URI uri) throws MalformedURLException {

		//var serverURI = ; // use discovery to find a uri of the Users service;

		if (uri.toString().endsWith("rest")) {
			restUsersClient.redifineURI(uri);
			return restUsersClient;
		}
		else{
			soapUsersClient.redifineURI(uri);
			return soapUsersClient;
		}
	}

}
