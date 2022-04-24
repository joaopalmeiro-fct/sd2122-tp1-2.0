package tp1.service.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tp1.api.User;
import tp1.api.clients.DirectoryClient;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;
import tp1.api.service.util.Users;
import tp1.clients.factory.DirectoryClientFactory;
import tp1.server.util.ServiceName;
import tp1.discovery.*;

public class JavaUsers implements Users {
	
	Discovery discovery;
	private final Map<String,User> users;
	private DirectoryClientFactory directoryClientFactory;
	//private final DirectoryClient directoryClient;
	
	public JavaUsers (Discovery discovery) {
		this.discovery = discovery;
		users = new HashMap<>();
		
		directoryClientFactory = new DirectoryClientFactory();
	}
		
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
			if( retUser == null )
				return Result.error( ErrorCode.NOT_FOUND );

			//Check if the password is correct
			if( !retUser.getPassword().equals(password))
				return Result.error( ErrorCode.FORBIDDEN );

			retUser.update(user);

		}

		return Result.ok(retUser);
	}


	@Override
	public Result<User> deleteUser(String userId, String password) {

		if(userId == null) 
			return Result.error( ErrorCode.BAD_REQUEST );

		User storedUser;

		synchronized (users) {

			storedUser = users.get(userId);

			// Check if user exists 
			if( storedUser == null )
				return Result.error( ErrorCode.NOT_FOUND );

			//Check if the password is correct
			if( password == null || !storedUser.getPassword().equals(password))
				return Result.error( ErrorCode.FORBIDDEN );

			deleteFiles(userId, password);
			users.remove(userId);

		}

		return Result.ok(storedUser);
	}


	@Override
	public Result<List<User>> searchUsers(String pattern) {

		if(pattern == null)
			return Result.error( ErrorCode.BAD_REQUEST );

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

		return Result.ok(retUsers);
	}

//------------------------------- Util methods (for other services) ---------------------------------

	@Override
	public Result<Void> authenticateUser (String userId, String password) {

		// Check if user is valid
		//if(userId == null || password == null) {
		if(userId == null) 
			return Result.error( ErrorCode.BAD_REQUEST );

		User user;

		synchronized (this) {
			user = users.get(userId);
		}

		// Check if user exists 
		if( user == null ) 
			return Result.error( ErrorCode.NOT_FOUND );

		//Check if the password is correct
		if( password == null || !user.getPassword().equals(password))
			return Result.error( ErrorCode.FORBIDDEN );
		
		return Result.error( ErrorCode.NO_CONTENT );
		
	}

	@Override
	public Result<Void> checkUserExistence (String userId) {

		if(userId == null)
			return Result.error( ErrorCode.BAD_REQUEST );

		User user;

		synchronized (users) {
			user = users.get(userId);
		}

		if( user == null )
			return Result.error( ErrorCode.NOT_FOUND );
		
		return Result.error( ErrorCode.NO_CONTENT );

	}

	//-------------------------------- Communication with directories -------------------------------\\

	private Result<Void> deleteFiles(String userId, String password) {
		URI uri;	

		try {
			uri = discovery.findURI(ServiceName.DIRECTORY.getServiceName());
		} catch (Exception e) {
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}
		
		Result<Void> r;
		
		synchronized(directoryClientFactory) {
			DirectoryClient client;
			try {
				client = directoryClientFactory.getClient(uri);
			}
			catch (MalformedURLException e) {
				return Result.error(ErrorCode.INTERNAL_ERROR);
			}
			//filesClient.redifineURI(uri);
			r = client.deleteAllFiles(userId,password);
			if (!r.isOK())
				return Result.error(r.error());
			else
				return Result.ok();
		
		}
	}

}

