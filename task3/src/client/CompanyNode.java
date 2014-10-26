package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.ICarRentalCompany;
import rental.ICarRentalSession;
import rental.IManagerSession;
import rental.IRentalServer;
import rental.ReservationException;

/**
 * A company node contains the code to add a new company to the cluster.
 * @author michiel
 *
 */
public class CompanyNode extends BasicClient {
	
	public CompanyNode(String name, String companyConfigFilePath) throws RemoteException, NotBoundException {
		super();
		IManagerSession manSession = server.getNewManagerSession();
		try {
			manSession.registerNewCarRentalCompany(name, loadData(companyConfigFilePath));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReservationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	/**
	 * Reads a company config file (eg: hertz.scv) and registers the company with the central server.
	 * @param datafile
	 * @return
	 * @throws ReservationException
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static List<Car> loadData(String datafile)
			throws ReservationException, NumberFormatException, IOException {

		List<Car> cars = new LinkedList<Car>();

		int nextuid = 0;
		
		//open file
		BufferedReader in = new BufferedReader(new FileReader(datafile));
		//while next line exists
		while (in.ready()) {
			//read line
			String line = in.readLine();
			//if comment: skip
			if(line.startsWith("#"))
				continue;
			//tokenize on ,
			StringTokenizer csvReader = new StringTokenizer(line, ",");
			//create new car type from first 5 fields
			CarType type = new CarType(csvReader.nextToken(),
					Integer.parseInt(csvReader.nextToken()),
					Float.parseFloat(csvReader.nextToken()),
					Double.parseDouble(csvReader.nextToken()),
					Boolean.parseBoolean(csvReader.nextToken()));
			System.out.println(type);
			//create N new cars with given type, where N is the 5th field
			for(int i = Integer.parseInt(csvReader.nextToken());i>0;i--){
				cars.add(new Car(nextuid++, type));
			}
		}
		
		return cars;
	}
}
