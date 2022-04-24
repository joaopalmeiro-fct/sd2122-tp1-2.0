package tp1.clients.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import jakarta.ws.rs.core.Response;
import tp1.api.clients.SoapClient;
import tp1.api.clients.UsersClient;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;

public class SoapUsersClient extends SoapClient implements UsersClient {

	SoapUsers users;

	public SoapUsersClient(String serverUrl) throws IOException {
		super(SoapUsers.NAMESPACE, SoapUsers.NAME, serverUrl);
		users = service.getPort(tp1.api.service.soap.SoapUsers.class);
	}

	public SoapUsersClient() {
		super(SoapUsers.NAMESPACE, SoapUsers.NAME);
	}
	
	public void redifineURI (URI uri) throws MalformedURLException{
		super.redifineURI(uri);
		users = service.getPort(tp1.api.service.soap.SoapUsers.class);
	}
	
	@Override
	public Result<Void> authenticateUser(String userId, String password) {
		try {
			users.authenticateUser(userId, password);
			return Result.ok();
		} catch( UsersException x ) {
			//x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

	@Override
	public Result<Void> checkUserExistence(String userId) {
		try {
			users.checkUserExistence(userId);
			return Result.ok();
		} catch( UsersException x ) {
			//x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

}
