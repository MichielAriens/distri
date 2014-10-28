package rental;

import java.rmi.RemoteException;
import java.util.List;

public class ManagerSession implements IManagerSession{
	
	private IRentalServer server;

	public ManagerSession(IRentalServer server) {
		this.server = server;
	}

	@Override
	public void registerNewCarRentalCompany(String name,
			List<Car> cars) throws RemoteException {
		getServer().addCarRentalCompany(new CarRentalCompany(name, cars));
	}

	@Override
	public IRentalServer getServer() throws RemoteException {
		return this.server;
	}
	
}
