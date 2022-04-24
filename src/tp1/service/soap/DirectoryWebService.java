package tp1.service.soap;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import jakarta.inject.Singleton;
import jakarta.jws.WebService;
import tp1.api.FileInfo;
import tp1.api.clients.FilesClient;
import tp1.api.service.soap.DirectoryException;
import tp1.api.service.soap.SoapDirectory;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;

import tp1.clients.factory.FilesClientFactory;
import tp1.discovery.Discovery;
import tp1.service.util.JavaDirectory;

@Singleton
@WebService(serviceName=SoapDirectory.NAME, targetNamespace=SoapDirectory.NAMESPACE, endpointInterface=SoapDirectory.INTERFACE)
public class DirectoryWebService implements SoapDirectory {

	Discovery discovery;
	private FilesClientFactory filesClientFactory;
	
	final Directory directoryImpl;
	
	public DirectoryWebService (Discovery discovery) {
		this.discovery = discovery;	
		filesClientFactory = new FilesClientFactory();
		directoryImpl = new JavaDirectory(discovery, filesClientFactory);
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
		
        if( result.isOK() ) {
        	String URIandFileID[] = result.uri().toString().split("/"+tp1.server.util.ServiceName.FILES.getServiceName()+"/");
        	URI uri;
        	try { uri = new URI(URIandFileID[0]); } catch (URISyntaxException e) {throw new DirectoryException(e.getMessage());}
        	String fileId = URIandFileID[1];
        	
        	Result<byte[]> r;
        	
			synchronized(filesClientFactory) {
				FilesClient client;
				try {
					client = filesClientFactory.getClient(uri);
				}
				catch (MalformedURLException e) {
					throw new DirectoryException(e.getMessage()) ;
				}
				
				r = client.getFile(fileId,"");
				if (!r.isOK())
					throw new DirectoryException(r.error().toString());
				 return r.value();
			}
        }
          
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
		
		var result = directoryImpl.deleteAllFiles(userId, password);
		
        if( result.isOK() )
            return;
        else
            throw new DirectoryException(result.error().toString());
		
	}

}
