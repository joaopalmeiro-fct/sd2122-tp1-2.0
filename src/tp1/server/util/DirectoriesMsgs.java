package tp1.server.util;

public enum DirectoriesMsgs {
	authenticatedUser ("User authenticated");
	
	private String msg;      

    private DirectoriesMsgs(String s) {
        this.msg = s;
    }

    public String toString() {
       return this.msg;
    }
}
