package tp1.service.util;


import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.FileInfo;
import tp1.api.User;
import tp1.api.clients.FilesClient;
import tp1.api.clients.UsersClient;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;
import tp1.api.service.util.Users;
import tp1.clients.factory.FilesClientFactory;
import tp1.clients.factory.UsersClientFactory;
import tp1.discovery.Discovery;
import tp1.discovery.util.exceptions.NoUrisFoundException;
import tp1.server.util.ServiceName;
import tp1.util.EntrySort;

public class JavaDirectory implements Directory {
	private static final String FORWARD_SLASH = "/";

	// map that saves all the files of a user
	private final Map<String, Map<String, FileInfo>> userFiles;

	// map with the users shared with the user
	private final Map<String, Map<String, FileInfo>> userSharedWithFiles;

	private static final String FILE_ID = "%s-%s";
	// after 50 operations using discovery, it will search for new URI's
	private static final int FILE_REDISCOVERY = 50;
	private final Map<URI, Integer> fileDistribution;

	private int rediscovery_counter;

	private UsersClientFactory usersClientFactory;
	private FilesClientFactory filesClientFactory;
	/*private final RestFilesClient filesClient;
	private final RestUsersClient usersClient;*/

	private final Discovery discovery;

	public JavaDirectory (Discovery discovery) {
		this.discovery = discovery;
		userFiles = new HashMap<>();
		userSharedWithFiles = new HashMap<>();
		fileDistribution = new HashMap<>();
		rediscovery_counter = 0;
		
		/*filesClient = new RestFilesClient();
		usersClient = new RestUsersClient();*/
		
		
		usersClientFactory = new UsersClientFactory();
		filesClientFactory = new FilesClientFactory();
		
		
	}

	@Override
	public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {

		if (filename == null || data == null || userId == null)
			return Result.error( ErrorCode.BAD_REQUEST );
		
		Result<Void> condition = authenticateUser(userId, password);
		if (!condition.isOK())
			return Result.error(condition.error());

		boolean existed = false;
		String fileId = String.format(FILE_ID, userId, filename);

		synchronized (userFiles) {

			try {

				FileInfo file;

				Map<String, FileInfo> userfiles = userFiles.get(userId);
				if (userfiles == null) {
					userfiles = new HashMap<String, FileInfo>();
					userFiles.put(userId, userfiles);
				}

				file = userfiles.get(fileId);

				if (file == null) {
					file = new FileInfo(userId, filename, null, null);
					userfiles.put(fileId, file);
				} else
					existed = true;

				Result<URI> result = sendToFiles(data, fileId);
				if (!result.isOK()) {
					if (!existed)
						userFiles.get(userId).remove(fileId);
					return Result.error(result.error());
				}
				
				URI uri = result.value();
				file.setFileURL(
						uri.toURL().toString().concat(FORWARD_SLASH + ServiceName.FILES.getServiceName() + FORWARD_SLASH + fileId));

				return Result.ok(file);

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
				if (!existed)
					userFiles.get(userId).remove(fileId);
				return Result.error(ErrorCode.INTERNAL_ERROR);
			}
		}
	}

	@Override
	public Result<Void> deleteFile(String filename, String userId, String password) {

		if (filename == null || userId == null)
			// Log.info ()... ?
			return Result.error(ErrorCode.BAD_REQUEST);

		Result<Void> condition = authenticateUser(userId, password);
		if (!condition.isOK())
			return condition;

		String fileId = String.format(FILE_ID, userId, filename);

		synchronized (userFiles) {

			Map<String, FileInfo> userfiles = userFiles.get(userId);

			if (userfiles == null) {
				return Result.error(ErrorCode.NOT_FOUND);
			}

			FileInfo file = userfiles.get(fileId);

			if (file == null) {
				return Result.error(ErrorCode.NOT_FOUND);
			}

			try {
				synchronized (userSharedWithFiles) {
					URI uri = new URI(file.getFileURL().split(FORWARD_SLASH + ServiceName.FILES.getServiceName())[0]);
					deleteFromFiles(fileId, uri);
					for (String u : file.getSharedWith()) {
						userSharedWithFiles.get(u).remove(fileId);
					}
					userfiles.remove(fileId);
					return Result.ok();
				}

			} catch (Exception e) {
				return Result.error(ErrorCode.INTERNAL_ERROR);
			}
		}

	}

	@Override
	public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {

		String fileId = String.format(FILE_ID, userId, filename);

		Result<Void> condition = checkShareConditions(userId, filename, userIdShare, password, fileId);
		if (!condition.isOK())
			return condition;

		FileInfo file;

		synchronized (userFiles) {

			file = userFiles.get(userId).get(fileId);

			try {

				synchronized (userSharedWithFiles) {
					file.addElemSharedWith(userIdShare);

					Map<String, FileInfo> sharedFiles = userSharedWithFiles.get(userIdShare);
					if (sharedFiles == null) {
						sharedFiles = new HashMap<>();
						userSharedWithFiles.put(userIdShare, sharedFiles);
					}
					sharedFiles.put(fileId, file);
					return Result.ok();
				}
				// newSharedWith.add(userIdShare);
				// file.setSharedWith(newSharedWith);
				// files.put(fileId, file);
			} catch (Exception e) {
				return Result.error(ErrorCode.INTERNAL_ERROR);
			}
		}

	}

