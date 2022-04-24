package tp1.api.clients;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tp1.api.service.util.Result;

public interface FilesClient {
	
	/**
	 * Write a file. If the file exists, overwrites the contents.
	 * 
	 * @param fileId - unique id of the file. 
	 * @param token - token for accessing the file server (in the first 
	 * project this will not be used).
     *
	 * @return OK if success.
	 *         FORBIDDEN if the token is invalid.
	 * 		   BAD_REQUEST otherwise.
	 */
	
	Result<Void> writeFile(String fileId, byte[] data, String token);

	/**
	 * Delete an existing file.
	 * 
	 * @param fileId - unique id of the file. 
	 * @param token - token for accessing the file server (in the first 
	 * project this will not be used).
	 * 
	 * @return OK if success; 
	 *		   NOT_FOUND if the fileId does not exist.
	 *         FORBIDDEN if the token is invalid.
	 * 		   BAD_REQUEST otherwise.
	 */
	
	Result<Void> deleteFile(String fileId, String token);

	/**
	 * Get the contents of the file. 
	 * 
	 * @param fileId - unique id of the file. 
	 * @param token - token for accessing the file server (in the first 
	 * project this will not be used).
	 * 
	 * @return OK if success + contents (through redirect to the File server); 
	 *		   NOT_FOUND if the fileId does not exist.
	 *         FORBIDDEN if the token is invalid.
	 * 		   BAD_REQUEST otherwise.
	 */
	Result<byte[]> getFile(String fileId, String token);
	
	/**
	 * Delete all existing files from given user.
	 * 
	 * @param userId - unique id of the user whose files will be deleted. 
	 * 
	 * @return OK if success (even if user has no files here); 
	 * 		   BAD_REQUEST otherwise.
	 */
	Result<Integer> deleteAllFilesF(String userId, String token);


}
