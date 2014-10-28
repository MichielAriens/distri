package rental;

public class Session{
	
	private IRentalServer server;
	
	public Session(IRentalServer server){
		this.server = server;
	}
	
	protected IRentalServer getServer(){
		return this.server;
	}

}
