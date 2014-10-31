package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ICarRentalCompany extends Remote{
	
	public Collection<CarType> getAllCarTypes() throws RemoteException;
	
	public String getName() throws RemoteException;
	
	public CarType getCarType(String carTypeName) throws RemoteException;
	
	public boolean isAvailable(String carTypeName, Date start, Date end) throws RemoteException;
	
	/**
	 * Get available car types in a certain period.
	 * @param start
	 * @param end
	 * @return
	 * @throws RemoteException
	 */
	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
	
	/**
	 * Create a tentative reservation. Stateless
	 * @param constraints
	 * @param client
	 * @return
	 * @throws ReservationException
	 * @throws RemoteException
	 */
	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException, RemoteException;
	
	/**
	 * Calculate a rental price. Logic for calculation goes here.
	 * @param rentalPricePerDay
	 * @param start
	 * @param end
	 * @return
	 * @throws RemoteException
	 */
	public double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) throws RemoteException;
	
	/**
	 * Only to be called by the RentalServer. Not exposed to client
	 * Confirms all quotes or none at all.
	 * @param quote
	 * @return
	 * @throws ReservationException		If something went wrong: No quotes are confirmed 
	 * @throws RemoteException
	 */
	public List<Reservation> confirmQuotes(List<Quote> quote) throws ReservationException, RemoteException;
	
	/**
	 * Only to be called by the RentalServer. Not exposed to client
	 * Cancels a batch of reservations
	 * @param res
	 * @throws RemoteException
	 */
	public void cancelReservations(List<Reservation> res) throws RemoteException;
	
	/**
	 * Get reservations for a client
	 * @param clientName
	 * @return
	 * @throws RemoteException
	 */
	public List<Reservation> getReservationsBy(String clientName) throws RemoteException;
	
	/**
	 * Gets the number of reservations for a particular cartype.
	 * @param carType
	 * @return
	 * @throws RemoteException
	 */
	public int getNumberOfReservationsForCarType(String carType) throws RemoteException;
	
	/**
	 * Lists the best customers: 
	 * 		0 if no cutomers
	 * 		n if there are n customers with the same score 
	 * 		1 otherwise
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getBestCustomers() throws RemoteException;
	
	/**
	 * Get the car type with the most reservations
	 * @return
	 * @throws RemoteException
	 */
	public CarType getMostPopularCartype() throws RemoteException;
	
	
}
