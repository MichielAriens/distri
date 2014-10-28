package rental;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface ICarRentalSession extends ISession{
	
	public List<CarType> getAvailablaCarTypes(Date start, Date end, String company) throws RemoteException;
	
	public Quote createQuote(ReservationConstraints cons, String company) throws RemoteException, ReservationException;
	
	public List<Quote> getCurrentQuotes();
	
	public List<Reservation> confirmQuotes() throws RemoteException;
	
	public CarType getCheapestCarType();

	public void setClientName(String client) throws RemoteException;
	
	public String getClientName() throws RemoteException;
}
