package tp1.service.rest;

import java.util.List;

import tp1.api.FileInfo;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.util.Directory;
import tp1.discovery.Discovery;
import tp1.service.util.JavaDirectory;

public class DirectoryResource implements RestDirectory{
	
	final Directory service;
	
	public DirectoryResource(Discovery discovery) {
		service = new JavaDirectory(discovery);
	}

	@Override
	public FileInfo writeFile(String filename, byte[] data, String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteFile(String filename, String userId, String password) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shareFile(String filename, String userId, String userIdShare, String password) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unshareFile(String filename, String userId, String userIdShare, String password) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] getFile(String filename, String userId, String accUserId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FileInfo> lsFile(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

}
