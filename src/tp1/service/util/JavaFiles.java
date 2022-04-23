package tp1.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.service.util.Files;
import tp1.api.service.util.Result;

public class JavaFiles implements Files {

	@Override
	public Result<Void> writeFile(String fileId, byte[] data, String token) {
		
		return null;
	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {
		
		return null;
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {
		
		return null;
	}
	
	@Override
	public void writeFile(String fileId, byte[] data, String token) {

		try {

			File file = new File(FILEPATH + fileId);
			file.createNewFile();
			try (FileOutputStream stream = new FileOutputStream(FILEPATH + fileId)) {
				stream.write(data);
				stream.close(); // Provavelmente redudante ja que o try-with-resources fecha, mas pelo sim pelo
				// nao, ca esta
			}

			synchronized (fileData) {
				fileData.put(fileId, file);
			}

		} catch (IOException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public void deleteFile(String fileId, String token) {

		try {

			synchronized(fileData) {
				File file = fileData.get(fileId);
				file.delete();
				fileData.remove(fileId);
			}

		} catch (Exception e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public byte[] getFile(String fileId, String token) {

		File file;
		synchronized (fileData) {

			file = fileData.get(fileId);

			if (file == null)
				throw new WebApplicationException(Status.NOT_FOUND);

			try {
				// File file = fileData.get(FILEPATH + fileId);
				byte[] fileContent;

				try (FileInputStream stream = new FileInputStream(file)) {
					fileContent = stream.readAllBytes();
					stream.close();
				}

				return fileContent;

			} catch (IOException e) {
				throw new WebApplicationException(e,Status.INTERNAL_SERVER_ERROR);
			}
		}

	}

	@Override
	public Integer deleteAllFiles(String userId, String token) {

		if (userId == null)
			throw new WebApplicationException(Status.BAD_REQUEST);

		Set<String> filesToDelete = new HashSet<>();
		int count = 0;

		synchronized (fileData) {

			for (Entry<String, File> k : fileData.entrySet()) {

				String fileId = k.getKey().toString();
				String fileUserId = fileId.split(FILEIDSPLIT)[0];

				if (fileUserId.equals(userId)) {
					filesToDelete.add(fileId);
					File file = (File)k.getValue();
					file.deleteOnExit();
				}
			}
			
			
			for (String id : filesToDelete) {
				fileData.remove(id);
				count++;
			} 
		}
		return count;

	}

}
