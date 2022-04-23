package tp1.service.soap;

import java.util.List;

import tp1.api.User;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;
import tp1.api.service.util.Users;
import tp1.discovery.Discovery;
import tp1.service.util.JavaUsers;

public class UsersWebService implements SoapUsers {

	Discovery discovery;
	
	final Users userImpl;
	
	public UsersWebService (Discovery discovery) {
		this.discovery = discovery;
		userImpl = new JavaUsers(discovery);
	}
	
	@Override
	public String createUser(User user) throws UsersException {
		
		var result = userImpl.createUser( user );
		
        if( result.isOK() )
            return result.value();
        else
            throw new UsersException(result.error().toString()) ;
	}

	@Override
	public User getUser(String userId, String password) throws UsersException {
		
		var result = userImpl.getUser(userId, password);
		
        if( result.isOK() )
            return result.value();
        else
            throw new UsersException(result.error().toString()) ;
	}

	@Override
	public User updateUser(String userId, String password, User user) throws UsersException {
		
		var result = userImpl.updateUser(userId, password, user);
		
        if( result.isOK() )
            return result.value();
        else
            throw new UsersException(result.error().toString()) ;
	}

	@Override
	public User deleteUser(String userId, String password) throws UsersException {
		
		var result = userImpl.deleteUser(userId, password);
		
        if( result.isOK() )
            return result.value();
        else
            throw new UsersException(result.error().toString()) ;
	}

	@Override
	public List<User> searchUsers(String pattern) throws UsersException {
		
		var result = userImpl.searchUsers(pattern);
		
        if( result.isOK() )
            return result.value();
        else
            throw new UsersException(result.error().toString()) ;
	}

}
