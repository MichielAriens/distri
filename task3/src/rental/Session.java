package rental;


public class Session implements ISession{
	
	private IRentalServer server;
	
	public Session(IRentalServer server){
		this.server = server;
	}
	
	public IRentalServer getServer(){
		return server;
	}

}
