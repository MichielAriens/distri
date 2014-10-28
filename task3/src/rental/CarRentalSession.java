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

	public List<CarType> getAvailableCarTypes(Date start, Date end,
			String company) throws RemoteException {
		return new ArrayList<CarType>(getServer().getCarRentalCompany(company).getAvailableCarTypes(start, end));
	}

	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException{
		Set<CarType> retval = new HashSet<>();
		for(ICarRentalCompany crc : getServer().getAllCarRentalCompanies()){
			retval.addAll(crc.getAvailableCarTypes(start, end));
		}
		return retval;
	}


	public Quote createQuote(ReservationConstraints cons, String company)
			throws RemoteException, ReservationException {
		String client = getClientName();
		return getServer().getCarRentalCompany(company).createQuote(cons, client);
	}
	

	public List<Quote> getCurrentQuotes() {
		return quotes;
	}


	public List<Reservation> confirmQuotes() throws RemoteException {
		return getServer().confirmQuotesForAll(quotes);
	}
	

	public String getCheapestCarType(Date start, Date end) throws RemoteException {
		CarType ct = null;
		for(CarType carType: getAvailableCarTypes(start,end)){
			if(!(ct == null) && (carType.getRentalPricePerDay() < ct.getRentalPricePerDay())){
				ct = carType;
			}
		}
		return ct.getName();
	}


	public void setClientName(String client) {
		this.client = client;
	}


	public String getClientName() {
		return client;
	}


}
