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
		return super.reTry(() -> {
			return clt_writeFile(fileId, data, token);
		});
	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {
		return super.reTry(() -> {
			return clt_deleteFile(fileId, token);
		});
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {
		return super.reTry(() -> {
			return clt_getFile(fileId, token);
		});
	}

	@Override
	public Result<Integer> deleteAllFilesF(String userId, String token) {
		return super.reTry(() -> {
			return clt_deleteAllFilesF(userId, token);
		});
	}

	//---------------------------------------------------------------------------------------------

	private Result<Void> clt_writeFile(String fileId, byte[] data, String token) {

		try {
			files.writeFile(fileId, data, token);
			return Result.ok();
		} catch( FilesException x ) {
			x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

	private Result<Void> clt_deleteFile(String fileId, String token) {

		try {
			files.deleteFile(fileId, token);
			return Result.ok();
		} catch( FilesException x ) {
			x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

	private Result<byte[]> clt_getFile(String fileId, String token) {

		try {
			var result = files.getFile(fileId, token);
			return Result.ok(result);
		} catch( FilesException x ) {
			x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

	private Result<Integer> clt_deleteAllFilesF(String userId, String token) {

		try {
			var result = files.deleteAllFilesF(userId, token);
			return Result.ok(result);
		} catch( FilesException x ) {
			x.printStackTrace();
			return Result.error(ErrorCode.valueOf(x.getMessage()));
		}
	}

}
