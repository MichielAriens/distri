package rental;

import java.rmi.RemoteException;
import java.util.List;

public interface IManagerSession extends ISession{
	
	public void registerNewCarRentalCompany(String name, List<Car> cars) throws RemoteException;

}
