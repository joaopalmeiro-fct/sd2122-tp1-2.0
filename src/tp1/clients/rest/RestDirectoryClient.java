package tp1.clients.rest;

import java.net.URI;
import java.util.logging.Logger;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.clients.DirectoryClient;
import tp1.api.clients.FilesClient;
import tp1.api.clients.RestClient;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.rest.RestUsers;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;

public class RestDirectoryClient extends RestClient implements DirectoryClient {

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

	@Override
	public Result<Void> deleteAllFiles(String userId, String password) {
		return super.reTry(() -> {
			return clt_deleteFiles(userId, password);
		});
	}

//------------------------------------------------------------------------------------------------

	private Result<Void> clt_deleteFiles(String userId, String password) {

		Response r = target.path(RestDirectory.DELETEALL_PATH + userId)
				.queryParam(RestUsers.PASSWORD, password)
				.request().delete();
		
		int status = r.getStatus();
		
		if (status == Status.NO_CONTENT.getStatusCode())
			return Result.ok();
		else
			return Result.error(ErrorCode.errorCodeOfStatus(status));
	}
}