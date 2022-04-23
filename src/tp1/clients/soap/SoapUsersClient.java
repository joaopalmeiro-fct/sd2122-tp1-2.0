package tp1.clients.soap;

import java.io.IOException;

import jakarta.ws.rs.core.Response;
import tp1.api.clients.SoapClient;
import tp1.api.clients.UsersClient;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.util.Result;

public class SoapUsersClient extends SoapClient implements UsersClient {

	SoapUsers users;

	public SoapUsersClient(String serverUrl) throws IOException {
		super(SoapUsers.NAMESPACE, SoapUsers.NAME, serverUrl);
		users = service.getPort(tp1.api.service.soap.SoapUsers.class);
	}

	public SoapUsersClient() throws IOException {
		super(SoapUsers.NAMESPACE, SoapUsers.NAME, "");
		users = service.getPort(tp1.api.service.soap.SoapUsers.class);
	}
	
	@Override
	public Result<Void> authenticateUser(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> checkUserExistence(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
