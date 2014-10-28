package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CarRentalSession extends Session implements ICarRentalSession{
	
	private List<Quote> quotes = new ArrayList<>();
	private String client;

	public CarRentalSession(IRentalServer server) {
		super(server);
	}

	@Override
	public List<CarType> getAvailablaCarTypes(Date start, Date end,
			String company) throws RemoteException {
		return new ArrayList<CarType>(getServer().getCarRentalCompany(company).getAvailableCarTypes(start, end));
	}

	@Override
	public Quote createQuote(ReservationConstraints cons, String company)
			throws RemoteException, ReservationException {
		String client = getClientName();
		return getServer().getCarRentalCompany(company).createQuote(cons, client);
	}

	@Override
	public List<Reservation> confirmQuotes() throws RemoteException {
		return getServer().confirmQuotesForAll(quotes);
	}

	@Override
	public void setClientName(String client) {
		this.client = client;
	}

	@Override
	public String getClientName() {
		return client;
	}

}
