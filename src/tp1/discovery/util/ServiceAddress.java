package tp1.discovery.util;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceAddress {
	
	private final String serviceName;
	private Map<URI, Address> addresses;
	
	public ServiceAddress (String name) {
		serviceName = name;
		addresses = new HashMap<URI, Address>();
	}
	
	public URI[] getAddresses() {
		int count = 0;
		Set<URI> keyset = addresses.keySet();
		URI[] uris = new URI[keyset.size()]; 
		for (URI u : addresses.keySet()) {
			uris[count] = u;
			count++;
		}
		return uris;
	}
	
	public Collection<Address> getAddressesInfo() {
		return addresses.values();
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public void putAddress(URI address) {
		Address add = addresses.get(address);
		if (add!=null) {
			add.setTime(System.currentTimeMillis());
			return;
		}
		addresses.put(address, new Address(address, System.currentTimeMillis()));
		
	}
	

}
