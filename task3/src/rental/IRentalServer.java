package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRentalServer extends Remote{
	
	public ICarRentalCompany getCarRentalCompany(String name) throws RemoteException;
	
	public List<ICarRentalCompany> getAllCarRentalCompanies() throws RemoteException;
	
	public List<Reservation> confirmQuotesForAll(List<Quote> quotes) throws RemoteException, ReservationException;
	
	public void addCarRentalCompany(ICarRentalCompany crc) throws RemoteException; 
	
}
