package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CarRentalSession extends Session{
	
	private List<Quote> quotes = new ArrayList<>();
	private String client;

	public CarRentalSession(IRentalServer server) {
		super(server);
	}


	public List<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException {
		return new ArrayList<CarType>(getServer().getCarRentalCompany(company).getAvailableCarTypes(start, end));
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
	

	public CarType getCheapestCarType(Date start, Date end) throws RemoteException {
		CarType ct = null;
		for(CarType carType: getAvailableCarTypes(start,end)){
			if(!(ct == null) && (carType.getRentalPricePerDay() < ct.getRentalPricePerDay())){
				ct = carType;
			}
		}
		return ct;
	}


	public void setClientName(String client) {
		this.client = client;
	}


	public String getClientName() {
		return client;
	}


}
