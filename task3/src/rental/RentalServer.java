package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central point of contact for the client.
 * @author michiel
 *
 */
public class RentalServer implements IRentalServer{
	private SessionServer sessionServer;
	private Map<String, CarRentalCompany> companies = new HashMap<>();
	
	public RentalServer(){
		this.sessionServer = new SessionServer(this);
	}
	
	public ICarRentalSession getNewCarRentalSession(){
		return this.sessionServer.getNewCarRentalSession();
	}
	
	public IManagerSession getNewManagerSession(){
		return this.sessionServer.getNewManagerSession();
	}

	@Override
	public ICarRentalCompany getCarRentalCompany(String name)
			throws RemoteException {
		return this.companies.get(name);
	}

	@Override
	public List<ICarRentalCompany> getAllCarRentalCompanies()
			throws RemoteException {
		return new ArrayList(this.companies.values());
	}

	@Override
	public List<Reservation> confirmQuotesForAll(Map<String, List<Quote>> quotes)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}

class SessionServer {
	private List<Session> activeSessions;
	private RentalServer parent;
	
	public SessionServer(RentalServer parent){
		this.activeSessions = new ArrayList<>();
		this.parent = parent;
	}
	
	protected CarRentalSession getNewCarRentalSession(){
		CarRentalSession retval = new CarRentalSession(this.parent);
		activeSessions.add(retval);
		return retval;
	}
	
	protected ManagerSession getNewManagerSession(){
		ManagerSession retval = new ManagerSession(this.parent);
		activeSessions.add(retval);
		return retval;
	}
}
