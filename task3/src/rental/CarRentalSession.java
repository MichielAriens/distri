package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CarRentalSession extends Session{
	
	private List<Quote> quotes = new ArrayList<>();
	private String client;

	public CarRentalSession(IRentalServer server) {
		super(server);
	}
	
	/**
	 * Gets the available car types in a given period for a particular company
	 * @param start
	 * @param end
	 * @param company
	 * @return
	 * @throws RemoteException
	 */
	public List<CarType> getAvailableCarTypes(Date start, Date end,
			String company) throws RemoteException {
		return new ArrayList<CarType>(getServer().getCarRentalCompany(company).getAvailableCarTypes(start, end));
	}
	
	/**
	 * Get the available CarTypes in a given period system wide.
	 * @param start
	 * @param end
	 * @return
	 * @throws RemoteException
	 */
	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException{
		Set<CarType> retval = new HashSet<>();
		for(ICarRentalCompany crc : getServer().getAllCarRentalCompanies()){
			retval.addAll(crc.getAvailableCarTypes(start, end));
		}
		return retval;
	}

	/**
	 * Creates a quote based on the reservation constraints provided at a particular company. Adds this quote to the session.
	 * @param cons
	 * @param company
	 * @return
	 * @throws RemoteException
	 * @throws ReservationException
	 */
	public Quote createQuote(ReservationConstraints cons, String company)
			throws RemoteException, ReservationException {
		String client = getClientName();
		Quote quote = getServer().getCarRentalCompany(company).createQuote(cons, client);
		quotes.add(quote);
		return quote;
	}
	
	/**
	 * Returns a list of quotes submitted to this session.
	 * @return
	 */
	public List<Quote> getCurrentQuotes() {
		return quotes;
	}

	/**
	 * Confirms all quotes in this session. Will remove these from the session.
	 * @return
	 * @throws RemoteException
	 * @throws ReservationException
	 */
	public List<Reservation> confirmQuotes() throws RemoteException, ReservationException{
		return getServer().confirmQuotesForAll(quotes);
	}
	
	/**
	 * Gets the cheapest car type in a given period
	 * @param start
	 * @param end
	 * @return
	 * @throws RemoteException
	 */
	public String getCheapestCarType(Date start, Date end) throws RemoteException {
		CarType ct = null;
		for(CarType carType: getAvailableCarTypes(start,end)){
			if(ct == null){
				ct = carType;
			}
			if(carType.getRentalPricePerDay() < ct.getRentalPricePerDay()){
				ct = carType;
			}
		}
		return ct.getName();
	}

	/**
	 * Sets the name for the client
	 * @param client
	 */
	public void setClientName(String client) {
		this.client = client;
	}

	/**
	 * getter for client name
	 * @return
	 */
	public String getClientName() {
		return client;
	}


}
