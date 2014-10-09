package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface ICar extends Remote{
	public void addReservation(Reservation res) throws RemoteException;
	
	public int getId() throws RemoteException;
	
	public CarType getType() throws RemoteException;
	
	public boolean isAvailable(Date start, Date end) throws RemoteException;
	
	public void removeReservation(Reservation reservation) throws RemoteException;

	public List<Reservation> getReservations() throws RemoteException;
	
	
}
