package tp1.clients.rest;


import java.net.URI;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import tp1.api.clients.RestClient;
import tp1.api.service.rest.RestUsers;

public class RestUsersClient extends RestClient {

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

	public Response authenticateUser(String userId, String password) {
		return super.reTry(() -> {
			return clt_authenticateUser(userId, password);
		});
	}
	
	public Response checkUserExistence (String userId) {
		return super.reTry(() -> {
			return clt_checkUserExistence(userId);
		});
	}

// ------------------------------------------------------------------------------------------------

	private Response clt_authenticateUser(String userId, String password) {

		Response r = target.path(AUTHENTICATE_PATH + userId)
				.queryParam(RestUsers.PASSWORD, password)
				.request()
				.get();

		return r;
	}
	
	private Response clt_checkUserExistence(String userId) {

		Response r = target.path(EXISTANCE_PATH + userId)
				.request()
				.get();

		return r;
	}

}