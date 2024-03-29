package rental;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ManagerSession extends Session {

	public ManagerSession(IRentalServer server) {
		super(server);
	}
	
	/**
	 * Register a new CarRentalCompany with the RentalServer.
	 * 
	 * @param name
	 * 			name of the CarRentalCompany to be registered
	 * @param cars
	 * 			a list of cars contained in the CarRentalCompany
	 * @throws RemoteException
	 */
	public void registerNewCarRentalCompany(String name,
			List<Car> cars) throws RemoteException {
		ICarRentalCompany crc = new CarRentalCompany(name, cars);
		ICarRentalCompany stub = (ICarRentalCompany) UnicastRemoteObject.exportObject(crc,0);
		getServer().addCarRentalCompany(stub);
	}
	
	/**
	 * Unregisters a certain CarRentalCompany from the server.
	 * 
	 * @param name
	 * @throws RemoteException
	 */
	public void unregisterCarRentalCompany(String name) throws RemoteException{
		getServer().removeCarRentalCompany(name);
	}
	
	/**
	 * 
	 * @return all registered CarRentalCompanies
	 * @throws RemoteException
	 */
	public List<String> getAllCarRentalCompanies() throws RemoteException{
		List<String> retval = new LinkedList<>();
		for (ICarRentalCompany crc : getServer().getAllCarRentalCompanies()){
			retval.add(crc.getName());
		}
		return retval;
	}
	
	/**
	 * 
	 * @param company
	 * @return all CarTypes provided by a given CarRentalCompany
	 * @throws RemoteException
	 */
	public List<CarType> getAllCarTypes(String company) throws RemoteException{
		return (List<CarType>) getServer().getCarRentalCompany(company).getAllCarTypes();
	}
	
	/**
	 * 
	 * @param company
	 * @param carType
	 * @return the number of reservations made for 
	 * 			a given CarType at a given CarRentalCompany
	 * @throws RemoteException
	 */
	public int getNumberOfReservationsForCarType(String company, String carType) throws RemoteException{
		return getServer().getCarRentalCompany(company).getNumberOfReservationsForCarType(carType);
	}
	
	/**
	 * Returns the best client(s)
	 * 
	 * @return a list of names of the client(s) with the
	 * 			most reservations over all CarRentalCompanies
	 * @throws RemoteException
	 */
	public Set<String> getBestClients() throws RemoteException{
		Set<String> best = new HashSet<String>();
		int res = 0;
		for(ICarRentalCompany crc: getServer().getAllCarRentalCompanies()){
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
	
	/**
	 * Gets the total number of reservations (system-wide) by a particular client
	 * @param client
	 * @return
	 * @throws RemoteException
	 */
	public int getNumberOfReservationsBy(String client) throws RemoteException{
		int res = 0;
		for(ICarRentalCompany crc: getServer().getAllCarRentalCompanies()){
			res = res + crc.getReservationsBy(client).size();
		}
		return res;
	}
	
	/**
	 * Gets the most popular car type in a company
	 * @param company		Company identifier
	 * @return				CarType in company identified by company with most reservations in that company
	 * @throws RemoteException
	 */
	public CarType getMostPopularCarTypeIn(String company) throws RemoteException{
		return getServer().getCarRentalCompany(company).getMostPopularCartype();
	}
	
}
