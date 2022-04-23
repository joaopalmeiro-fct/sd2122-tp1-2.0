package tp1.service.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import tp1.api.FileInfo;
import tp1.api.User;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;
import tp1.api.service.util.Users;
import tp1.discovery.Discovery;

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
	
	private final FilesClient filesClient;
	private final UsersClient usersClient;
	
	private final Discovery discovery;
	
	public JavaDirectory (Discovery discovery) {
		this.discovery = discovery;
		userFiles = new HashMap<>();
		userSharedWithFiles = new HashMap<>();

		fileDistribution = new HashMap<>();
		rediscovery_counter = 0;
		filesClient = new FilesClient();
		usersClient = new UsersClient();
	}

	/*
	@Override
	public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> deleteFile(String filename, String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<FileInfo>> lsFile(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}*/
	
		

		// private final FilesClient filesClient new FilesClient();

		

		@Override
		public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {

			if (filename == null || data == null || userId == null)
				return Result.error( ErrorCode.BAD_REQUEST );

			authenticateUser(userId, password);
			

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

					URI uri = sendToFiles(data, fileId);
					file.setFileURL(
							uri.toURL().toString().concat(FORWARD_SLASH + FilesServer.SERVICE + FORWARD_SLASH + fileId));

					return Result.ok(file);

				} catch (Exception e) {
					e.printStackTrace();
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

			authenticateUser(userId, password);

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
						URI uri = new URI(file.getFileURL().split(FORWARD_SLASH + FilesServer.SERVICE)[0]);
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

			checkShareConditions(userId, filename, userIdShare, password, fileId);

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

			checkShareConditions(userId, filename, userIdShare, password, fileId);

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

			authenticateUser(accUserId, password);

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
					// return getFromFiles(fileId);
					return new Result.ok(URI.create(file.getFileURL()));
				else
					return Result.error(ErrorCode.FORBIDDEN);

			}

		}

		@Override
		public Result<List<FileInfo>> lsFile(String userId, String password) {

			if (userId == null)
				return Result.error(ErrorCode.BAD_REQUEST);

			authenticateUser(userId, password);

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

			authenticateUser(userId, password);

			// boolean aux = false;

			synchronized (userFiles) {

				Map<String, FileInfo> userfiles = userFiles.get(userId);

				synchronized (userSharedWithFiles) {

					if (userfiles != null) {
						
						deleteAllFromFiles(userId);
						for (FileInfo file : userfiles.values()) {
							for (String shared_user : file.getSharedWith()) {
								userSharedWithFiles.get(shared_user)
										.remove(String.format(FILE_ID, userId, file.getFilename()));
							}
							// file_url.put(file.getFilename(), file.getFileURL());
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

		// -------------------------------------------Util
		// methods--------------------------------------------

		private void checkShareConditions(String userId, String filename, String userIdShare, String password,
				String fileId) {

			if (filename == null || userId == null || userIdShare == null)
				// Log.info ()... ?
				return new Result.error(ErrorCode.BAD_REQUEST);

			if (!authenticateUser(userId, password))
				return new Result.error(ErrorCode.FORBIDDEN);

			if (!checkUserExistance(userIdShare))
				return new Result.error(ErrorCode.NOT_FOUND);

			synchronized (userFiles) {

				Map<String, FileInfo> userfiles = userFiles.get(userId);

				if (userfiles == null) {
					return new Result.error("No files from the user", ErrorCode.NOT_FOUND);
				}

				FileInfo file = userfiles.get(fileId);

				if (file == null) {
					return new Result.error("File does not exist", ErrorCode.NOT_FOUND);
				}

				String owner = file.getOwner();

				if (!owner.equals(userId))
					return new Result.error(ErrorCode.FORBIDDEN);

			}

		}

		// -----------------------------------Communication between
		// servers---------------------------------

		// ------------------------------------------- Users
		// ----------------------------------------------

		private boolean authenticateUser(String userId, String password) {

			URI uri;

			try {
				uri = discovery.findURI(UsersServer.SERVICE);
			} catch (Exception e) {
				return new Result.error(ErrorCode.INTERNAL_SERVER_ERROR);
			}
			int result;
			synchronized(usersClient) {
				usersClient.redifineURI(uri);
				result = usersClient.authenticateUser(userId, password).getErrorCode();
			}
			if (result == ErrorCode.NO_CONTENT.getErrorCodeCode()) {
				return true;
			} else
				return new Result.error(result);

		}

		private boolean checkUserExistance(String userId) {

			URI uri;

			try {
				uri = discovery.findURI(UsersServer.SERVICE);
			} catch (Exception e) {
				return new Result.error(ErrorCode.INTERNAL_SERVER_ERROR);
			}
			int result;
			synchronized(usersClient) {
				usersClient.redifineURI(uri);
				result = usersClient.checkUserExistence(userId).getErrorCode();
			}

			if (result == ErrorCode.NO_CONTENT.getErrorCodeCode()) {
				return true;
			} else
				return new Result.error(result);

		}

		// ----------------------------------------- Files
		// ---------------------------------------------

		private URI sendToFiles(byte[] data, String fileId) {
			// Fazer aqui de forma a utilizar vários servidores de files, por exemplo com
			// rotacao ou assim.

			updateFileDiscovered();

			URI uri = null;
			synchronized (fileDistribution) {
				List<EntrySort<URI, Integer>> sortedList = EntrySort.toEntrySort(fileDistribution.entrySet());

				for (int i = 0; i < sortedList.size(); i++) {
					uri = sortedList.get(i).getKey();
					Response result;
					synchronized(filesClient) {
						filesClient.redifineURI(uri);
						result = filesClient.writeFile(data, fileId);
					}
					if (result == null)
						continue;
					int ErrorCode = result.getErrorCode();
					if (ErrorCode == ErrorCode.NO_CONTENT.getErrorCodeCode()) {
						int count = fileDistribution.get(uri);
						count++;
						fileDistribution.replace(uri, count);
						rediscovery_counter++;
						return uri;
					} else
						return new Result.error(result);
				}
			}
			return new Result.error(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		private void updateFileDiscovered() {
			synchronized (fileDistribution) {
				if ((rediscovery_counter % FILE_REDISCOVERY) == 0) {
					try {
						URI[] uris = discovery.knownUrisOf(FilesServer.SERVICE);
						for (URI uri : uris)
							if (!fileDistribution.containsKey(uri))
								fileDistribution.put(uri, 0);
					} catch (NoUrisFoundException e) {
						return new Result.error(e);
					}

				}
			}
		}

		private void deleteFromFiles(String fileId, URI uri) {

			try {
				// URI uri;

				// uri = discovery.findURI(FilesServer.SERVICE);
				synchronized(filesClient) {
					filesClient.redifineURI(uri);
					filesClient.deleteFile(fileId);
				}
				synchronized (fileDistribution) {
					int count = fileDistribution.get(uri);
					count--;
					fileDistribution.replace(uri, count);

					rediscovery_counter--;
				}

				
			} catch (Exception e) {
				return new Result.error(e);
			}
		}

		private void deleteAllFromFiles(String userId) {
			synchronized (fileDistribution) {
				for (URI uri : fileDistribution.keySet()) {
					try {
						
						Response r;
						synchronized(filesClient) {
							filesClient.redifineURI(uri);
							r = filesClient.deleteAllFiles(userId);
						}
						if (r.getErrorCode() == ErrorCode.OK.getErrorCodeCode() && r.hasEntity()) {
							Integer deleted = r.readEntity(Integer.class);
							int prev_count = fileDistribution.get(uri);
							fileDistribution.replace(uri, prev_count - deleted);

						}
					} catch (Exception e) {
						return new Result.error(e);
					}
				}
			}
		}
	

}
