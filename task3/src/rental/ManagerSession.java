package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	public List<ICarRentalCompany> getAllCarRentalCompanies() throws RemoteException{
		return getServer().getAllCarRentalCompanies();
	}
	
	public int getNumberOfReservationsForCarType(String company, String carType) throws RemoteException{
		return getServer().getCarRentalCompany(company).getNumberOfReservationsForCarType(carType);
	}
	
	public Set<String> getBestClients() throws RemoteException{
		Set<String> best = new HashSet<String>();
		int res = 0;
		for(ICarRentalCompany crc: getAllCarRentalCompanies()){
			List<String> bestCustomers = crc.getBestCustomers();
			if(!bestCustomers.isEmpty()){		
				int numb = getNumberOfReservationsBy(bestCustomers.get(0));
				if(numb == res){
					best.addAll(bestCustomers);
				}
				if(numb>res){
					best.clear();
					best.addAll(bestCustomers);
					res = numb;
				}
			}
		}
		
		return best;
	}
	
	public int getNumberOfReservationsBy(String client) throws RemoteException{
		int res = 0;
		for(ICarRentalCompany crc: getAllCarRentalCompanies()){
			res = res + crc.getReservationsBy(client).size();
		}
		return res;
	}
	
	public CarType getMostPopularCarTypeIn(String company) throws RemoteException{
		return getServer().getCarRentalCompany(company).getMostPopularCartype();
	}
	
}
