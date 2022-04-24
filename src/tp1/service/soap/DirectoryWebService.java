package tp1.service.soap;

import java.util.List;

import jakarta.inject.Singleton;
import jakarta.jws.WebService;
import tp1.api.FileInfo;
import tp1.api.service.soap.DirectoryException;
import tp1.api.service.soap.SoapDirectory;
import tp1.api.service.util.Directory;
import tp1.discovery.Discovery;
import tp1.service.util.JavaDirectory;

@Singleton
@WebService(serviceName=SoapDirectory.NAME, targetNamespace=SoapDirectory.NAMESPACE, endpointInterface=SoapDirectory.INTERFACE)
public class DirectoryWebService implements SoapDirectory {

	Discovery discovery;
	
	final Directory directoryImpl;
	
	public DirectoryWebService (Discovery discovery) {
		this.discovery = discovery;
		directoryImpl = new JavaDirectory(discovery);
	}
	
	@Override
	public FileInfo writeFile(String filename, byte[] data, String userId, String password) 
			throws DirectoryException {
		
		var result = directoryImpl.writeFile(filename, data, userId, password);
		
        if( result.isOK() )
            return result.value();
        else
            throw new DirectoryException(result.error().toString()) ;
	}

	@Override
	public void deleteFile(String filename, String userId, String password) throws DirectoryException {

		var result = directoryImpl.deleteFile(filename, userId, password);
		
        if( result.isOK() )
            return;
        else
            throw new DirectoryException(result.error().toString()) ;
		
	}

	@Override
	public void shareFile(String filename, String userId, String userIdShare, String password)
			throws DirectoryException {

		var result = directoryImpl.shareFile(filename, userId, userIdShare, password);
		
        if( result.isOK() )
            return;
        else
            throw new DirectoryException(result.error().toString()) ;
		
	}

	@Override
	public void unshareFile(String filename, String userId, String userIdShare, String password)
			throws DirectoryException {
		
		var result = directoryImpl.unshareFile(filename, userId, userIdShare, password);
		
        if( result.isOK() )
            return;
        else
            throw new DirectoryException(result.error().toString()) ;
		
	}

	@Override
	public byte[] getFile(String filename, String userId, String accUserId, String password) throws DirectoryException {

		var result = directoryImpl.getFile(filename, userId, accUserId, password);
		
        if( result.isOK() )
            return result.value();
        else
            throw new DirectoryException(result.error().toString()) ;
        
	}

	@Override
	public List<FileInfo> lsFile(String userId, String password) throws DirectoryException {
		
		var result = directoryImpl.lsFile(userId, password);
		
        if( result.isOK() )
            return result.value();
        else
            throw new DirectoryException(result.error().toString());
        
	}

	@Override
	public void deleteAllFiles(String userId, String password) throws DirectoryException {
		
		var result = directoryImpl.lsFile(userId, password);
		
        if( result.isOK() )
            return;
        else
            throw new DirectoryException(result.error().toString());
		
	}

}
