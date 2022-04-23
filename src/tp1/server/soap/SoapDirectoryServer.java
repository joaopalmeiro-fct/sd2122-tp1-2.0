package tp1.server.soap;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.xml.ws.Endpoint;
import tp1.discovery.Discovery;
import tp1.server.util.ServiceName;
import tp1.service.soap.DirectoryWebService;

public class SoapDirectoryServer {

	public static final int PORT = 8080;
	public static String SERVER_BASE_URI = "http://%s:%s/soap";

	private static Logger Log = Logger.getLogger(SoapDirectoryServer.class.getName());

	public static void main(String[] args) throws Exception {
		
		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

		Log.setLevel(Level.INFO);

		String ip = InetAddress.getLocalHost().getHostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);
		Discovery discovery = new Discovery
				(new InetSocketAddress(ip, PORT), ServiceName.DIRECTORY.toString(), serverURI);
		discovery.start();

		Endpoint.publish(serverURI.replace(ip, "0.0.0.0"), new DirectoryWebService(discovery));

		Log.info(String.format("%s Soap Server ready @ %s\n", ServiceName.DIRECTORY.toString(), serverURI));
	}
}
