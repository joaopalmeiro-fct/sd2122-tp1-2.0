package tp1.clients.rest;

import java.net.URI;
import java.util.logging.Logger;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import tp1.api.clients.RestClient;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.rest.RestUsers;

public class RestDirectoryClient extends RestClient {

	//private static final String DELETEALL_PATH = "deleteall/";
	WebTarget target;
	
	public RestDirectoryClient(URI serverURI) {
		super(serverURI);
		target = client.target(serverURI).path(RestDirectory.PATH);
	}
	
	public RestDirectoryClient() {
		super(null);
	}

	public void redifineURI(URI serverURI) {
		this.serverURI = serverURI;
		target = client.target(serverURI).path(RestDirectory.PATH);
	}

	//private static Logger Log = Logger.getLogger(UsersClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public Response deleteFiles(String userId, String password) {
		return super.reTry(() -> {
			return clt_deleteFiles(userId, password);
		});
	}

//------------------------------------------------------------------------------------------------

	private Response clt_deleteFiles(String userId, String password) {

		Response r = target.path(RestDirectory.DELETEALL_PATH + userId)
				.queryParam(RestUsers.PASSWORD, password)
				.request().delete();

		return r;
	}
}