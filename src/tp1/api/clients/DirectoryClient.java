package tp1.api.clients;

import tp1.api.service.util.Result;

public interface DirectoryClient {
	
	/**
	 * Deletes all files of the given user, removes it from any sharings and deletes all info associated with it.
	 * @param userId - id of the user.
	 * @param password - the password of the user.
	 * @return OK if success (regardless of having any files). 
	 *		   NOT_FOUND if the userId does not exist.
	 *         FORBIDDEN if the password is incorrect.
	 * 		   BAD_REQUEST otherwise.
	 */
	Result<Void> deleteAllFiles(String userId, String password);

}
