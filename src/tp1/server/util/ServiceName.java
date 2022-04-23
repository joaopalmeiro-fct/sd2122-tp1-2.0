package tp1.server.util;

public enum ServiceName {
	
	USERS("users"),
	DIRECTORY("directory"),
	FILES("files");
	
	private String serviceName;
	
	ServiceName (String serviceName){
		this.serviceName = serviceName;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
}
