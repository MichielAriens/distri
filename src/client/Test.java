package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import rental.Car;
import rental.ICarRentalCompany;
import rental.Reservation;

public class Test {
	public static void main(String[] args){
		Test test = new Test();
		test.run();
	}
	
	public static String carRentalCompanyName = "Hertz";
	
	Registry registry;
	ICarRentalCompany crc;
	
	public Test(){
		try {
			this.registry = LocateRegistry.getRegistry("localhost", 1099);
			this.crc = (ICarRentalCompany) registry.lookup(carRentalCompanyName);			
		} catch (RemoteException e) {
			//e.printStackTrace();
			throw new RuntimeException("Could not connect to registry!");
		} catch (NotBoundException e) {
			//e.printStackTrace();
			throw new RuntimeException("The car rental company specified was not found on the registry.");
		}
	}
	
	public void run(){
		try{
			getAllCars();
		}catch (RemoteException e){
			e.printStackTrace();
		}
		
	}
	
	public void getAllCars() throws RemoteException{
		List<Car> cars = crc.getCars();
		for(Car car : cars){
			for( Reservation res : car.getReservations()){
				
			}
		}
	}
}
