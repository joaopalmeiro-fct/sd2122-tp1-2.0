package tp1.service.soap;

import jakarta.inject.Singleton;
import jakarta.jws.WebService;
import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapFiles;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Files;
import tp1.discovery.Discovery;
import tp1.service.util.JavaDirectory;
import tp1.service.util.JavaFiles;

@Singleton
@WebService(serviceName=SoapFiles.NAME, targetNamespace=SoapFiles.NAMESPACE, endpointInterface=SoapFiles.INTERFACE)
public class FilesWebService implements SoapFiles {
	
	Discovery discovery;
	
	final Files filesImpl;
	
	public FilesWebService (Discovery discovery) {
		this.discovery = discovery;
		filesImpl = new JavaFiles(discovery);
	}

	@Override
	public byte[] getFile(String fileId, String token) throws FilesException {

		var result = filesImpl.getFile(fileId, token);
		
        if( result.isOK() )
            return result.value();
        else
            throw new FilesException(result.error().toString());
	}

	@Override
	public void deleteFile(String fileId, String token) throws FilesException {

		var result = filesImpl.deleteFile(fileId, token);
		
        if( result.isOK() )
            return;
        else
            throw new FilesException(result.error().toString()) ;
		
	}

	@Override
	public void writeFile(String fileId, byte[] data, String token) throws FilesException {

		var result = filesImpl.writeFile(fileId, data, token);
		
        if( result.isOK() )
            return;
        else
            throw new FilesException(result.error().toString()) ;
		
	}

	@Override
	public Integer deleteAllFilesF(String userId, String token) throws FilesException {
		
		var result = filesImpl.deleteAllFiles(userId, token);
		
        if( result.isOK() )
            return result.value();
        else
            throw new FilesException(result.error().toString()) ;
		
	}

}
