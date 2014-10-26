package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IRentalServer extends Remote{
	
	public ICarRentalSession getNewCarRentalSession() throws RemoteException;
	
	public IManagerSession getNewManagerSession() throws RemoteException;
	
	public ICarRentalCompany getCarRentalCompany(String name) throws RemoteException;
	
	public List<ICarRentalCompany> getAllCarRentalCompanies() throws RemoteException;
	
	public List<Reservation> confirmQuotesForAll(Map<String, List<Quote>> quotes) throws RemoteException;
	
	public ICarRentalCompany addCarRentalCompany(ICarRentalCompany crc) throws RemoteException; 
	

}
