package tp1.clients.rest;

import java.net.URI;
//import java.util.logging.Logger;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.clients.FilesClient;
import tp1.api.clients.RestClient;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;

public class RestFilesClient extends RestClient implements FilesClient {
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

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	@Override
	public Result<Void> writeFile(String fileId, byte[] data, String token) {
		return super.reTry(() -> {
			return clt_writeFile(data, fileId);
		});
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {
		return super.reTry(() -> {
			return clt_getFile(fileId);
		});
	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {
		return super.reTry(() -> {
			return clt_deleteFile(fileId);
		});
	}

	@Override
	public Result<Integer> deleteAllFilesF(String userId, String token) {
		return super.reTry(() -> {
			return clt_deleteAllFiles(userId);
		});
	}

	// -------------------------------------------------------------------------------------------

	private Result<Void> clt_writeFile(byte[] data, String fileId) {

		Response r = target.path(fileId)
				// .queryParam(uma token)
				.request().post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));
		
		int status = r.getStatus();
		if (status == Status.NO_CONTENT.getStatusCode())
			return Result.ok();
		else
			return Result.error(ErrorCode.errorCodeOfStatus(status));
		
	}

	private Result<byte[]> clt_getFile(String fileId) {

		Response r = target.path(fileId)
				// .queryParam(uma token)
				.request().get();
		
		int status = r.getStatus();
		if (status == Status.OK.getStatusCode() && r.hasEntity())
			return Result.ok(r.readEntity(byte[].class));
		else
			return Result.error(ErrorCode.errorCodeOfStatus(status));

	}

	private Result<Void> clt_deleteFile(String fileId) {

		Response r = target.path(fileId)
				// .queryParam(uma token)
				.request().delete();
		
		int status = r.getStatus();
		if (status == Status.NO_CONTENT.getStatusCode())
			return Result.ok();
		else
			return Result.error(ErrorCode.errorCodeOfStatus(status));

	}

	private Result<Integer> clt_deleteAllFiles(String userId) {

		Response r = target.path(RestFiles.DELETEALL_PATH + userId)
				// .queryParam(uma token)
				.request()
				.accept(MediaType.TEXT_PLAIN).delete();
		
		int status = r.getStatus();
		if (status == Status.OK.getStatusCode() && r.hasEntity())
			return Result.ok(r.readEntity(Integer.class));
		else
			return Result.error(ErrorCode.errorCodeOfStatus(status));
		
	}

}
