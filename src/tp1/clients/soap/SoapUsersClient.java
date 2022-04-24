package tp1.clients.soap;


import java.net.MalformedURLException;
import java.net.URI;


import jakarta.xml.ws.BindingProvider;
import tp1.api.clients.SoapClient;
import tp1.api.clients.UsersClient;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;

public class SoapUsersClient extends SoapClient implements UsersClient {

	SoapUsers users;

	public SoapUsersClient(String serverUrl) throws MalformedURLException {
		super(SoapUsers.NAMESPACE, SoapUsers.NAME, serverUrl);
		users = service.getPort(tp1.api.service.soap.SoapUsers.class);
		super.setClientTimeouts((BindingProvider)users);
	}

	public SoapUsersClient() {
		super(SoapUsers.NAMESPACE, SoapUsers.NAME);
	}
	
	public void redifineURI (URI uri) throws MalformedURLException{
		super.redifineURI(uri);
		users = service.getPort(tp1.api.service.soap.SoapUsers.class);
		super.setClientTimeouts((BindingProvider)users);
	}
	
	@Override
	public Result<Void> authenticateUser(String userId, String password) {
		return super.reTry(() -> {
			return clt_authenticateUser(userId, password);
		});
	}
	
	@Override
	public Result<Void> checkUserExistence(String userId) {
		return super.reTry(() -> {
			return clt_checkUserExistence(userId);
		});
	}
	
	
	//-------------------------------------------------------------------------------------------
	
	private Result<Void> clt_authenticateUser(String userId, String password) {
		try {
			users.authenticateUser(userId, password);
			return Result.ok();
		} catch( UsersException x ) {
			//x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

	private Result<Void> clt_checkUserExistence(String userId) {
		try {
			users.checkUserExistence(userId);
			return Result.ok();
		} catch( UsersException x ) {
			//x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

}