	@Override
	public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {

		String fileId = String.format(FILE_ID, userId, filename);

		Result<Void> condition = checkShareConditions(userId, filename, userIdShare, password, fileId);
		if (!condition.isOK())
			return condition;
		FileInfo file;

		synchronized (userFiles) {
			file = userFiles.get(userId).get(fileId);
		}

		try {
			file.removeElemSharedWith(userIdShare);

			synchronized (userSharedWithFiles) {

				Map<String, FileInfo> sharedFiles = userSharedWithFiles.get(userIdShare);

				if (sharedFiles == null) {
					sharedFiles = new HashMap<>();
					userSharedWithFiles.put(userIdShare, sharedFiles);
				} else
					sharedFiles.remove(fileId);
				return Result.ok();
			}
		} catch (Exception e) {
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}

	}

	@Override
	public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {
		String fileId = String.format(FILE_ID, userId, filename);

		if (filename == null || userId == null || accUserId == null)
			return Result.error(ErrorCode.BAD_REQUEST);

		Result<Void> condition = authenticateUser(accUserId, password);
		if (!condition.isOK()) {
			return Result.error(condition.error());
		}

		checkUserExistance(userId);

		synchronized (userFiles) {

			Map<String, FileInfo> userfiles = userFiles.get(userId);

			if (userfiles == null) {
				return Result.error(ErrorCode.NOT_FOUND);
			}

			FileInfo file = userfiles.get(fileId);

			if (file == null) {
				return Result.error(ErrorCode.NOT_FOUND);
			}

			String owner = file.getOwner();
			Set<String> sharedWith = file.getSharedWith();

			if (owner.equals(accUserId) || sharedWith.contains(accUserId))
				//return Result.ok();
				return Result.ok(null,URI.create(file.getFileURL()));
			else
				return Result.error(ErrorCode.FORBIDDEN);

		}

	}

	@Override
	public Result<List<FileInfo>> lsFile(String userId, String password) {

		if (userId == null)
			return Result.error(ErrorCode.BAD_REQUEST);

		Result<Void> condition = authenticateUser(userId, password);
		if (!condition.isOK())
			return Result.error(condition.error());

		List<FileInfo> ls;

		synchronized (userFiles) {

			Map<String, FileInfo> userfiles = userFiles.get(userId);

			synchronized (userSharedWithFiles) {

				Map<String, FileInfo> sharedfiles = userSharedWithFiles.get(userId);
				ls = new ArrayList<FileInfo>();

				if (userfiles != null) {
					for (FileInfo fileinfo : userfiles.values()) {
						ls.add(fileinfo);
					}
				}
				if (sharedfiles != null) {
					for (FileInfo fileinfo : sharedfiles.values()) {
						ls.add(fileinfo);
					}
				}
			}
		}
		// Collections.sort(ls);
		return Result.ok(ls);
	}

	// ---------------------------------------------------------------------------------------------

	@Override
	public Result<Void> deleteAllFiles(String userId, String password) {

		if (userId == null)
			return Result.error(ErrorCode.BAD_REQUEST);

		Result<Void> condition = authenticateUser(userId, password);
		if (!condition.isOK())
			return condition;

		synchronized (userFiles) {

			Map<String, FileInfo> userfiles = userFiles.get(userId);

			synchronized (userSharedWithFiles) {

				if (userfiles != null) {

					Result<Void> r = deleteAllFromFiles(userId);
					if (!r.isOK())
						return Result.error(r.error());
					for (FileInfo file : userfiles.values()) {
						for (String shared_user : file.getSharedWith()) {
							userSharedWithFiles.get(shared_user)
							.remove(String.format(FILE_ID, userId, file.getFilename()));
						}
					}
					userFiles.remove(userId);
				}

				Map<String, FileInfo> usersharedfiles = userSharedWithFiles.get(userId);

				if (usersharedfiles != null) {
					for (FileInfo file : usersharedfiles.values()) {
						file.removeElemSharedWith(userId);
					}
					userSharedWithFiles.remove(userId);
				}
			}
		}
		return Result.ok();



	}

	// --------------------------------------- Util methods ---------------------------------------

	private Result<Void> checkShareConditions(String userId, String filename, String userIdShare, String password,
			String fileId) {

		if (filename == null || userId == null || userIdShare == null)
			// Log.info ()... ?
			return Result.error(ErrorCode.BAD_REQUEST);

		Result<Void> condition = authenticateUser(userId, password);
		if (!condition.isOK())
			return condition;
		
		condition = checkUserExistance(userIdShare);
		if (!condition.isOK())
			return condition;

	

		synchronized (userFiles) {

			Map<String, FileInfo> userfiles = userFiles.get(userId);

			if (userfiles == null) {
				return Result.error(ErrorCode.NOT_FOUND);
			}

			FileInfo file = userfiles.get(fileId);

			if (file == null) {
				return Result.error(ErrorCode.NOT_FOUND);
			}

			String owner = file.getOwner();

			if (!owner.equals(userId))
				return Result.error(ErrorCode.FORBIDDEN);

		}
		
		return Result.ok();

	}

