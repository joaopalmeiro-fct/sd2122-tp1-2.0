package tp1.service.rest;

import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Files;
import tp1.discovery.Discovery;
import tp1.service.util.JavaFiles;

public class FilesResource implements RestFiles {
	
	final Files service;
	
	public FilesResource (Discovery discovery) {
		service = new JavaFiles(discovery);
	}

	@Override
	public void writeFile(String fileId, byte[] data, String token) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteFile(String fileId, String token) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] getFile(String fileId, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer deleteAllFiles(String userId, String token) {
		// TODO Auto-generated method stub
		return null;
	}

}
