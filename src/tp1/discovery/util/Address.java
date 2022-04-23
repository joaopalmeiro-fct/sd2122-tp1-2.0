package tp1.discovery.util;

import java.net.URI;

public class Address {
	
	private final URI address;
	private long time;
	
	public Address(URI address, long time) {
		this.address = address;
		this.setTime(time);
	}

	public URI getAddress() {
		return address;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	

}
