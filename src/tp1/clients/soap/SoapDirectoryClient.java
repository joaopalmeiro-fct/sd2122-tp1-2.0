package tp1.clients.soap;

import java.io.IOException;
import java.net.MalformedURLException;

import tp1.api.clients.DirectoryClient;
import tp1.api.clients.SoapClient;
import tp1.api.service.soap.SoapDirectory;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.util.Result;

public class SoapDirectoryClient extends SoapClient implements DirectoryClient {

	SoapDirectory directory;

	public SoapDirectoryClient(String serverUrl) throws MalformedURLException {
		super(SoapDirectory.NAMESPACE, SoapDirectory.NAME, serverUrl);
		directory = service.getPort(tp1.api.service.soap.SoapDirectory.class);
	}

	public SoapDirectoryClient() {
		super(SoapDirectory.NAMESPACE, SoapDirectory.NAME);
	}

	@Override
	public Result<Void> deleteAllFiles(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
