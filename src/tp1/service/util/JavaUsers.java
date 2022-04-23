package tp1.service.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.ErrorCode;
import tp1.api.User;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;
import tp1.api.service.util.Users;
import tp1.server.DirectoriesServer;

public class JavaUsers implements Users {

	/*
	@Override
	public Result<String> createUser(User user) {
		
		return null;
	}

	@Override
	public Result<User> getUser(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<User> deleteUser(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	
	private final Map<String,User> users = new HashMap<>();
	
	@Override
	public Result<String> createUser(User user) {

		// Check if user data is valid
		if(user.getUserId() == null || user.getPassword() == null || user.getFullName() == null || 
				user.getEmail() == null)
			return Result.error(ErrorCode.BAD_REQUEST);

		synchronized (users) {

			// Check if userId already exists
			if( users.containsKey(user.getUserId()))
				return Result.error(ErrorCode.CONFLICT);

			//Add the user to the map of users
			users.put(user.getUserId(), user);

		}

		return Result.ok(user.getUserId());
	}


	@Override
	public Result<User> getUser(String userId, String password) {
		
		if(userId == null) {
			return Result.error( ErrorCode.BAD_REQUEST );
		}

		User user;

		synchronized (users) {
			user = users.get(userId);
		}

		// Check if user exists 
		if( user == null ) 
			return Result.error( ErrorCode.NOT_FOUND );

		//Check if the password is correct
		if( password == null || !user.getPassword().equals( password))
			return Result.error( ErrorCode.FORBIDDEN );

		return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String userId, String password, User user) {

		// Check if user is valid
		if(userId == null || password == null || user == null)
			return Result.error( ErrorCode.BAD_REQUEST );

		User retUser;

		synchronized (users) {

			retUser = users.get(userId);

			// Check if user exists 
			if( retUser == null ) {
				Log.info("User does not exist.");
				return Result.error( ErrorCode.NOT_FOUND );
			}

			//Check if the password is correct
			if( !retUser.getPassword().equals(password)) {
				Log.info("Password is incorrect.");
				return Result.error( ErrorCode.FORBIDDEN );
			}

			retUser.update(user);

			//retUser = users.put(userId, storedUser);
		}

		return retUser;
	}


	@Override
	public User deleteUser(String userId, String password) {
		Log.info("deleteUser : user = " + userId + "; pwd = " + password);

		if(userId == null) {
			Log.info("UserId null.");
			return Result.error( ErrorCode.BAD_REQUEST );
		}

		User storedUser;

		synchronized (users) {

			storedUser = users.get(userId);

			// Check if user exists 
			if( storedUser == null ) {
				System.out.println("User does not exist.");
				return Result.error( ErrorCode.NOT_FOUND );
			}

			//Check if the password is correct
			if( password == null || !storedUser.getPassword().equals(password)) {
				Log.info("Password is incorrect.");
				return Result.error( ErrorCode.FORBIDDEN );
			}

			deleteFiles(userId, password);
			users.remove(userId);

		}

		return storedUser;
	}


	@Override
	public List<User> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);

		if(pattern == null) {
			Log.info("Pattern null. Use empty string for all results");
			return Result.error( ErrorCode.BAD_REQUEST );
		}

		List<User> retUsers = new ArrayList<User>();
		Collection<User> allUsers;

		synchronized (users) {
			
			allUsers = users.values();

			for (User user: allUsers) {
				String id = user.getFullName();
				if (id.toLowerCase().contains(pattern.toLowerCase()))
					retUsers.add(user);
			}

		}

		return retUsers;
	}

//------------------------------- Util methods (for other services) ---------------------------------

	@Override
	public void authenticateUser (String userId, String password) {
		Log.info("authenticateUser : user = " + userId + "; pwd = " + password);

		// Check if user is valid
		//if(userId == null || password == null) {
		if(userId == null) {
			Log.info("UserId null.");
			return Result.error( ErrorCode.BAD_REQUEST );
		}

		User user;

		synchronized (this) {
			user = users.get(userId);
		}

		// Check if user exists 
		if( user == null ) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND );
		}

		//Check if the password is correct
		if( password == null || !user.getPassword().equals( password)) {
			Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN );
		}
	}

	@Override
	public void checkUserExistence (String userId) {

		if(userId == null) {
			Log.info("UserId null.");
			return Result.error( ErrorCode.BAD_REQUEST );
		}

		User user;

		synchronized (users) {
			user = users.get(userId);
		}

		if( user == null ) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND );
		}

	}

	//-------------------------------- Communication with directories -------------------------------\\

	private void deleteFiles(String userId, String password) {
		URI uri;	

		try {
			uri = discovery.findURI(DirectoriesServer.SERVICE);
		} catch (Exception e) {
			return Result.error(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		
		int result;
		synchronized(directoriesClient) {
			directoriesClient.redifineURI(uri);
			result = directoriesClient.deleteFiles(userId, password).getErrorCode();
		}
		if (result != ErrorCode.NO_CONTENT.getErrorCodeCode()) 
			return Result.error(result);

	}

}


}
