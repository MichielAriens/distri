package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ICarRentalCompany extends Remote{
	
	public Collection<CarType> getAllCarTypes() throws RemoteException;
	
	public CarType getCarType(String carTypeName) throws RemoteException;
	
	public boolean isAvailable(String carTypeName, Date start, Date end) throws RemoteException;
	
	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
	
	public ICar getCar(int uid) throws RemoteException;
	
	public List<ICar> getAvailableCars(String carType, Date start, Date end) throws RemoteException;
	
	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException, RemoteException;
	
	public double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) throws RemoteException;
	
	public Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;
	
	public void cancelReservation(Reservation res) throws ReservationException, RemoteException;
	
	public List<ICar> getCars() throws RemoteException;
}
