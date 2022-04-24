package tp1.clients.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import jakarta.xml.ws.Service;
import tp1.api.clients.DirectoryClient;
import tp1.api.clients.SoapClient;
import tp1.api.service.soap.DirectoryException;
import tp1.api.service.soap.SoapDirectory;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;

public class SoapDirectoryClient extends SoapClient implements DirectoryClient {

	SoapDirectory directory;

	public SoapDirectoryClient(String serverUrl) throws MalformedURLException {
		super(SoapDirectory.NAMESPACE, SoapDirectory.NAME, serverUrl);
		directory = service.getPort(tp1.api.service.soap.SoapDirectory.class);
	}

	public SoapDirectoryClient() {
		super(SoapDirectory.NAMESPACE, SoapDirectory.NAME);
	}
	
	public void redifineURI (URI uri) throws MalformedURLException{
		super.redifineURI(uri);
		directory = service.getPort(tp1.api.service.soap.SoapDirectory.class);
	}
	
	@Override
	public Result<Void> deleteAllFiles(String userId, String password) {
		try {
			directory.deleteAllFiles(userId, password);
			return Result.ok();
		} catch( DirectoryException x ) {
			x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}
	
}
