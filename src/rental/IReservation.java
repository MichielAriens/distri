package rental;

import java.rmi.RemoteException;

public interface IReservation extends IQuote{
	
	public int getCarId() throws RemoteException;

}
