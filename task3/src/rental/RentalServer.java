package rental;

import java.util.ArrayList;
import java.util.List;

/**
 * Central point of contact for the client.
 * @author michiel
 *
 */
public class RentalServer implements IRentalServer{
	private SessionServer sessionServer;
	
	public RentalServer(){
		this.sessionServer = new SessionServer();
	}
	
	public CarRentalSession getNewCarRentalSession(){
		return this.sessionServer.getNewCarRentalSession();
	}
}

class SessionServer {
	private List<Session> activeSessions;
	
	public SessionServer(){
		activeSessions = new ArrayList<>();
	}
	
	protected CarRentalSession getNewCarRentalSession(){
		CarRentalSession retval = new CarRentalSession();
		activeSessions.add(retval);
		return retval;
	}
	
	protected ManagerSession getNewManagerSession(){
		ManagerSession retval = new ManagerSession();
		activeSessions.add(retval);
		return retval;
	}
}
