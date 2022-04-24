package tp1.service.rest;

import java.util.List;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.FileInfo;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.util.Directory;
import tp1.discovery.Discovery;
import tp1.service.util.JavaDirectory;

public class DirectoryResource implements RestDirectory{
	
	final Directory directoryImpl;
	
	public DirectoryResource(Discovery discovery) {
		directoryImpl = new JavaDirectory(discovery);
	}

	@Override
	public FileInfo writeFile(String filename, byte[] data, String userId, String password) {
		var result = directoryImpl.writeFile(filename, data, userId, password);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
	}

	@Override
	public void deleteFile(String filename, String userId, String password) {
		var result = directoryImpl.deleteFile(filename, userId, password);

		if (result.isOK())
			return;
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
		
	}

	@Override
	public void shareFile(String filename, String userId, String userIdShare, String password) {
		var result = directoryImpl.shareFile(filename, userId, userIdShare, password);

		if (result.isOK())
			return;
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
		
	}

	@Override
	public void unshareFile(String filename, String userId, String userIdShare, String password) {
		var result = directoryImpl.unshareFile(filename, userId, userIdShare, password);

		if (result.isOK())
			return;
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
		
	}

	@Override
	//TODO - MUDAR ISTO DE FORMA A DAR REDIRECT
	public byte[] getFile(String filename, String userId, String accUserId, String password) {
		var result = directoryImpl.getFile(filename, userId, accUserId, password);

		if (result.isOK())
			throw new WebApplicationException(Response.temporaryRedirect(result.uri()).build());
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
	}

	@Override
	public List<FileInfo> lsFile(String userId, String password) {
		var result = directoryImpl.lsFile(userId, password);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
	}

	@Override
	public void deleteAllFiles(String userId, String password) {
		var result = directoryImpl.deleteAllFiles(userId, password);
		if (result.isOK())
			return;
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
		
	}

}
