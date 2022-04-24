package tp1.service.rest;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Files;

import tp1.discovery.Discovery;
import tp1.service.util.JavaFiles;


public class FilesResource implements RestFiles {
	
	Discovery discovery;
	Files filesImpl;

	public FilesResource (Discovery discovery) {
		this.filesImpl = new JavaFiles(discovery);
		this.discovery = discovery;
	}

	@Override
	public void writeFile(String fileId, byte[] data, String token) {
		
		var result = filesImpl.writeFile(fileId, data, token);

		if (result.isOK())
			return;
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
	}

	@Override
	public void deleteFile(String fileId, String token) {
		
		var result = filesImpl.deleteFile(fileId, token);

		if (result.isOK())
			return;
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
	}

	@Override
	public byte[] getFile(String fileId, String token) {
		
		var result = filesImpl.getFile(fileId, token);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
	}

	@Override
	public Integer deleteAllFiles(String userId, String token) {
		
		var result = filesImpl.deleteAllFiles(userId, token);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
	}

}
