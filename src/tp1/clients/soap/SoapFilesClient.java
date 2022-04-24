package tp1.clients.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import tp1.api.clients.FilesClient;
import tp1.api.clients.SoapClient;
import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapFiles;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;

public class SoapFilesClient extends SoapClient implements FilesClient {

	SoapFiles files;
	
	public SoapFilesClient(String serverUrl) throws IOException {
		super(SoapFiles.NAMESPACE, SoapFiles.NAME, serverUrl);
		files = service.getPort(tp1.api.service.soap.SoapFiles.class);
	}
	
	public SoapFilesClient(){
		super(SoapFiles.NAMESPACE, SoapFiles.NAME);
	}
	
	public void redifineURI (URI uri) throws MalformedURLException{
		super.redifineURI(uri);
		files = service.getPort(tp1.api.service.soap.SoapFiles.class);
	}

	@Override
	public Result<Void> writeFile(String fileId, byte[] data, String token) {

		try {
			files.writeFile(fileId, data, token);
			return Result.ok();
		} catch( FilesException x ) {
			x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {

		try {
			files.deleteFile(fileId, token);
			return Result.ok();
		} catch( FilesException x ) {
			x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {

		try {
			var result = files.getFile(fileId, token);
			return Result.ok(result);
		} catch( FilesException x ) {
			x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

	@Override
	public Result<Integer> deleteAllFiles(String userId, String token) {

		try {
			var result = files.deleteAllFiles(userId, token);
			return Result.ok(result);
		} catch( FilesException x ) {
			x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

}
