package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ManagerSession extends Session {
	
	private IRentalServer server;

	public ManagerSession(IRentalServer server) {
		super(server);
	}

	public void registerNewCarRentalCompany(String name,
			List<Car> cars) throws RemoteException {
		ICarRentalCompany crc = new CarRentalCompany(name, cars);
		@SuppressWarnings("deprecation")
		ICarRentalCompany stub = (ICarRentalCompany) UnicastRemoteObject.exportObject(crc,0);
		getServer().addCarRentalCompany(stub);
	}
	
	public List<CarRentalCompany> getAllCarRentalCompanies() throws RemoteException{
		return getServer().getAllCarRentalCompanies();
	}
	
	public int getNumberOfReservationsForCarType(String company, String carType) throws RemoteException{
		return getServer().getCarRentalCompany(company).getNumberOfReservationsForCarType(carType);
	}
	
	public Set<String> getBestClients(){
		return null;
	}
	
	public int getNumberOfReservationsBy(String client) throws RemoteException{
		int res = 0;
		for(CarRentalCompany crc: getAllCarRentalCompanies()){
			res = res + crc.getReservationsBy(client).size();
		}
		return res;
	}
	
	public CarType getMostPopularCarTypeIn(String company) throws RemoteException{
		return getServer().getCarRentalCompany(company).getMostPopularCartype();
	}
	
}
