package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ICar extends Remote{
	public List<Reservation> getReservations() throws RemoteException;
	
	public void addReservation(Reservation res) throws RemoteException;
	
	public int getId() throws RemoteException;
}
