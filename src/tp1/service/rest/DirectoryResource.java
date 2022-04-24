package tp1.service.rest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.FileInfo;
import tp1.api.clients.FilesClient;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.soap.DirectoryException;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;
import tp1.clients.factory.FilesClientFactory;
import tp1.discovery.Discovery;
import tp1.service.util.JavaDirectory;

public class DirectoryResource implements RestDirectory{
	
	private FilesClientFactory filesClientFactory;
	final Directory directoryImpl;
	Discovery discovery;
	
	public DirectoryResource(Discovery discovery) {
		this.discovery = discovery;
		filesClientFactory = new FilesClientFactory();
		directoryImpl = new JavaDirectory(discovery, filesClientFactory);
		
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
	//TODO - MUDAR ISTO DE FORMA A DAR REDIRECT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!DKJAASLHDALSJDLKASJDKLASJDLKASJDLKASJDLAJDLKASJDLKJL
	public byte[] getFile(String filename, String userId, String accUserId, String password) {
		var result = directoryImpl.getFile(filename, userId, accUserId, password);
		
		/*
		 * Nao testado:
		 */
		if (result.isOK()) {
			String URIandFileID[] = result.uri().toString().split("/"+tp1.server.util.ServiceName.FILES.getServiceName()+"/");
			if (URIandFileID[0].endsWith("rest")) {
				throw new WebApplicationException(Response.temporaryRedirect(result.uri()).build());
			}
			else {
				URI uri;
	        	try { uri = new URI(URIandFileID[0]); } catch (URISyntaxException e) {throw new WebApplicationException(e.getMessage());}
		    	String fileId = URIandFileID[1];
		    	Result<byte[]> r;
		    	
		    	synchronized(filesClientFactory) {
					FilesClient client;
					try {
						client = filesClientFactory.getClient(uri);
					}
					catch (MalformedURLException e) {
						throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
					}
					r = client.getFile(fileId,"");
					if (!r.isOK())
						throw new WebApplicationException(Status.fromStatusCode(r.getErrorCodeNum()));
					 return r.value();
				}
			}
		}
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));
			
    	
  
    	
    	
    	
		
		
		
		
		//----
		
		
		//FUNCIONAL
		/*if (result.isOK())
			throw new WebApplicationException(Response.temporaryRedirect(result.uri()).build());
		else
			throw new WebApplicationException(Status.fromStatusCode(result.getErrorCodeNum()));*/
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
