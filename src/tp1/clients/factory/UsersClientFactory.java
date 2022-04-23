package tp1.clients.factory;

import tp1.api.service.util.Users;

public class UsersClientFactory {

	public static Users getClient() {
		
	       var serverURI = ; // use discovery to find a uri of the Users service;
	       
	       if( serverURI.endsWith("rest")
	          return new RestUsersClient( serverURI );
	       else
	          return new SoapUsersClient( serverURI );
	    }
	
}
