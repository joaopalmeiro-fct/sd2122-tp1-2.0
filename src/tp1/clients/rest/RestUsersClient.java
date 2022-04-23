package tp1.clients.rest;


import java.net.URI;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.clients.RestClient;
import tp1.api.clients.UsersClient;
import tp1.api.service.rest.RestUsers;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;

public class RestUsersClient extends RestClient implements UsersClient {

	private static final String AUTHENTICATE_PATH = "authenticate/";
	private static final String EXISTANCE_PATH = "exists/";
	WebTarget target;

	public RestUsersClient(URI serverURI) {
		super(serverURI);
		target = client.target(serverURI).path(RestUsers.PATH);
	}
	
	public RestUsersClient() {
		super(null);
	}
	
	public void redifineURI(URI serverURI) {
		this.serverURI = serverURI;
		target = client.target(serverURI).path(RestUsers.PATH);
	}

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	@Override
	public Result<Void> authenticateUser(String userId, String password) {
		return super.reTry(() -> {
			return clt_authenticateUser(userId, password);
		});
	}
	
	@Override
	public Result<Void> checkUserExistence (String userId) {
		return super.reTry(() -> {
			return clt_checkUserExistence(userId);
		});
	}

// ------------------------------------------------------------------------------------------------

	private Result<Void> clt_authenticateUser(String userId, String password) {

		Response r = target.path(AUTHENTICATE_PATH + userId)
				.queryParam(RestUsers.PASSWORD, password)
				.request()
				.get();

		int status = r.getStatus();
		
		if (status == Status.NO_CONTENT.getStatusCode())
			return Result.ok();
		else
			return Result.error(ErrorCode.valueOf((Status.fromStatusCode(status).toString())));
	}
	
	private Result<Void> clt_checkUserExistence(String userId) {

		Response r = target.path(EXISTANCE_PATH + userId)
				.request()
				.get();

		int status = r.getStatus();
		
		if (status == Status.NO_CONTENT.getStatusCode())
			return Result.ok();
		else
			return Result.error(ErrorCode.valueOf((Status.fromStatusCode(status).toString())));
	}

}