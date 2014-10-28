package rental;

import java.rmi.RemoteException;
import java.util.List;

public class ManagerSession extends Session implements IManagerSession{

	public ManagerSession(RentalServer server, String client) {
		super(server,client);
	}

	@Override
	public ICarRentalCompany registerNewCarRentalCompany(String name,
			List<Car> cars) throws RemoteException {
		return getServer().addCarRentalCompany(new CarRentalCompany(name, cars));
	}
	
	

}
