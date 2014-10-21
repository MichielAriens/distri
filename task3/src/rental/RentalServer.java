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
	public synchronized List<Reservation> confirmQuotesForAll(Map<String, List<Quote>> quotes)
			throws RemoteException {
		Map<String, List<Reservation>> confirmed = new HashMap<>();
		for(String key : quotes.keySet()){
			try {
				List<Reservation> currentBatch = companies.get(key).confirmQuotes(quotes.get(key));
				confirmed.put(key, currentBatch);
			} catch (ReservationException e) {
				//The CRC takes care of the current batch so we need not worry about it. 
				//We need to roll back all successful calls of confirmQuotes(...) however:
				for(String key1 : confirmed.keySet()){
					companies.get(key1).cancelReservations(confirmed.get(key1));
				}
			}
		}
		List<Reservation> retval = new ArrayList<>();
		for(List<Reservation> part : confirmed.values()){
			retval.addAll(part);
		}
		return retval;
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
