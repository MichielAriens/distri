package rental;

import java.rmi.RemoteException;
import java.util.List;

public class ManagerSession extends Session {
	
	private IRentalServer server;

	public ManagerSession(IRentalServer server) {
		super(server);
	}

	public void registerNewCarRentalCompany(String name,
			List<Car> cars) throws RemoteException {
		
		
		getServer().addCarRentalCompany(new CarRentalCompany(name, cars));
	}
	
}
