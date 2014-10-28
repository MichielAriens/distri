package rental;


public class Session implements ISession{
	
	private IRentalServer server;
	private String client;
	
	public Session(IRentalServer server, String client){
		this.server = server;
		this.client = client;
	}
	
	public IRentalServer getServer(){
		return server;
	}
	
	public String getClient(){
		return client;
	}

}
