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
	
	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
	
	public Car getCar(int uid) throws RemoteException;
	
	public List<Car> getAvailableCars(String carType, Date start, Date end) throws RemoteException;
	
	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException, RemoteException;
	
	public double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) throws RemoteException;
	
	public List<Reservation> confirmQuotes(List<Quote> quote) throws ReservationException, RemoteException;
	
	//public void cancelReservation(Reservation res) throws ReservationException, RemoteException;
	
	public void cancelReservations(List<Reservation> res) throws RemoteException;
	
	public List<Reservation> getReservationsBy(String clientName) throws RemoteException;
	
	public int getNumberOfReservationsForCarType(String carType) throws RemoteException;
	
	public String getBestCustomer() throws RemoteException;
	
	public CarType getMostPopularCartype() throws RemoteException;
	
	
}
