package rental;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
	private Map<String, ICarRentalCompany> companies = new HashMap<>();
	
	public RentalServer(){
		
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
		System.out.println("Added new car rental company: " + crc.getName());
	}

	
}
