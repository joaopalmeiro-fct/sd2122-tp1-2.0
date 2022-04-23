package tp1.service.rest;

import java.util.List;

import jakarta.ws.rs.WebApplicationException;
import tp1.api.User;
import tp1.api.service.rest.RestUsers;
import tp1.api.service.util.Users;
import tp1.discovery.Discovery;
import tp1.service.util.JavaUsers;

public class UsersResource implements RestUsers {
	
	Discovery discovery;
	
	final Users userImpl;
	
	public UsersResource (Discovery discovery) {
		userImpl = new JavaUsers(discovery);
		this.discovery = discovery;
	}

	@Override
	public String createUser(User user) {
		
		var result = userImpl.createUser( user );
		
        if( result.isOK() )
            return result.value();
        else
            throw new WebApplicationException(result.error().toString()) ;
	}

	@Override
	public User getUser(String userId, String password) {

		var result = userImpl.getUser(userId, password);
		
        if( result.isOK() )
            return result.value();
        else
            throw new WebApplicationException(result.error().toString()) ;
	}

	@Override
	public User updateUser(String userId, String password, User user) {

		var result = userImpl.updateUser(userId, password, user);
		
        if( result.isOK() )
            return result.value();
        else
            throw new WebApplicationException(result.error().toString()) ;
	}

	@Override
	public User deleteUser(String userId, String password) {

		var result = userImpl.deleteUser(userId, password);
		
        if( result.isOK() )
            return result.value();
        else
            throw new WebApplicationException(result.error().toString()) ;
	}

	@Override
	public List<User> searchUsers(String pattern) {

		var result = userImpl.searchUsers(pattern);
		
        if( result.isOK() )
            return result.value();
        else
            throw new WebApplicationException(result.error().toString()) ;
	}

}
