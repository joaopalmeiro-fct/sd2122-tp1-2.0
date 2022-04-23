package tp1.service.soap;

import java.util.List;

import tp1.api.FileInfo;
import tp1.api.service.soap.DirectoryException;
import tp1.api.service.soap.SoapDirectory;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Users;
import tp1.discovery.Discovery;
import tp1.service.util.JavaDirectory;
import tp1.service.util.JavaUsers;

public class DirectoryWebService implements SoapDirectory {

	Discovery discovery;
	
	final Directory directoryImpl;
	
	public DirectoryWebService (Discovery discovery) {
		this.discovery = discovery;
		directoryImpl = new JavaDirectory(discovery);
	}
	
	@Override
	public FileInfo writeFile(String filename, byte[] data, String userId, String password) throws DirectoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteFile(String filename, String userId, String password) throws DirectoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shareFile(String filename, String userId, String userIdShare, String password)
			throws DirectoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unshareFile(String filename, String userId, String userIdShare, String password)
			throws DirectoryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] getFile(String filename, String userId, String accUserId, String password) throws DirectoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FileInfo> lsFile(String userId, String password) throws DirectoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
