package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface IQuote extends Remote{
	
	public Date getStartDate() throws RemoteException;
	
	public Date getEndDate() throws RemoteException;
	
	public String getCarRenter() throws RemoteException;
	
	public String getRentalCompany() throws RemoteException;
	
	//TODO
}
