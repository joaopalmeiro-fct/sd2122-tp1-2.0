package tp1.service.soap;

import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapFiles;

public class FilesWebService implements SoapFiles {

	@Override
	public byte[] getFile(String fileId, String token) throws FilesException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteFile(String fileId, String token) throws FilesException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeFile(String fileId, byte[] data, String token) throws FilesException {
		// TODO Auto-generated method stub
		
	}

}
