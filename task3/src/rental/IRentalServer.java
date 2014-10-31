package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRentalServer extends Remote{
	
	/**
	 * Get a car rental company based on its name.
	 * @param name
	 * @return
	 * @throws RemoteException
	 */
	public ICarRentalCompany getCarRentalCompany(String name) throws RemoteException;
	
	/**
	 * Get all car rental companies
	 * @return
	 * @throws RemoteException
	 */
	public List<ICarRentalCompany> getAllCarRentalCompanies() throws RemoteException;
	
	/**
	 * Confirms all quotes provided in all companies involved or none at all. 
	 * @param quotes		List of quotes
	 * @return				List of reservations
	 * @throws RemoteException		In case of communication failure
	 * @throws ReservationException	In case of reservation failure: No quotes are committed. 
	 */
	public List<Reservation> confirmQuotesForAll(List<Quote> quotes) throws RemoteException, ReservationException;
	
	/**
	 * Register a car rental company
	 * @param crc
	 * @throws RemoteException
	 */
	public void addCarRentalCompany(ICarRentalCompany crc) throws RemoteException; 

	/**
	 * Remove a car rental company
	 * @param company
	 * @throws RemoteException
	 */
	public void removeCarRentalCompany(String company) throws RemoteException;
}
