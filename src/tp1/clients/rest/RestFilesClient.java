package tp1.clients.rest;

import java.net.URI;
//import java.util.logging.Logger;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tp1.api.clients.RestClient;
import tp1.api.service.rest.RestFiles;

public class RestFilesClient extends RestClient {
	WebTarget target;

	public RestFilesClient(URI serverURI) {
		super(serverURI);
		target = client.target(serverURI).path(RestFiles.PATH);
	}
	
	public RestFilesClient() {
		super(null);
	}
	
	public void redifineURI(URI serverURI) {
		this.serverURI = serverURI;
		target = client.target(serverURI).path(RestFiles.PATH);
	}

	// private static Logger Log = Logger.getLogger(FilesClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public Response writeFile(byte[] data, String fileId) {
		return super.reTry(() -> {
			return clt_writeFile(data, fileId);
		});
	}

	public byte[] getFile(String fileId) {
		return super.reTry(() -> {
			return clt_getFile(fileId);
		});
	}

	public Response deleteFile(String fileId) {
		return super.reTry(() -> {
			return clt_deleteFile(fileId);
		});
	}

	public Response deleteAllFiles(String userId) {
		return super.reTry(() -> {
			return clt_deleteAllFiles(userId);
		});
	}

	// -------------------------------------------------------------------------------------------

	private Response clt_writeFile(byte[] data, String fileId) {

		Response r = target.path(fileId)
				// .queryParam(uma token)
				.request().post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));

		return r;
	}

	private byte[] clt_getFile(String fileId) {

		Response r = target.path(fileId)
				// .queryParam(uma token)
				.request().get();

		return r.readEntity(byte[].class);
	}

	private Response clt_deleteFile(String fileId) {

		Response r = target.path(fileId)
				// .queryParam(uma token)
				.request().delete();

		return r;
	}

	private Response clt_deleteAllFiles(String userId) {

		Response r = target.path(RestFiles.DELETEALL_PATH + userId)
				// .queryParam(uma token)
				.request()
				.accept(MediaType.TEXT_PLAIN).delete();
		
		return r;
	}

}
