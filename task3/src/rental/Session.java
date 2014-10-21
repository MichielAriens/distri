package rental;

import java.rmi.Remote;

public class Session implements ISession{
	
	private IRentalServer server;
	
	public Session(IRentalServer server){
		this.server = server;
	}
	
	private IRentalServer getServer(){
		return server;
	}

}
