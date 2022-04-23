package tp1.server.rest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import tp1.discovery.Discovery;
import tp1.server.util.CustomLoggingFilter;
import tp1.server.util.GenericExceptionMapper;
import tp1.server.util.ServiceName;
import tp1.service.rest.DirectoryResource;
import tp1.util.Debug;

public class RestDirectoryServer {

	private static Logger Log = Logger.getLogger(RestDirectoryServer.class.getName());
	// private static Discovery discovery = null;

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static final int PORT = 8080;
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";

	public static void main(String[] args) {
		try {

			Debug.setLogLevel(Level.INFO, Debug.SD2122);
			String ip = InetAddress.getLocalHost().getHostAddress();
			String serverURI = String.format(SERVER_URI_FMT, ip, PORT);

			Discovery discovery = new Discovery
					(new InetSocketAddress(ip, PORT), ServiceName.USERS.toString(), serverURI);
			discovery.start();

			ResourceConfig config = new ResourceConfig();
			config.register(new DirectoryResource(discovery));
			config.register(CustomLoggingFilter.class);
			config.register(GenericExceptionMapper.class);

			JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);

			Log.info(String.format("%s Server ready @ %s\n", ServiceName.USERS.toString(), serverURI));

		} catch (Exception e) {
			Log.severe(e.getMessage());
		}
	}

}
