package rental;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class RentalServer {

	public static void main(String[] args) throws ReservationException, NumberFormatException, IOException {
		//LocateRegistry.createRegistry(1099);
		List<ICar> cars = loadData("hertz.csv");
			System.setSecurityManager(null);
			try {
				String name = "Hertz";
				ICarRentalCompany crc = new CarRentalCompany(name,cars);
				for(ICar car : cars){
					UnicastRemoteObject.exportObject(car, 0);
				}
				
				ICarRentalCompany stub = (ICarRentalCompany) UnicastRemoteObject.exportObject(crc,0);
				
				Registry registry = LocateRegistry.getRegistry();
				registry.rebind(name, stub);
				System.out.println("CarRentalCompany bound");
			}
			catch(Exception e) {
				System.err.println("Exception");
				e.printStackTrace();
				
			
		}
	}

	public static List<ICar> loadData(String datafile)
			throws ReservationException, NumberFormatException, IOException {

		List<ICar> cars = new LinkedList<ICar>();

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