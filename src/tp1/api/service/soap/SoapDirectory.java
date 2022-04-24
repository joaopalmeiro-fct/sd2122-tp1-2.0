package tp1.api.service.soap;

import java.util.List;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import tp1.api.FileInfo;

@WebService(serviceName=SoapDirectory.NAME, targetNamespace=SoapDirectory.NAMESPACE, endpointInterface=SoapDirectory.INTERFACE)
public interface SoapDirectory {
	
	static final String NAME = "dir";
	static final String NAMESPACE = "http://sd2122";
	static final String INTERFACE = "tp1.api.service.soap.SoapDirectory";

	@WebMethod
	FileInfo writeFile(String filename, byte []data, String userId, String password) throws DirectoryException;

	@WebMethod
	void deleteFile(String filename, String userId, String password) throws DirectoryException;

	@WebMethod
	void shareFile(String filename, String userId, String userIdShare, String password) throws DirectoryException;

	@WebMethod
	void unshareFile(String filename, String userId, String userIdShare, String password) throws DirectoryException;

	@WebMethod
	byte[] getFile(String filename,  String userId, String accUserId, String password) throws DirectoryException;

	@WebMethod
	List<FileInfo> lsFile(String userId, String password) throws DirectoryException;
	
	/**
	 * Deletes all files of the given user, removes it from any sharings and deletes all info associated with it.
	 * @param userId - id of the user.
	 * @param password - the password of the user.
	 * @throws DirectoryException otherwise
	 */
	@WebMethod
	void deleteAllFiles(String userId, String password) throws DirectoryException;
}
