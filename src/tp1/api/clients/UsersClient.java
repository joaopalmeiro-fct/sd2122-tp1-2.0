package tp1.api.clients;

import tp1.api.service.util.Result;

public interface UsersClient {
	
	/**
	 * Authenticates the user with the given id.
	 * 
	 * @param userId the userId of the user
	 * @param password password of the user
	 * @return 204 if the user exists and the password matches
	 *         403 if the password is incorrect
	 *         404 if no user exists with the provided userId
	 */
	public Result<Void> authenticateUser(String userId, String password);
	
	/**
	 * Check the existence of the user with the given id.
	 * 
	 * @param userId the userId of the user
	 * @return 204 if the user exists
	 *         404 if no user exists with the provided userId
	 */
	public Result<Void> checkUserExistence (String userId);
}