	// -------------------------------Communication between servers--------------------------------

	// ------------------------------------------- Users ------------------------------------------

	private Result<Void> authenticateUser(String userId, String password) {

		URI uri;

		try {
			uri = discovery.findURI(ServiceName.USERS.getServiceName());
		} catch (Exception e) {
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}
		
		Result<Void> result;
		synchronized(usersClientFactory) {
			UsersClient client;
			try {
				client = usersClientFactory.getClient(uri);
			}
			catch (MalformedURLException e) {
				return Result.error(ErrorCode.INTERNAL_ERROR);
			}
			
			result = client.authenticateUser(userId, password);
		}
		if (result.isOK()) {
			return Result.ok();
		} else
			return Result.error(result.error());

	}

	private Result<Void> checkUserExistance(String userId) {

		URI uri;

		try {
			uri = discovery.findURI(ServiceName.USERS.getServiceName());
		} catch (Exception e) {
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}
		Result<Void> result;
		synchronized(usersClientFactory) {
			UsersClient client;
			try {
				client = usersClientFactory.getClient(uri);
			}
			catch (MalformedURLException e) {
				return Result.error(ErrorCode.INTERNAL_ERROR);
			}
			result = client.checkUserExistence(userId);
		}

		if (result.isOK()) {
			return Result.ok();
		} else
			return Result.error(result.error());

	}

	// ----------------------------------------- Files --------------------------------------------

	private Result<URI> sendToFiles(byte[] data, String fileId) {
		
		Result<Void> r = updateFileDiscovered();
		if (!r.isOK())
			return Result.error(r.error());

		URI uri = null;
		synchronized (fileDistribution) {
			List<EntrySort<URI, Integer>> sortedList = EntrySort.toEntrySort(fileDistribution.entrySet());

			for (int i = 0; i < sortedList.size(); i++) {
				uri = sortedList.get(i).getKey();
				Result<Void> result;
				synchronized(filesClientFactory) {
					FilesClient client;
					try {
						client = filesClientFactory.getClient(uri);
					}
					catch (MalformedURLException e) {
						return Result.error(ErrorCode.INTERNAL_ERROR);
					}
					//filesClient.redifineURI(uri);
					result = client.writeFile(fileId, data, "");
				}
				if (result == null)
					continue;
				if (result.isOK()) {
					int count = fileDistribution.get(uri);
					count++;
					fileDistribution.replace(uri, count);
					rediscovery_counter++;
					return Result.ok(uri);
				}
				else
					return Result.error(result.error());
			}
		}
		return Result.error(ErrorCode.INTERNAL_ERROR);
	}

	private Result<Void> updateFileDiscovered() {
		synchronized (fileDistribution) {
			if ((rediscovery_counter % FILE_REDISCOVERY) == 0) {
				try {
					URI[] uris = discovery.knownUrisOf(ServiceName.FILES.getServiceName());
					for (URI uri : uris)
						if (!fileDistribution.containsKey(uri))
							fileDistribution.put(uri, 0);
				} catch (NoUrisFoundException e) {
					return Result.error(ErrorCode.INTERNAL_ERROR);
				}

			}
		}
		return Result.ok();
	}

	private Result<Void> deleteFromFiles(String fileId, URI uri) {

		try {
			// URI uri;

			// uri = discovery.findURI(FilesServer.SERVICE);
			synchronized(filesClientFactory) {
				FilesClient client;
				try {
					client = filesClientFactory.getClient(uri);
				}
				catch (MalformedURLException e) {
					return Result.error(ErrorCode.INTERNAL_ERROR);
				}
				//filesClient.redifineURI(uri);
				Result<Void> r = client.deleteFile(fileId, "");
				if (!r.isOK())
					return Result.error(r.error());
			}
			synchronized (fileDistribution) {
				int count = fileDistribution.get(uri);
				count--;
				fileDistribution.replace(uri, count);

				rediscovery_counter--;
			}
			return Result.ok();


		} catch (Exception e) {
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}
	}

	private Result<Void> deleteAllFromFiles(String userId) {
		synchronized (fileDistribution) {
			for (URI uri : fileDistribution.keySet()) {
				try {

					Result<Integer> r;
					synchronized(filesClientFactory) {
						FilesClient client;
						try {
							client = filesClientFactory.getClient(uri);
						}
						catch (MalformedURLException e) {
							return Result.error(ErrorCode.INTERNAL_ERROR);
						}
						
						
						//filesClient.redifineURI(uri);
						r = client.deleteAllFiles(userId,"");
						if (!r.isOK())
							return Result.error(r.error());
					}
					if (r.isOK()) {
						Integer deleted = r.value();
						int prev_count = fileDistribution.get(uri);
						fileDistribution.replace(uri, prev_count - deleted);

					}
				} catch (Exception e) {
					return Result.error(ErrorCode.INTERNAL_ERROR);
				}
			}
		}
		return Result.ok();
	}


}
