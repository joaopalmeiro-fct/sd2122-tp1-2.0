package tp1.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import jakarta.ws.rs.WebApplicationException;
import tp1.api.service.util.Files;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;
import tp1.discovery.Discovery;
import tp1.server.resources.FilesResource;
i

public class JavaFiles implements Files {
	
	
	private static final String FILEPATH = ".\\files\\";
	private static final String FILEIDSPLIT = "-";
	//private static Logger Log = Logger.getLogger(JavaFiles.class.getName());

	//map that associates the fileId with the file itself
	private final Map<String, File> fileData = new HashMap<String, File>();

	// private static Logger Log = Logger.getLogger(FilesResource.class.getName());

	// Nao necessario por enquanto, sera necessario quando fizer uso do token
	private Discovery discovery;

	public JavaFiles(Discovery discovery) {
		this.discovery = discovery;
		//Log.setLevel(Level.ALL);
		//Log.addHandler(new StreamHandler(System.out, new SimpleFormatter()));
	}

	/*@Override
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
	}*/
	
	@Override
	public Result<Void> writeFile(String fileId, byte[] data, String token) {

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
			return Result.ok();

		} catch (IOException e) {
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}

	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {

		try {

			synchronized(fileData) {
				File file = fileData.get(fileId);
				file.delete();
				fileData.remove(fileId);
			}
			return Result.ok();

		} catch (Exception e) {
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}

	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {

		File file;
		synchronized (fileData) {

			file = fileData.get(fileId);

			if (file == null)
				return Result.error(ErrorCode.NOT_FOUND);

			try {
				// File file = fileData.get(FILEPATH + fileId);
				byte[] fileContent;

				try (FileInputStream stream = new FileInputStream(file)) {
					fileContent = stream.readAllBytes();
					stream.close();
				}

				return Result.ok(fileContent);

			} catch (IOException e) {
				return Result.error(ErrorCode.INTERNAL_ERROR);
			}
		}

	}

	@Override
	public Result<Integer> deleteAllFiles(String userId, String token) {

		if (userId == null)
			return Result.error(ErrorCode.BAD_REQUEST);

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
		return Result.ok(count);

	}

}
