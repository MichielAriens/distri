package rental;

import java.rmi.RemoteException;
import java.util.List;

public class ManagerSession extends Session implements IManagerSession{

	public ManagerSession(RentalServer server) {
		super(server);
	}

	@Override
	public ICarRentalCompany registerNewCarRentalCompany(String name,
			List<Car> cars) throws RemoteException {
		return getServer().addCarRentalCompany(new CarRentalCompany(name, cars));
	}
	
}
