package tp1.api.service.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@WebService(serviceName=SoapFiles.NAME, targetNamespace=SoapFiles.NAMESPACE, endpointInterface=SoapFiles.INTERFACE)
public interface SoapFiles {

	static final String NAME = "files";
	static final String NAMESPACE = "http://sd2122";
	static final String INTERFACE = "tp1.api.service.soap.SoapFiles";

	@WebMethod
	byte[] getFile(String fileId, String token) throws FilesException;

	@WebMethod
	void deleteFile(String fileId, String token) throws FilesException;
	
	@WebMethod
	void writeFile(String fileId, byte[] data, String token) throws FilesException;	
	
	/**
	 * Delete all existing files from given user.
	 * @param userId - unique id of the user whose files will be deleted. 
	 * @throws FilesException otherwise
	 */
	@WebMethod
	Integer deleteAllFilesF(String userId, String token) throws FilesException;
}
