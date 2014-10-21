package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISession extends Remote{
	
	public IRentalServer getServer() throws RemoteException;

}
