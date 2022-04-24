package tp1.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.Enumeration;
import java.util.Map;

import java.util.HashMap;

import java.util.logging.Logger;

import tp1.discovery.util.ServiceAddress;
import tp1.discovery.util.exceptions.NoUrisFoundException;

/**
 * <p>A class to perform service discovery, based on periodic service contact endpoint 
 * announcements over multicast communication.</p>
 * 
 * <p>Servers announce their *name* and contact *uri* at regular intervals. The server actively
 * collects received announcements.</p>
 * 
 * <p>Service announcements have the following format:</p>
 * 
 * <p>&lt;service-name-string&gt;&lt;delimiter-char&gt;&lt;service-uri-string&gt;</p>
 */
public class Discovery {
	private static Boolean LOGKNOWN = false;
	private static Logger Log = Logger.getLogger(Discovery.class.getName());
	private Map<String, ServiceAddress> sv_addresses;
	private Thread announcer, listener;
	private Boolean stopListener;
	private Boolean stopAnnouncer;

	

	static {
		// addresses some multicast issues on some TCP/IP stacks
		System.setProperty("java.net.preferIPv4Stack", "true");
		// summarizes the logging format
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}
	
	
	// The pre-aggreed multicast endpoint assigned to perform discovery. 
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("227.227.227.227", 2277);
	static final int DISCOVERY_PERIOD = 1000;
	static final int DISCOVERY_TIMEOUT = 5000;

	// Used separate the two fields that make up a service announcement.
	private static final String DELIMITER = "\t";

	private InetSocketAddress addr;
	private String serviceName;
	private String serviceURI;

	/**
	 * @param  serviceName the name of the service to announce
	 * @param  serviceURI an uri string - representing the contact endpoint of the service being announced
	 */
	public Discovery(InetSocketAddress addr, String serviceName, String serviceURI) {
		sv_addresses = new HashMap<String,ServiceAddress>();
		this.addr = addr;
		this.serviceName = serviceName;
		this.serviceURI  = serviceURI;
		stopListener = false; stopAnnouncer = false;
	}
	
	/**
	 * Continuously announces a service given its name and uri
	 * 
	 * @param serviceName the composite service name: <domain:service>
	 * @param serviceURI - the uri of the service
	 */
	public void announce(String serviceName, String serviceURI) {
		stopAnnouncer = false;
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n", DISCOVERY_ADDR, serviceName, serviceURI));

		var pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();

		DatagramPacket pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);
		// start thread to send periodic announcements
		announcer = new Thread(() -> {
			try (var ds = new DatagramSocket()) {
				while(!stopAnnouncer) {
					try {
						Thread.sleep(DISCOVERY_PERIOD);
						ds.send(pkt);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		announcer.start();
	}

	/**
	 * Listens for the given composite service name, blocks until a minimum number of replies is collected.
	 * @param serviceName - the composite name of the service
	 * @param minRepliesNeeded - the minimum number of replies required.
	 * @return the discovery results as an array
	 */
	
	public void listener() {
		stopListener = false;
		Log.info(String.format("Starting discovery on multicast group: %s, port: %d\n", DISCOVERY_ADDR.getAddress(), DISCOVERY_ADDR.getPort()));

		final int MAX_DATAGRAM_SIZE = 65536;
		var pkt = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);

		listener = new Thread(() -> {
			
		    try (var ms = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
			    joinGroupInAllInterfaces(ms);
				while (!stopListener) {
					try {
						pkt.setLength(MAX_DATAGRAM_SIZE);
						ms.receive(pkt);
						
						var msg = new String(pkt.getData(), 0, pkt.getLength());
						System.out.printf( "FROM %s (%s) : %s\n", pkt.getAddress().getCanonicalHostName(), 
								pkt.getAddress().getHostAddress(), msg);
						var tokens = msg.split(DELIMITER);
					
						if (tokens.length == 2) {
						
							//For testing purposes
							if (LOGKNOWN) try { 
								System.out.println();
								System.out.println("Discovered uris of "+tokens[0]);
								for (URI u : knownUrisOf(tokens[0])) {
									System.out.println(u.toURL());
								}
								System.out.println();
							}
							catch (Exception e) {System.out.println(e.getMessage());}
								
								
							//token - [servicename,uri]
							//to complete by recording the received information from the other node.
							try {
								var newuri = new URI(tokens[1]);
								var uris = sv_addresses.get(tokens[0]);
								
								if (uris == null) {
									ServiceAddress svad = new ServiceAddress(tokens[0]);
									svad.putAddress(newuri);
									sv_addresses.put(tokens[0], svad);
								}
								else {
									uris.putAddress(newuri);
									sv_addresses.put(tokens[0], uris);
								}
									
									
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
								
					} catch (IOException e) {
						e.printStackTrace();
						try {
							Thread.sleep(DISCOVERY_PERIOD);
						} catch (InterruptedException e1) {
						// do nothing
						}
						Log.finest("Still listening...");
					}
				}
  		    } 
		    catch (IOException e) {
			    e.printStackTrace();
		    }
		});
		listener.start();
	}

	/**
	 * Returns the known servers for a service.
	 * @param  serviceName the name of the service being discovered
	 * @return an array of URI with the service instances discovered. 
	 * @throws Exception 
	 */
	public URI[] knownUrisOf(String serviceName) throws NoUrisFoundException {
		//You have to implement this!!
		ServiceAddress svad = sv_addresses.get(serviceName);
		URI[] uris;
		if (svad!=null) {
			uris = svad.getAddresses();
			return uris;
		}
		else 
			throw new NoUrisFoundException(serviceName);
	}
	
	public URI findURI(String serviceName) throws NoUrisFoundException {
		URI[] uris = knownUrisOf(serviceName);
		URI serverUrl = null;
		try {
			for (URI u : uris) { //Tenho de tirar este for que da forma como esta agora a ser usado pa sacar o primeiro elemento e um bocado useless
				serverUrl = u;
				break; // Get first one.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverUrl;
	}
		
	private void joinGroupInAllInterfaces(MulticastSocket ms) throws SocketException {
		Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
		while (ifs.hasMoreElements()) {
			NetworkInterface xface = ifs.nextElement();
			try {
				ms.joinGroup(DISCOVERY_ADDR, xface);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}	

	/**
	 * Starts sending service announcements at regular intervals... 
	 */
	public void start() {
		
		announce(serviceName, serviceURI);
		listener();
	}
	
	public void stop() {
		stopAnnouncer = true;
		stopListener = true;
	}

	// Main just for testing purposes
	public static void main( String[] args) throws Exception {
		Discovery discovery = new Discovery( DISCOVERY_ADDR, "test", "http://" + InetAddress.getLocalHost().getHostAddress());
		discovery.start();
	}
}
