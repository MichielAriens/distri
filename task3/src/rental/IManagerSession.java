package rental;

import java.rmi.RemoteException;
import java.util.List;

public interface IManagerSession extends ISession{
	
	public ICarRentalCompany registerNewCarRentalCompany(String name, List<Car> cars) throws RemoteException;

}
