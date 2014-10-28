package rental;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Central point of contact for the client.
 * @author michiel, julie
 *
 */
public class RentalServer implements IRentalServer{
	private SessionServer sessionServer;
	private Map<String, ICarRentalCompany> companies = new HashMap<>();
	
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
	public synchronized List<Reservation> confirmQuotesForAll(List<Quote> quotes)
			throws RemoteException {
		//sort and map the quotes by company 
		Map<String, List<Quote>> mapped = mapOnCompany(quotes);		
		Map<String, List<Reservation>> confirmed = new HashMap<>();
		for(String key : mapped.keySet()){
			try {
				List<Reservation> currentBatch = companies.get(key).confirmQuotes(mapped.get(key));
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
	
	/**
	 * Maps a list if quote onto a map: company:String -> List<Quote>
	 * Note: We could generalize this method using lambdas.
	 * @param quotes
	 * @return
	 */
	private Map<String, List<Quote>> mapOnCompany(List<Quote> quotes) {
		Map<String, List<Quote>> mapped = new HashMap<>();
		String key;
		for(Quote quote : quotes){
			key = quote.getRentalCompany();
			if(mapped.containsKey(key)){
				mapped.get(key).add(quote);
			}else{
				mapped.put(key, new LinkedList<Quote>());
				mapped.get(key).add(quote);
			}
		}
		return mapped;
	}
	
	/**
	 * 
	 */
	@Override
	public void addCarRentalCompany(ICarRentalCompany crc)
			throws RemoteException {
		companies.put(crc.getName(),crc);
	}

	@Override
	public CarType getCheapestCarType() throws RemoteException{
		CarType ct = null;
		for(CarType carType: getAllCarTypes()){
			if(!(ct == null) && (carType.getRentalPricePerDay() < ct.getRentalPricePerDay())){
				ct = carType;
			}
		}
		return ct;
	}

	@Override
	public Set<CarType> getAllCarTypes() throws RemoteException {
		Set<CarType> carTypes = new HashSet<CarType>();
		for(ICarRentalCompany crc: getAllCarRentalCompanies()){
			carTypes.addAll(crc.getAllCarTypes());
		}
		return carTypes;
	}
	
	
}

class SessionServer implements Serializable{
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
