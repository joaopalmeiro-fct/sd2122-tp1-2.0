package tp1.service.soap;

import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapFiles;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Files;
import tp1.discovery.Discovery;
import tp1.service.util.JavaDirectory;
import tp1.service.util.JavaFiles;

public class FilesWebService implements SoapFiles {
	
	Discovery discovery;
	
	final Files filesImpl;
	
	public FilesWebService (Discovery discovery) {
		this.discovery = discovery;
		filesImpl = new JavaFiles(discovery);
	}

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
