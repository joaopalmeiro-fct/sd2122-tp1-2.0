package tp1.clients.factory;

import java.io.IOException;
import java.net.URI;

import tp1.api.clients.RestClient;
import tp1.api.clients.UsersClient;
import tp1.api.service.util.Users;
import tp1.clients.rest.RestUsersClient;
import tp1.clients.soap.SoapUsersClient;

public class UsersClientFactory {
	
	RestUsersClient restUsersClient;
	SoapUsersClient soapUsersClient;
	
	public UsersClientFactory () throws IOException {
		restUsersClient = new RestUsersClient();
		soapUsersClient = new SoapUsersClient();
	}

	public UsersClient getClient(URI uri) throws IOException {

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
