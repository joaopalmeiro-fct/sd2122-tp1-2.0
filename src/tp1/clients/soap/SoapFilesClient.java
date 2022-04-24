package tp1.clients.soap;

import java.io.IOException;

import tp1.api.clients.FilesClient;
import tp1.api.clients.SoapClient;
import tp1.api.service.soap.SoapFiles;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.util.Result;

public class SoapFilesClient extends SoapClient implements FilesClient {

	SoapFiles files;
	
	public SoapFilesClient(String serverUrl) throws IOException {
		super(SoapFiles.NAMESPACE, SoapFiles.NAME, serverUrl);
		files = service.getPort(tp1.api.service.soap.SoapFiles.class);
	}
	
	public SoapFilesClient(){
		super(SoapFiles.NAMESPACE, SoapFiles.NAME);
	}

	@Override
	public Result<Void> writeFile(String fileId, byte[] data, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Integer> deleteAllFiles(String userId, String token) {
		// TODO Auto-generated method stub
		return null;
	}

}
