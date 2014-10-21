package rental;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CarRentalSession extends Session implements ICarRentalSession{
	
	private List<Quote> quotes = new ArrayList<>();

	public CarRentalSession(IRentalServer server) {
		super(server);
	}

	@Override
	public List<CarType> getAvailablaCarTypes(Date start, Date end,
			String company) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quote createQuote(ReservationConstraints cons, String company)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Reservation> confirmQuotes() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
