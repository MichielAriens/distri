package rental;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface ICarRentalSession extends ISession{
	
	public List<CarType> getAvailablaCarTypes(Date start, Date end, String company) throws RemoteException;
	
	public Quote createQuote(ReservationConstraints cons, String company) throws RemoteException, ReservationException;
	
	public List<Reservation> confirmQuotes() throws RemoteException;

	public void setClientName(String client);
	
	public String getClientName();
}
